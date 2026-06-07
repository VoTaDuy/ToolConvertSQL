package com.example.ToolConvertSQL.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/generate";

    private static final String SQL_MODEL =
            "qwen2.5-coder:7b";

    private static final String INTENT_MODEL =
            "llama3:latest";

    /**
     * Generic generation using Llama3
     * Suitable for:
     * - Intent Detection
     * - Classification
     * - Question Decomposition
     */
    public String generate(String prompt) {
        return generate(prompt, INTENT_MODEL);
    }

    /**
     * SQL Generation using Qwen Coder
     */
    public String generateSql(String question) {

        String prompt = """
                You are a MySQL expert.

                Convert the natural language question
                into a valid MySQL query.

                Rules:
                - Return ONLY SQL.
                - No markdown.
                - No explanation.
                - No comments.

                Question:
                %s
                """.formatted(question);

        return generate(prompt, SQL_MODEL);
    }

    /**
     * Internal model execution
     */
    private String generate(String prompt, String model) {

        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                        "temperature", 0,
                        "top_p", 1,
                        "num_predict", 512
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        OLLAMA_URL,
                        request,
                        Map.class
                );

        if (response.getBody() == null) {
            throw new RuntimeException(
                    "Ollama returned empty response"
            );
        }

        Object result =
                response.getBody().get("response");

        if (result == null) {
            throw new RuntimeException(
                    "Missing response field from Ollama"
            );
        }

        return result.toString().trim();
    }
}
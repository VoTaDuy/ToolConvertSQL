package com.example.ToolConvertSQL.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateSql(String question) {

        String url = "http://localhost:11434/api/generate";

        String prompt = """
                You are a MySQL expert.
                Return ONLY SQL query.
                No explanation.
                Question: %s
                """.formatted(question);

        Map<String, Object> body = Map.of(
                "model", "qwen2.5-coder:7b",
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
                restTemplate.postForEntity(url, request, Map.class);

        return response.getBody().get("response").toString();
    }
}
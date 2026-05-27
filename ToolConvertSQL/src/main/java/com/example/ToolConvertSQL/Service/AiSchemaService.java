package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import com.example.ToolConvertSQL.Service.Imp.VectorServiceImp;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiSchemaService implements AiSchemaServiceImp {

    private final RestTemplate restTemplate = new RestTemplate();

    private final SchemaService schemaService;
    private final EmbeddingService embeddingService;
    private final VectorServiceImp vectorService;
    private final RagPromptBuilder ragPromptBuilder;

    public AiSchemaService(SchemaService schemaService,
                           EmbeddingService embeddingService,
                           VectorServiceImp vectorService,
                           RagPromptBuilder ragPromptBuilder) {
        this.schemaService = schemaService;
        this.embeddingService = embeddingService;
        this.vectorService = vectorService;
        this.ragPromptBuilder = ragPromptBuilder;
    }

    public String generateSql(String question) {

        try {

            String schema = schemaService.getFullSchema();

            List<Double> embedding = embeddingService.embed(question);

            if (embedding == null) {
                return null;
            }


            List<Map<String, String>> examples =
                    vectorService.search(embedding, 5);

            String prompt = ragPromptBuilder.buildPrompt(
                    schema,
                    question,
                    examples
            );

            String response = callOllama(prompt);

            return cleanSql(response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String callOllama(String prompt) {

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> requestBody = Map.of(
                "model", "mistral",
                "prompt", prompt,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null) return null;

        return (String) response.getBody().get("response");
    }

    private String cleanSql(String raw) {

        if (raw == null) return null;

        raw = raw.trim();

        // remove markdown
        if (raw.startsWith("```")) {
            raw = raw.replace("```sql", "")
                    .replace("```", "")
                    .trim();
        }

        // remove explanation if model hallucinated
        if (raw.contains("\n")) {
            raw = raw.split("\n")[0];
        }

        if (!raw.endsWith(";")) {
            raw = raw + ";";
        }

        return raw;
    }

    public String generateRaw(String prompt) {

        try {

            String response = callOllama(prompt);

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
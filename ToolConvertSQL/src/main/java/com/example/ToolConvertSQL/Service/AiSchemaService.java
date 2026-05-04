package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiSchemaService implements AiSchemaServiceImp {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SchemaService schemaService;

    public AiSchemaService(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @Override
    public String generateSql(String question) {

        String schema = schemaService.getSchemaDescription("film_db");

        String prompt = """
                You are a professional MySQL expert.

                DATABASE SCHEMA:
                %s

                RULES:
                - Use ONLY tables and columns from schema.
                - Do NOT invent table names.
                - Return ONLY valid MySQL query.
                - No explanation.

                USER QUESTION:
                %s
                """.formatted(schema, question);

        Map<String, Object> body = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "http://localhost:11434/api/generate",
                        request,
                        Map.class
                );

        return response.getBody().get("response").toString();
    }
}
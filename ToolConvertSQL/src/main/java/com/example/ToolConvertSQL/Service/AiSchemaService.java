package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import org.springframework.http.*;
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

        if (question == null || question.isBlank()) {
            return null;
        }

        String schema = schemaService.getSchemaDescription("film_db");

        String prompt = """
You are a strict MySQL query generator.

DATABASE SCHEMA:
%s

IMPORTANT RULES:
- Use ONLY the exact table and column names shown in the schema.
- Do NOT invent columns like runtime, length, director, genre, country.
- duration column is named: duration_minutes
- director is linked via director_id
- nationality is the correct column name (NOT country)
- genre requires JOIN movie_genre and genres
- actor requires JOIN movie_actor and actors
- Return ONLY pure MySQL query.
- No explanation.
- No markdown.
- No ```sql```

USER QUESTION:
%s
""".formatted(schema, question);

        try {

            Map<String, Object> body = Map.of(
                    "model", "llama3",
                    "prompt", prompt,
                    "stream", false,
                    "temperature", 0
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

            if (response.getBody() == null ||
                    response.getBody().get("response") == null) {
                return null;
            }

            String sql = response.getBody().get("response").toString();

            return cleanSql(sql);

        } catch (Exception e) {
            System.out.println("AI generation failed: " + e.getMessage());
            return null;
        }
    }

    private String cleanSql(String sql) {

        if (sql == null) return null;

        sql = sql.replaceAll("```sql", "")
                .replaceAll("```", "");

        sql = sql.replaceAll("\\s+", " ").trim();

        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        return sql;
    }
}
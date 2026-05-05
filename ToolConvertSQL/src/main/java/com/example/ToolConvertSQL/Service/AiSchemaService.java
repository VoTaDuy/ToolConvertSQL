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

        if (question == null || question.isBlank()) return null;

        String schema = retrieveSchema(question);

        if (schema == null || schema.isBlank()) {
            schema = schemaService.getFullSchema();
        }

        String prompt = """
You are a STRICT TEXT-TO-SQL ENGINE.

====================
DATABASE SCHEMA
====================
%s

====================
HARD RULES (MUST FOLLOW)
====================

1. You MUST ONLY use tables and columns from schema above.
2. NEVER invent tables or columns.
3. movies table DOES NOT contain rating.
4. rating EXISTS ONLY in reviews table.
5. If rating is used → MUST JOIN reviews.
6. If actor is used → MUST JOIN movie_actors + actors.
7. If director is used → MUST JOIN movie_directors + directors.
8. If genre is used → MUST JOIN movie_genres + genres.

MOVIE RATING RULE:
- movies table DOES NOT store rating
- rating is derived from reviews table
- ALWAYS use AVG(r.rating) when asking about movie rating
AGGREGATION RULE:
- AVG, COUNT, SUM MUST NOT be used in WHERE
- MUST use HAVING instead
====================
JOIN MAP (MANDATORY)
====================

movies.id = reviews.movie_id
movies.id = movie_actors.movie_id
movies.id = movie_directors.movie_id
movies.id = movie_genres.movie_id

====================
OUTPUT FORMAT
====================
- ONLY SQL
- NO explanation
- NO markdown
- NO extra text
- NO semicolon required

====================
USER QUESTION
====================
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

            sql = cleanSql(sql);

            validateSql(sql);

            return sql;

        } catch (Exception e) {
            System.out.println("AI generation failed: " + e.getMessage());
            return null;
        }
    }

    public String retrieveSchema(String question) {

        if (question == null) return "";

        String q = question.toLowerCase();
        StringBuilder schema = new StringBuilder();

        schema.append(schemaService.getTable("movies")).append("\n");

        if (containsAny(q, "rating", "review", "score", "đánh giá")) {
            schema.append(schemaService.getTable("reviews")).append("\n");
        }

        if (containsAny(q, "actor", "cast", "diễn viên")) {
            schema.append(schemaService.getTable("actors")).append("\n");
            schema.append(schemaService.getTable("movie_actors")).append("\n");
        }

        if (containsAny(q, "director", "đạo diễn")) {
            schema.append(schemaService.getTable("directors")).append("\n");
            schema.append(schemaService.getTable("movie_directors")).append("\n");
        }

        if (containsAny(q, "genre", "thể loại")) {
            schema.append(schemaService.getTable("genres")).append("\n");
            schema.append(schemaService.getTable("movie_genres")).append("\n");
        }

        return schema.toString();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    private String cleanSql(String sql) {

        if (sql == null) return null;

        return sql.replaceAll("```sql", "")
                .replaceAll("```", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private void validateSql(String sql) {

        String lower = sql.toLowerCase();

        if (lower.contains("movies.rating")) {
            throw new RuntimeException("Invalid SQL: rating is not in movies table");
        }

        if (lower.contains("drop ") ||
                lower.contains("delete ") ||
                lower.contains("update ") ||
                lower.contains("insert ")) {
            throw new RuntimeException("Unsafe SQL blocked");
        }
    }
}
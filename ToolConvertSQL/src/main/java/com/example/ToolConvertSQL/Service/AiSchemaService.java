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
                
                  1. You MUST ONLY use tables and columns from the schema above.
                  2. NEVER invent tables or columns.
                  3. movies table DOES NOT contain rating.
                  4. rating EXISTS ONLY in reviews table.
                  5. If rating is used → MUST JOIN reviews table.
                  6. If actor is used → MUST JOIN movie_actors + actors.
                  7. If director is used → MUST JOIN movie_directors + directors.
                  8. If genre is used → MUST JOIN movie_genres + genres.
                
                  9. You MUST use ALL information mentioned in the user question.
                     - Every condition, filter, number, comparison, entity,
                       aggregation, or relationship MUST appear in the SQL.
                     - Do NOT ignore any constraint.
                     - If multiple entities are mentioned, SQL MUST include
                       proper JOINs and filters for all of them.
                
                  10. PROOF REQUIREMENT:
                      - If the question contains a number → SQL MUST contain that number.
                      - If it contains a comparison (>, <, =, >=, <=) → SQL MUST contain it.
                      - If it requests aggregation (average, count, total, max, min) →
                        SQL MUST use correct aggregation function.
                      - If it mentions relationships (actor, director, genre) →
                        SQL MUST use correct JOIN mapping.
                      - If any part is missing → regenerate internally before returning.
                      
                  11. DO NOT use aggregation unless explicitly required by the question.
                  12. DO NOT JOIN tables unless explicitly required by the question.
                  13. If the question does NOT mention rating → DO NOT use reviews table.
                  14. If the question does NOT mention actor → DO NOT join actors tables.
                  15. If the question does NOT mention director → DO NOT join directors tables.
                  16. If the question does NOT mention genre → DO NOT join genres tables.
                  17. ALWAYS prefix columns with table alias when multiple tables are used.
                  18. NEVER use ambiguous column names.
                  19. Only JOIN tables if required by the question.
                  20. If listing unique entities, use DISTINCT.
                  21. If filtering by director attributes (country, name), apply condition on directors table, NOT movies.
                  22. Only join movies if question explicitly involves movie information.
                  ====================
                  MOVIE RATING RULE
                  ====================
                
                  - movies table DOES NOT store rating.
                  - rating is derived from reviews table.
                  - ALWAYS use AVG(r.rating) when asking about movie rating.
                  - If filtering by rating → MUST use GROUP BY + HAVING.
                
                  ====================
                  AGGREGATION RULE
                  ====================
                
                  - AVG, COUNT, SUM, MAX, MIN MUST NOT be used in WHERE clause.
                  - Aggregated conditions MUST be placed in HAVING.
                  - When using aggregation → MUST include GROUP BY non-aggregated columns.
                
                  ====================
                  JOIN MAP (MANDATORY)
                  ====================
                
                  movies.id = reviews.movie_id
                  movies.id = movie_actors.movie_id
                  movies.id = movie_directors.movie_id
                  movies.id = movie_genres.movie_id
                
                  movie_actors.actor_id = actors.id
                  movie_directors.director_id = directors.id
                  movie_genres.genre_id = genres.id
                
                  ====================
                  SQL SAFETY RULE
                  ====================
                
                  - ONLY generate SELECT statements.
                  - DO NOT generate INSERT, UPDATE, DELETE, DROP, ALTER.
                  - No subqueries unless logically required.
                  - No semicolon at the end.
                
                  ====================
                  SELF CHECK BEFORE FINAL ANSWER
                  ====================
                
                  1. Identify all entities and constraints in the question.
                  2. For each table used in SQL:
                      - Verify it is explicitly required by the question.
                      - If not required → REMOVE it.
                  3. If aggregation appears:
                      - Confirm question explicitly requires average, count, etc.
                  4. If any extra column/table exists → regenerate internally.
                  5. Return ONLY final SQL.
                
                  ====================
                  OUTPUT FORMAT
                  ====================
                
                  - ONLY SQL
                  - NO explanation
                  - NO markdown
                  - NO comments
                  - NO extra text
                
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
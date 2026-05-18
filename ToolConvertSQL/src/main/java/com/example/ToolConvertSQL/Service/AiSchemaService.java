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
    private final SqlExecutionService sqlExecutionService;

    public AiSchemaService(SchemaService schemaService,
                           SqlExecutionService sqlExecutionService) {
        this.schemaService = schemaService;
        this.sqlExecutionService = sqlExecutionService;
    }

    @Override
    public String generateSql(String question) {

        if (question == null || question.isBlank()) return null;

        String schema = schemaService.getFullSchema();

        String sql = callLLM(buildPrompt(schema, question));

        if (sql == null) return null;

        sql = cleanSql(sql);

        // ===== Repair Loop (max 2 attempts) =====
        for (int i = 0; i < 2; i++) {

            try {
                validateSql(sql);

                // try execute (syntax + runtime validation)
                sqlExecutionService.execute(sql);

                return sql;

            } catch (Exception e) {

                String repairPrompt = """
You are fixing a SQL query.

Database schema:
%s

The following SQL failed:

%s

Error:
%s

Rules:
- Use only tables and columns from schema
- Only generate SELECT
- No explanation
- No markdown
- Return ONLY corrected SQL

Fix it.
""".formatted(schema, sql, e.getMessage());

                sql = callLLM(repairPrompt);

                if (sql == null) return null;

                sql = cleanSql(sql);
            }
        }

        return null;
    }

    // =========================
    // Prompt Builder (Simple & Strong)
    // =========================
    private String buildPrompt(String schema, String question) {

        return """
You are a TEXT-TO-SQL system.

Database schema:
%s

Rules:
- Use only tables and columns from schema
- Only generate SELECT statement
- No explanation
- No markdown
- If rating is requested → use AVG(r.rating)
- Aggregation conditions must use HAVING
- Use JOIN only when required
- Prefix columns with table alias when joining

Question:
%s
""".formatted(schema, question);
    }


    private String callLLM(String prompt) {

        try {

            Map<String, Object> body = Map.of(
                    "model", "deepseek-coder", // recommend change here
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

            return response.getBody().get("response").toString();

        } catch (Exception e) {
            System.out.println("LLM call failed: " + e.getMessage());
            return null;
        }
    }


    private String cleanSql(String sql) {

        if (sql == null) return null;

        return sql.replaceAll("```sql", "")
                .replaceAll("```", "")
                .replaceAll(";", "")
                .replaceAll("\\s+", " ")
                .trim();
    }


    private void validateSql(String sql) {

        String lower = sql.toLowerCase();

        if (!lower.startsWith("select")) {
            throw new RuntimeException("Only SELECT allowed");
        }

        if (lower.contains("drop ") ||
                lower.contains("delete ") ||
                lower.contains("update ") ||
                lower.contains("insert ") ||
                lower.contains("alter ")) {
            throw new RuntimeException("Unsafe SQL blocked");
        }

        if (lower.contains("movies.rating")) {
            throw new RuntimeException("rating is not in movies table");
        }
    }
}
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
    private final SqlExecutionService sqlExecutionService;
    private final EmbeddingService embeddingService;
    private final VectorServiceImp vectorSearchService;
    private final RagPromptBuilder ragPromptBuilder;

    public AiSchemaService(SchemaService schemaService,
                           SqlExecutionService sqlExecutionService,
                           EmbeddingService embeddingService,
                           VectorServiceImp vectorSearchService,
                           RagPromptBuilder ragPromptBuilder) {

        this.schemaService = schemaService;
        this.sqlExecutionService = sqlExecutionService;
        this.embeddingService = embeddingService;
        this.vectorSearchService = vectorSearchService;
        this.ragPromptBuilder = ragPromptBuilder;
    }

    @Override
    public String generateSql(String question) {

        if (question == null || question.isBlank()) return null;

        try {

            // 1️⃣ Get full schema
            String schema = schemaService.getFullSchema();

            // 2️⃣ Embed user question
            List<Double> queryVector = embeddingService.embed(question);

            if (queryVector == null || queryVector.isEmpty()) {
                return null;
            }

            // 3️⃣ Retrieve top 3 similar examples
            List<Map<String, String>> examples =
                    vectorSearchService.search(queryVector, 3);

            // 4️⃣ Build RAG prompt
            String prompt =
                    ragPromptBuilder.buildPrompt(schema, question, examples);

            // 5️⃣ Call LLM
            String sql = callLLM(prompt);

            if (sql == null) return null;

            sql = cleanSql(sql);

            // 6️⃣ Validate SQL
            validateSql(sql);

            // 7️⃣ Execute to ensure valid
            sqlExecutionService.execute(sql);

            return sql;

        } catch (Exception e) {
            System.out.println("RAG SQL generation failed: " + e.getMessage());
            return null;
        }
    }

    // =========================
    // LLM CALL
    // =========================
    private String callLLM(String prompt) {

        try {

            Map<String, Object> body = Map.of(
                    "model", "deepseek-coder",
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

    // =========================
    // CLEAN SQL
    // =========================
    private String cleanSql(String sql) {

        if (sql == null) return null;

        return sql.replaceAll("```sql", "")
                .replaceAll("```", "")
                .replaceAll(";", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // =========================
    // VALIDATION
    // =========================
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
            throw new RuntimeException("rating not in movies table");
        }
    }
}
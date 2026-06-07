
        package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import com.example.ToolConvertSQL.Service.Imp.SchemaSelectorServiceImp;
import com.example.ToolConvertSQL.Service.Imp.VectorServiceImp;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiSchemaService
        implements AiSchemaServiceImp {

    private final RestTemplate restTemplate =
            new RestTemplate();

    private final EmbeddingService embeddingService;

    private final VectorServiceImp vectorService;

    private final SchemaSelectorServiceImp schemaSelectorService;

    private final RagPromptBuilder ragPromptBuilder;

    public AiSchemaService(
            EmbeddingService embeddingService,
            VectorServiceImp vectorService,
            SchemaSelectorServiceImp schemaSelectorService,
            RagPromptBuilder ragPromptBuilder
    ) {

        this.embeddingService =
                embeddingService;

        this.vectorService =
                vectorService;

        this.schemaSelectorService =
                schemaSelectorService;

        this.ragPromptBuilder =
                ragPromptBuilder;
    }

    @Override
    public String generateSql(String question) {

        try {

            List<Double> questionEmbedding =
                    embeddingService.embed(question);

            if (questionEmbedding == null) {
                return null;
            }

            // RAG schema retrieval
            String relevantSchema =
                    schemaSelectorService
                            .selectRelevantSchema(
                                    question
                            );

            // retrieve similar examples
            List<Map<String, String>> examples =
                    vectorService.search(
                            questionEmbedding,
                            5
                    );

            // lightweight decomposition
            String decomposition =
                    buildSimpleDecomposition(
                            question
                    );

            // build prompt
            String prompt =
                    ragPromptBuilder.buildPrompt(
                            relevantSchema,
                            question,
                            decomposition,
                            examples
                    );

            // generate SQL
            String response =
                    callOllama(prompt);

            return cleanSql(response);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    @Override
    public String generateSqlWithSchema(
            String question,
            String schema,
            String decomposition
    ) {

        try {

            List<Double> questionEmbedding =
                    embeddingService.embed(question);

            if (questionEmbedding == null) {
                return null;
            }

            List<Map<String, String>> examples =
                    vectorService.search(
                            questionEmbedding,
                            5
                    );

            String prompt =
                    ragPromptBuilder.buildPrompt(
                            schema,
                            question,
                            decomposition,
                            examples
                    );

            String response =
                    callOllama(prompt);

            return cleanSql(response);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    private String buildSimpleDecomposition(
            String question
    ) {

        question = question.toLowerCase();

        StringBuilder sb =
                new StringBuilder();

        if (question.contains("actor")
                || question.contains("diễn viên")) {

            sb.append("- Entity: actors\n");
        }

        if (question.contains("genre")
                || question.contains("thể loại")) {

            sb.append("- Entity: genres\n");
        }

        if (question.contains("director")
                || question.contains("đạo diễn")) {

            sb.append("- Entity: directors\n");
        }

        if (question.contains("review")
                || question.contains("đánh giá")) {

            sb.append("- Entity: reviews\n");
        }

        if (question.contains("favorite")
                || question.contains("yêu thích")) {

            sb.append("- Entity: favorites\n");
        }

        if (question.contains("count")) {

            sb.append("- Aggregation: COUNT\n");
        }

        if (question.contains("average")
                || question.contains("avg")) {

            sb.append("- Aggregation: AVG\n");
        }

        if (question.contains("highest")
                || question.contains("max")) {

            sb.append("- Ordering: DESC\n");
        }

        if (sb.isEmpty()) {

            sb.append("- Basic SELECT query");
        }

        return sb.toString();
    }

    private String callOllama(String prompt) {

        String url =
                "http://localhost:11434/api/generate";

        Map<String, Object> requestBody =
                Map.of(
                        "model", "mistral",
                        "prompt", prompt,
                        "stream", false,
                        "temperature", 0
                );

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        Map.class
                );

        if (response.getBody() == null) {
            return null;
        }

        return (String)
                response.getBody()
                        .get("response");
    }

    private String cleanSql(String raw) {

        if (raw == null) {
            return null;
        }

        raw = raw.trim();

        // remove markdown
        raw = raw.replace("```sql", "")
                .replace("```", "")
                .trim();

        // find SELECT
        int selectIndex =
                raw.toUpperCase()
                        .indexOf("SELECT");

        if (selectIndex != -1) {

            raw = raw.substring(
                    selectIndex
            );
        }

        // stop at first semicolon
        int semicolonIndex =
                raw.indexOf(";");

        if (semicolonIndex != -1) {

            raw = raw.substring(
                    0,
                    semicolonIndex + 1
            );
        }

        return raw.trim();
    }

    public String generateRaw(String prompt) {

        try {

            return callOllama(prompt);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}

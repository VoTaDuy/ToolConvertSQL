package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.IntentCategory;
import com.example.ToolConvertSQL.DTO.IntentResult;
import com.example.ToolConvertSQL.Service.Imp.IntentDetectionServiceImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class IntentDetectionService implements IntentDetectionServiceImp {

    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    public IntentDetectionService(
            OllamaService ollamaService,
            ObjectMapper objectMapper
    ) {
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
    }

    @Override
    public IntentResult detect(String question) {

        try {

            String prompt = """
                    You are a semantic intent classifier.

                    Determine the category of the question.

                    Categories:

                    TEXT_TO_SQL
                    - Requires querying data from a database.

                    DATABASE_KNOWLEDGE
                    - Questions about SQL, schema, normalization,
                      primary keys, foreign keys, indexing, etc.

                    GENERAL_CHAT
                    - Greetings, casual conversation, jokes.

                    OUT_OF_SCOPE
                    - Anything unrelated.

                    Return ONLY valid JSON:

                    {
                      "category":"TEXT_TO_SQL",
                      "confidence":0.95,
                      "reason":"User requests data retrieval"
                    }

                    Question:
                    %s
                    """.formatted(question);

            String response = ollamaService.generate(prompt);

            String jsonString = extractJson(response);

            JsonNode json = objectMapper.readTree(jsonString);

            String categoryText =
                    json.path("category")
                            .asText("OUT_OF_SCOPE");

            double confidence =
                    json.path("confidence")
                            .asDouble(0.0);

            String reason =
                    json.path("reason")
                            .asText("No reason provided");

            IntentCategory category;

            try {
                category = IntentCategory.valueOf(
                        categoryText.trim().toUpperCase()
                );
            } catch (Exception e) {
                category = IntentCategory.OUT_OF_SCOPE;
            }

            IntentResult result = new IntentResult();

            result.setCategory(category);
            result.setConfidence(confidence);
            result.setReason(reason);

            result.setDatabaseQuestion(
                    category == IntentCategory.TEXT_TO_SQL
            );

            return result;

        } catch (Exception e) {

            IntentResult result = new IntentResult();

            result.setCategory(IntentCategory.OUT_OF_SCOPE);
            result.setConfidence(0.0);
            result.setReason(
                    "Intent detection failed: " + e.getMessage()
            );
            result.setDatabaseQuestion(false);

            return result;
        }
    }

    private String extractJson(String response) {

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }

        return """
                {
                    "category":"OUT_OF_SCOPE",
                    "confidence":0.0,
                    "reason":"Invalid JSON response"
                }
                """;
    }
}
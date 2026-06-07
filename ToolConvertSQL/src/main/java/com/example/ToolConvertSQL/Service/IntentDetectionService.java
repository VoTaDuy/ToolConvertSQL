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
                You are an intent classifier for a Text-to-SQL system.
                
                Database domain:
                Movie Database
                
                Tables:
                - users
                - movies
                - actors
                - directors
                - genres
                - reviews
                - favorites
                - movie_actors
                - movie_directors
                - movie_genres
                
                Classify the question into ONE category:
                
                TEXT_TO_SQL
                - User wants information that can be retrieved from the database.
                - User asks to list, show, count, find, search, display, retrieve data.
                - User mentions movies, actors, directors, genres, reviews, ratings, favorites, users.
                
                DATABASE_KNOWLEDGE
                - User asks about SQL, schema, normalization,
                  primary key, foreign key, indexing, etc.
                
                GENERAL_CHAT
                - Greetings or casual conversation.
                
                OUT_OF_SCOPE
                - Anything unrelated to the movie database.
                
                Return ONLY JSON.
                
                {
                  "category":"TEXT_TO_SQL",
                  "confidence":0.95,
                  "reason":"..."
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
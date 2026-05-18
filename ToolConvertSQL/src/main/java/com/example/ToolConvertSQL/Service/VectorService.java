package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.VectorServiceImp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorService implements VectorServiceImp {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VectorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, String>> search(List<Double> queryVector, int topK) {

        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList("SELECT question, sql_text, embedding FROM nl2sql_embeddings");

        List<Map<String, String>> scored = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            try {

                List<Double> vector =
                        objectMapper.readValue(
                                row.get("embedding").toString(),
                                new TypeReference<List<Double>>() {}
                        );

                double score = cosineSimilarity(queryVector, vector);

                Map<String, String> result = new HashMap<>();
                result.put("question", row.get("question").toString());
                result.put("sql", row.get("sql_text").toString());
                result.put("score", String.valueOf(score));

                scored.add(result);

            } catch (Exception ignored) {}
        }

        scored.sort((a, b) ->
                Double.compare(
                        Double.parseDouble(b.get("score")),
                        Double.parseDouble(a.get("score"))
                )
        );

        return scored.stream().limit(topK).toList();
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
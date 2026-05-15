package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.VectorServiceImp;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorService implements VectorServiceImp {

    private final EmbeddingService embeddingService;
    private final SchemaService schemaService;

    private final Map<String, List<Double>> vectorStore = new HashMap<>();

    public VectorService(EmbeddingService embeddingService,
                         SchemaService schemaService) {
        this.embeddingService = embeddingService;
        this.schemaService = schemaService;
        ingestSchema();
    }

    private void ingestSchema() {

        String[] tables = {
                "movies",
                "reviews",
                "actors",
                "directors",
                "genres",
                "movie_actors",
                "movie_directors",
                "movie_genres"
        };

        for (String table : tables) {
            String schemaText = schemaService.getTable(table);
            List<Double> embedding = embeddingService.embed(schemaText);
            if (embedding != null) {
                vectorStore.put(schemaText, embedding);
            }
        }

        System.out.println("Schema embedded into vector store.");
    }

    @Override
    public String retrieveRelevantSchema(String question) {

        List<Double> queryEmbedding = embeddingService.embed(question);
        if (queryEmbedding == null) {
            return schemaService.getFullSchema();
        }

        double bestScore = -1;
        String bestMatch = "";

        for (Map.Entry<String, List<Double>> entry : vectorStore.entrySet()) {

            double score = cosineSimilarity(queryEmbedding, entry.getValue());

            if (score > bestScore) {
                bestScore = score;
                bestMatch = entry.getKey();
            }
        }

        return bestMatch;
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {

        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.EmbeddingServiceImp;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService implements EmbeddingServiceImp {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Double> embed(String text) {

        Map<String, Object> body = Map.of(
                "model", "nomic-embed-text",
                "prompt", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "http://localhost:11434/api/embeddings",
                        request,
                        Map.class
                );

        if (response.getBody() == null ||
                response.getBody().get("embedding") == null) {
            return null;
        }

        return (List<Double>) response.getBody().get("embedding");
    }
}
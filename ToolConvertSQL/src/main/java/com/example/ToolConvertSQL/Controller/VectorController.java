package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.Service.EmbeddingIndexer;
import com.example.ToolConvertSQL.Service.EmbeddingService;
import com.example.ToolConvertSQL.Service.VectorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vector")
public class VectorController {

    private final EmbeddingService embeddingService;
    private final VectorService vectorService;

    public VectorController(
            EmbeddingService embeddingService,
            VectorService vectorService
    ) {
        this.embeddingService = embeddingService;
        this.vectorService = vectorService;
    }

    @PostMapping("/search")
    public List<Map<String,String>> search(
            @RequestBody Map<String,String> body){

        String question = body.get("question");

        List<Double> embedding =
                embeddingService.embed(question);

        return vectorService.search(embedding,3);
    }
}



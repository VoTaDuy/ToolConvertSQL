package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.IntentResult;
import com.example.ToolConvertSQL.Service.Imp.IntentDetectionServiceImp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intent")
public class IntentController {

    private final IntentDetectionServiceImp intentDetectionService;

    public IntentController(IntentDetectionServiceImp intentDetectionService) {
        this.intentDetectionService = intentDetectionService;
    }

    @GetMapping
    public IntentResult detect(
            @RequestParam String question
    ) {
        return intentDetectionService.detect(question);
    }
}
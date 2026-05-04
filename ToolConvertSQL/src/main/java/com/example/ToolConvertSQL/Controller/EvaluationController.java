package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.EvaluationCase;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationServiceImp evaluationService;

    public EvaluationController(EvaluationServiceImp evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/run")
    public EvaluationResult runEvaluation(
            @RequestBody List<EvaluationCase> testCases) {

        return evaluationService.evaluateAllStrategies(testCases);
    }
}
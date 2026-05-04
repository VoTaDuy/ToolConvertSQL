package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.EvaluationCase;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.Service.Imp.DatasetLoaderServiceImp;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationServiceImp evaluationService;
    private final DatasetLoaderServiceImp datasetLoaderServiceImp;

    public EvaluationController(EvaluationServiceImp evaluationService, DatasetLoaderServiceImp datasetLoaderServiceImp) {
        this.evaluationService = evaluationService;
        this.datasetLoaderServiceImp = datasetLoaderServiceImp;
    }

    @PostMapping("/run")
    public EvaluationResult runEvaluation(
            @RequestBody List<EvaluationCase> testCases) {

        return evaluationService.evaluateAllStrategies(testCases);
    }

    @PostMapping("/run-from-file")
    public EvaluationResult runFromFile() {

        List<EvaluationCase> testCases =
                datasetLoaderServiceImp.loadDataset("film_nl2sql_dataset.json");

        return evaluationService.evaluateAllStrategies(testCases);
    }
}
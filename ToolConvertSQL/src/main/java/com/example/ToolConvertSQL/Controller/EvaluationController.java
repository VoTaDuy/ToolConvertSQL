package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.DatasetItem;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.Service.Imp.DatasetLoaderServiceImp;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private  final EvaluationServiceImp evaluationService;
    @Autowired
    private final DatasetLoaderServiceImp datasetLoaderService;

    public EvaluationController(EvaluationServiceImp evaluationService,
                                DatasetLoaderServiceImp datasetLoaderService) {
        this.evaluationService = evaluationService;
        this.datasetLoaderService = datasetLoaderService;
    }


    @PostMapping("/run")
    public ResponseEntity<EvaluationResult> runEvaluation(
            @RequestBody List<DatasetItem> testCases) {

        if (testCases == null || testCases.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        EvaluationResult result =
                evaluationService.evaluateAllStrategies(testCases);

        return ResponseEntity.ok(result);
    }


    @PostMapping("/run-from-file")
    public ResponseEntity<EvaluationResult> runFromFile(
            @RequestParam(defaultValue = "movie_eval_dataset.json") String file) {

        try {
            List<DatasetItem> testCases =
                    datasetLoaderService.loadDataset(file);

            if (testCases.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            EvaluationResult result =
                    evaluationService.evaluateAllStrategies(testCases);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
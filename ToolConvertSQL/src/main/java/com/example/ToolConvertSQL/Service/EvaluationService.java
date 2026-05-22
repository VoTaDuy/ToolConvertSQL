package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.DatasetItem;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.DTO.FailedCase;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationService implements EvaluationServiceImp  {

    private final AiSchemaService aiSchemaService;
    private final JdbcTemplate jdbcTemplate;

    public EvaluationService(AiSchemaService aiSchemaService,
                             JdbcTemplate jdbcTemplate) {
        this.aiSchemaService = aiSchemaService;
        this.jdbcTemplate = jdbcTemplate;
    }


    public EvaluationResult evaluateAllStrategies(List<DatasetItem> dataset) {

        List<FailedCase> failedCases = new ArrayList<>();

        EvaluationResult result = new EvaluationResult();
        result.setTotal(dataset.size());

        for (DatasetItem item : dataset) {

            String predictedSql = aiSchemaService.generateSql(item.getQuestion());

            if (predictedSql == null || predictedSql.isBlank()) {

                FailedCase failed = new FailedCase(
                        item.getId(),
                        item.getQuestion(),
                        "",
                        item.getGroundTruthSql(),
                        "Generated SQL is null or blank"
                );

                failedCases.add(failed);

                continue;
            }

            boolean valid = isValidSQL(predictedSql);

            if (valid) {
                result.setValidSql(result.getValidSql() + 1);
            } else {

                FailedCase failed = new FailedCase(
                        item.getId(),
                        item.getQuestion(),
                        predictedSql,
                        item.getGroundTruthSql(),
                        "Invalid SQL"
                );

                failedCases.add(failed);

                continue;
            }

            try {
                List<Map<String, Object>> predicted =
                        jdbcTemplate.queryForList(predictedSql);

                List<Map<String, Object>> groundTruth =
                        jdbcTemplate.queryForList(item.getGroundTruthSql());

                if (compareResultSets(predicted, groundTruth)) {
                    result.setExecutionCorrect(result.getExecutionCorrect() + 1);
                } else {

                    FailedCase failed = new FailedCase(
                            item.getId(),
                            item.getQuestion(),
                            predictedSql,
                            item.getGroundTruthSql(),
                            "Execution result mismatch"
                    );

                    failedCases.add(failed);
                }

                System.out.println("=================================");
                System.out.println("QUESTION: " + item.getQuestion());
                System.out.println("PREDICTED SQL: " + predictedSql);
                System.out.println("GROUND TRUTH: " + item.getGroundTruthSql());

            } catch (Exception e) {

                FailedCase failed = new FailedCase(
                        item.getId(),
                        item.getQuestion(),
                        predictedSql,
                        item.getGroundTruthSql(),
                        e.getMessage()
                );

                failedCases.add(failed);

                System.out.println("=================================");
                System.out.println("FAILED QUESTION: " + item.getQuestion());
                System.out.println("PREDICTED SQL: " + predictedSql);
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        result.setFailedCases(failedCases);

        return result;
    }

    private boolean isValidSQL(String sql) {
        try {
            jdbcTemplate.queryForList(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean compareResultSets(
            List<Map<String, Object>> r1,
            List<Map<String, Object>> r2) {

        if (r1.size() != r2.size()) return false;

        return r1.containsAll(r2) && r2.containsAll(r1);
    }
}
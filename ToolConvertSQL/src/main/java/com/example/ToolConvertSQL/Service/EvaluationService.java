package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.DatasetItem;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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

        EvaluationResult result = new EvaluationResult();
        result.setTotal(dataset.size());

        for (DatasetItem item : dataset) {

            String predictedSql = aiSchemaService.generateSql(item.getQuestion());

            if (predictedSql == null || predictedSql.isBlank()) {
                continue;
            }

            // --- VALID SQL CHECK ---
            boolean valid = isValidSQL(predictedSql);

            if (valid) {
                result.setValidSql(result.getValidSql() + 1);
            } else {
                continue;
            }

            // --- EXECUTION ACCURACY ---
            try {
                List<Map<String, Object>> predicted =
                        jdbcTemplate.queryForList(predictedSql);

                List<Map<String, Object>> groundTruth =
                        jdbcTemplate.queryForList(item.getGroundTruthSql());

                if (compareResultSets(predicted, groundTruth)) {
                    result.setExecutionCorrect(result.getExecutionCorrect() + 1);
                }

            } catch (Exception ignored) {}
        }

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
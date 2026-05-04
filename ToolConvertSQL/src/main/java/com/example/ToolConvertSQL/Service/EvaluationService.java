package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.EvaluationCase;
import com.example.ToolConvertSQL.DTO.EvaluationResult;
import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import com.example.ToolConvertSQL.Service.Imp.EvaluationServiceImp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EvaluationService implements EvaluationServiceImp {

    private final QueryGenerateService ruleService;
    private final OllamaService aiService;
    private final AiSchemaServiceImp aiSchemaService;
    private final SqlExecutionService executionService;

    public EvaluationService(
            QueryGenerateService ruleService,
            OllamaService aiService,
            AiSchemaServiceImp aiSchemaService,
            SqlExecutionService executionService
    ) {
        this.ruleService = ruleService;
        this.aiService = aiService;
        this.aiSchemaService = aiSchemaService;
        this.executionService = executionService;
    }



    @Override
    public EvaluationResult evaluateAllStrategies(List<EvaluationCase> testCases) {

        if (testCases == null || testCases.isEmpty()) {
            return new EvaluationResult(0,0,0,0,"0%","0%","0%");
        }

        int ruleCorrect = 0;
        int aiCorrect = 0;
        int schemaCorrect = 0;

        for (EvaluationCase test : testCases) {

            String question = test.question();
            String expectedSql = test.expectedSql();

            if (evaluateStrategy(ruleService.generateSql(question), expectedSql)) {
                ruleCorrect++;
            }

            if (evaluateStrategy(aiService.generateSql(question), expectedSql)) {
                aiCorrect++;
            }

            if (evaluateStrategy(aiSchemaService.generateSql(question), expectedSql)) {
                schemaCorrect++;
            }
        }

        int total = testCases.size();

        return new EvaluationResult(
                total,
                ruleCorrect,
                aiCorrect,
                schemaCorrect,
                percent(ruleCorrect, total),
                percent(aiCorrect, total),
                percent(schemaCorrect, total)
        );
    }

    private boolean evaluateStrategy(String generatedSql, String expectedSql) {

        if (generatedSql == null || generatedSql.isBlank()) {
            return false;
        }

        try {

            generatedSql = cleanSql(generatedSql);
            expectedSql = cleanSql(expectedSql);

            List<Map<String, Object>> genResult =
                    executionService.execute(generatedSql);

            List<Map<String, Object>> expResult =
                    executionService.execute(expectedSql);

            return genResult.equals(expResult);

        } catch (Exception e) {
            System.out.println("Execution failed: " + e.getMessage());
            return false;
        }
    }

    private String percent(int correct, int total) {
        if (total == 0) return "0%";
        double p = (double) correct / total * 100;
        return String.format("%.2f%%", p);
    }

    private String cleanSql(String sql) {
        if (sql == null) return null;

        return sql
                .replace("```sql", "")
                .replace("```", "")
                .trim();
    }
}
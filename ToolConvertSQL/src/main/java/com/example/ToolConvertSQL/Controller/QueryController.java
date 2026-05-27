package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.QueryRequest;
import com.example.ToolConvertSQL.DTO.QueryResponse;
import com.example.ToolConvertSQL.Service.*;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/generate")
public class QueryController {

    private final QueryGenerateService ruleService;
    private final OllamaService ollamaService;
    private final SqlExecutionService sqlExecutionService;
    private final SqlSafetyService sqlSafetyService;
    private final AiSchemaServiceImp aiSchemaServiceImp;

    // NEW
    private final SchemaSelectorService schemaSelectorService;
    private final QuestionDecomposerService questionDecomposerService;
    private final SQLRefinerService sqlRefinerService;

    public QueryController(
            QueryGenerateService ruleService,
            OllamaService ollamaService,
            SqlExecutionService sqlExecutionService,
            AiSchemaServiceImp aiSchemaServiceImp,
            SqlSafetyService sqlSafetyService,

            // NEW
            SchemaSelectorService schemaSelectorService,
            QuestionDecomposerService questionDecomposerService,
            SQLRefinerService sqlRefinerService
    ) {
        this.ruleService = ruleService;
        this.ollamaService = ollamaService;
        this.sqlExecutionService = sqlExecutionService;
        this.aiSchemaServiceImp = aiSchemaServiceImp;
        this.sqlSafetyService = sqlSafetyService;

        // NEW
        this.schemaSelectorService = schemaSelectorService;
        this.questionDecomposerService = questionDecomposerService;
        this.sqlRefinerService = sqlRefinerService;
    }

    private String cleanSql(String sql) {

        if (sql == null) return null;

        sql = sql.replaceAll("```sql", "");
        sql = sql.replaceAll("```", "");
        sql = sql.trim();

        int index = sql.toLowerCase().indexOf("select");

        if (index != -1) {
            sql = sql.substring(index);
        }

        if (!sql.endsWith(";")) {
            sql += ";";
        }

        return sql;
    }

    @PostMapping("/ask")
    public QueryResponse ask(
            @RequestParam String method,
            @RequestBody QueryRequest request
    ) {

        String sql;

        try {

            String question = request.getQuestion();

            // =========================
            // MAC-SQL STYLE COMPONENTS
            // =========================

            String decomposition =
                    questionDecomposerService
                            .decompose(question);

            String filteredSchema =
                    schemaSelectorService
                            .selectRelevantSchema(question);

            System.out.println("========== DECOMPOSITION ==========");
            System.out.println(decomposition);

            System.out.println("========== FILTERED SCHEMA ==========");
            System.out.println(filteredSchema);

            // =========================
            // GENERATE SQL
            // =========================

            if ("rule".equalsIgnoreCase(method)) {

                sql = ruleService.generateSql(question);

            } else if ("ai".equalsIgnoreCase(method)) {

                sql = ollamaService.generateSql(question);

            } else if ("aiSchema".equalsIgnoreCase(method)) {

                sql = aiSchemaServiceImp.generateSql(question);

            } else {

                return new QueryResponse(
                        "Invalid method",
                        null
                );
            }

            sql = cleanSql(sql);

            System.out.println("========== GENERATED SQL ==========");
            System.out.println(sql);

            if (sql == null || sql.isBlank()) {

                return new QueryResponse(
                        "Empty SQL generated",
                        null
                );
            }

            if (!sqlSafetyService.isSafe(sql)) {

                return new QueryResponse(
                        "Unsafe SQL detected",
                        null
                );
            }

            // =========================
            // EXECUTE SQL
            // =========================

            try {

                List<Map<String, Object>> result =
                        sqlExecutionService.execute(sql);

                return new QueryResponse(sql, result);

            } catch (Exception ex) {

                System.out.println("========== SQL FAILED ==========");
                System.out.println(ex.getMessage());

                // =========================
                // SQL REFINER
                // =========================

                String refinedSql =
                        sqlRefinerService.refine(
                                question,
                                filteredSchema,
                                sql,
                                ex.getMessage()
                        );

                refinedSql = cleanSql(refinedSql);

                System.out.println("========== REFINED SQL ==========");
                System.out.println(refinedSql);

                if (!sqlSafetyService.isSafe(refinedSql)) {

                    return new QueryResponse(
                            "Unsafe refined SQL detected",
                            null
                    );
                }

                List<Map<String, Object>> refinedResult =
                        sqlExecutionService.execute(refinedSql);

                return new QueryResponse(
                        refinedSql,
                        refinedResult
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            return new QueryResponse(
                    "Execution error: " + e.getMessage(),
                    null
            );
        }
    }
}
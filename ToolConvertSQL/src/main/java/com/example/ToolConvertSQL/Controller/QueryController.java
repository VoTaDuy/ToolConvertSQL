package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.IntentCategory;
import com.example.ToolConvertSQL.DTO.IntentResult;
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

    private final SchemaSelectorService schemaSelectorService;
    private final QuestionDecomposerService questionDecomposerService;
    private final SQLRefinerService sqlRefinerService;

    // NEW
    private final IntentDetectionService intentDetectionService;

    public QueryController(
            QueryGenerateService ruleService,
            OllamaService ollamaService,
            SqlExecutionService sqlExecutionService,
            AiSchemaServiceImp aiSchemaServiceImp,
            SqlSafetyService sqlSafetyService,
            SchemaSelectorService schemaSelectorService,
            QuestionDecomposerService questionDecomposerService,
            SQLRefinerService sqlRefinerService,
            IntentDetectionService intentDetectionService
    ) {
        this.ruleService = ruleService;
        this.ollamaService = ollamaService;
        this.sqlExecutionService = sqlExecutionService;
        this.aiSchemaServiceImp = aiSchemaServiceImp;
        this.sqlSafetyService = sqlSafetyService;

        this.schemaSelectorService = schemaSelectorService;
        this.questionDecomposerService = questionDecomposerService;
        this.sqlRefinerService = sqlRefinerService;

        this.intentDetectionService = intentDetectionService;
    }

    private String cleanSql(String sql) {

        if (sql == null) return null;

        sql = sql.replace("```sql", "");
        sql = sql.replace("```", "");
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

            // =====================================
            // INTENT DETECTION GATEWAY
            // =====================================

            IntentResult intent =
                    intentDetectionService.detect(question);

            System.out.println("========== INTENT ==========");
            System.out.println("Category   : " + intent.getCategory());
            System.out.println("Confidence : " + intent.getConfidence());
            System.out.println("Reason     : " + intent.getReason());

            switch (intent.getCategory()) {

                case TEXT_TO_SQL:
                    break;

                case DATABASE_KNOWLEDGE:
                    return new QueryResponse(
                            "Rejected: DATABASE_KNOWLEDGE - "
                                    + intent.getReason(),
                            null
                    );

                case GENERAL_CHAT:
                    return new QueryResponse(
                            "Rejected: GENERAL_CHAT - "
                                    + intent.getReason(),
                            null
                    );

                case OUT_OF_SCOPE:
                    return new QueryResponse(
                            "Rejected: OUT_OF_SCOPE - "
                                    + intent.getReason(),
                            null
                    );

                default:
                    return new QueryResponse(
                            "Unknown intent category",
                            null
                    );
            }


            String decomposition =
                    questionDecomposerService.decompose(question);

            String filteredSchema =
                    schemaSelectorService
                            .selectRelevantSchema(question);

            System.out.println("========== DECOMPOSITION ==========");
            System.out.println(decomposition);

            System.out.println("========== FILTERED SCHEMA ==========");
            System.out.println(filteredSchema);

            if ("rule".equalsIgnoreCase(method)) {

                sql = ruleService.generateSql(question);

            } else if ("ai".equalsIgnoreCase(method)) {

                sql = ollamaService.generateSql(question);

            } else if ("aiSchema".equalsIgnoreCase(method)) {

                sql = aiSchemaServiceImp.generateSqlWithSchema(
                        question,
                        filteredSchema,
                        decomposition
                );

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

            try {

                List<Map<String, Object>> result =
                        sqlExecutionService.execute(sql);

                return new QueryResponse(
                        sql,
                        result
                );

            } catch (Exception ex) {

                System.out.println("========== SQL FAILED ==========");
                System.out.println(ex.getMessage());

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
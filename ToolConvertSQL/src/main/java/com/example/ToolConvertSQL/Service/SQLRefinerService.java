package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SQLRefinerServiceImp;
import org.springframework.stereotype.Service;

@Service
public class SQLRefinerService implements SQLRefinerServiceImp {

    private final AiSchemaService aiService;

    public SQLRefinerService(AiSchemaService aiService) {
        this.aiService = aiService;
    }

    @Override
    public String refine(
            String question,
            String schema,
            String sql,
            String error
    ) {

        // =====================================
        // RULE-BASED FIXES
        // =====================================

        String ruleFixedSql =
                applyRuleBasedFix(sql, error);

        if (!ruleFixedSql.equals(sql)) {

            System.out.println(
                    "========== RULE FIX APPLIED =========="
            );

            return ruleFixedSql;
        }

        // =====================================
        // LLM REFINEMENT
        // =====================================

        String prompt = buildPrompt(
                question,
                schema,
                sql,
                error
        );

        return aiService.generateSql(prompt);
    }

    private String applyRuleBasedFix(
            String sql,
            String error
    ) {

        if (sql == null || error == null) {
            return sql;
        }

        // -------------------------------------
        // MySQL:
        // Subquery returns more than 1 row
        // -------------------------------------

        if (error.contains("Subquery returns more than 1 row")) {

            return sql.replaceAll(
                    "=\\s*\\(",
                    " IN ("
            );
        }

        return sql;
    }

    private String buildPrompt(
            String question,
            String schema,
            String sql,
            String error
    ) {

        return """
                You are an expert MySQL SQL debugger.

                Original Question:
                %s

                Failed SQL:
                %s

                Database Error:
                %s

                Available Schema:
                %s

                Rules:

                - Return ONLY valid MySQL SQL.
                - No explanation.
                - No markdown.
                - Preserve the original intent.
                - Use only tables and columns that exist in schema.
                - If a subquery may return multiple rows,
                  use IN instead of =.
                - Prefer JOIN when relationships exist.
                - Fix table names if incorrect.
                - Fix column names if incorrect.
                - Fix aliases if incorrect.
                - Fix joins if missing.

                Return ONLY SQL.
                """.formatted(
                question,
                sql,
                error,
                schema
        );
    }
}
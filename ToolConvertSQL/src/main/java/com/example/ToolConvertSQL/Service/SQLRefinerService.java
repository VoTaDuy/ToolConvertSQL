package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SQLRefinerServiceImp;
import org.springframework.stereotype.Service;

@Service
public class SQLRefinerService implements SQLRefinerServiceImp {
    private final AiSchemaService aiService;

    public SQLRefinerService (AiSchemaService aiService) {
        this.aiService = aiService;
    }

    @Override
    public String refine(
            String question,
            String schema,
            String sql,
            String error
    ) {

        String prompt = buildPrompt(
                question,
                schema,
                sql,
                error
        );

        return aiService.generateSql(prompt);
    }

    private String buildPrompt(
            String question,
            String schema,
            String sql,
            String error
    ) {

        return """
                The SQL query failed.

                Question:
                %s

                Generated SQL:
                %s

                Execution Error:
                %s

                Available Schema:
                %s

                Fix the SQL query.
                Return ONLY SQL.
                """.formatted(
                question,
                sql,
                error,
                schema
        );
    }
}

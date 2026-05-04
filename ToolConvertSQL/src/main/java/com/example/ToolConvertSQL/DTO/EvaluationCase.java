package com.example.ToolConvertSQL.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EvaluationCase(
        int id,
        String difficulty,
        String question,
        String sql
) {

    public String expectedSql() {
        return sql;
    }
}
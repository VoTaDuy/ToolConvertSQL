package com.example.ToolConvertSQL.DTO;

public record EvaluationResult(
        int totalCases,
        int ruleCorrect,
        int aiCorrect,
        int schemaCorrect,
        String ruleAccuracy,
        String aiAccuracy,
        String schemaAccuracy
) {}
package com.example.ToolConvertSQL.Service.Imp;

import com.example.ToolConvertSQL.DTO.EvaluationCase;
import com.example.ToolConvertSQL.DTO.EvaluationResult;

import java.util.List;

public interface EvaluationServiceImp {
    EvaluationResult evaluateAllStrategies(List<EvaluationCase> testCases);
}

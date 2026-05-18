package com.example.ToolConvertSQL.Service.Imp;

import com.example.ToolConvertSQL.DTO.DatasetItem;
import com.example.ToolConvertSQL.DTO.EvaluationResult;

import java.util.List;

public interface EvaluationServiceImp {
    EvaluationResult evaluateAllStrategies(List<DatasetItem> testCases);
}

package com.example.ToolConvertSQL.Service.Imp;

import com.example.ToolConvertSQL.DTO.EvaluationCase;

import java.util.List;

public interface DatasetLoaderServiceImp {
    List<EvaluationCase> loadDataset(String fileName);
}

package com.example.ToolConvertSQL.Service.Imp;

import com.example.ToolConvertSQL.DTO.DatasetItem;

import java.util.List;

public interface DatasetLoaderServiceImp {
    List<DatasetItem> loadDataset(String fileName);
}

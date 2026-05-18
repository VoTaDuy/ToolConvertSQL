package com.example.ToolConvertSQL.Service.Imp;

import java.util.List;
import java.util.Map;

public interface VectorServiceImp {
    List<Map<String, String>> search(List<Double> queryVector, int topK);
}

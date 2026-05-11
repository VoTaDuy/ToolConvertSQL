package com.example.ToolConvertSQL.Service.Imp;

public interface VectorServiceImp {
    void ingestSchema();
    String retrieveRelevantSchema(String question);
}

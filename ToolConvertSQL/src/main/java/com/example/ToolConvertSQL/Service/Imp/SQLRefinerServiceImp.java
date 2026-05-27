package com.example.ToolConvertSQL.Service.Imp;

public interface SQLRefinerServiceImp {
    String refine(
            String question,
            String schema,
            String sql,
            String error
    );
}

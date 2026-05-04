package com.example.ToolConvertSQL.DTO;

public class QueryResponse {
    private String generatedSql;
    private Object result;

    public QueryResponse(String generatedSql, Object result) {
        this.generatedSql = generatedSql;
        this.result = result;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public Object getResult() {
        return result;
    }
}
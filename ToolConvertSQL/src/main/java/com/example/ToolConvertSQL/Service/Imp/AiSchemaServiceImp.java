package com.example.ToolConvertSQL.Service.Imp;

public interface AiSchemaServiceImp {
    String generateSql(String question);

    public String generateSqlWithSchema(
            String question,
            String schema,
            String decomposition
    ) ;
}

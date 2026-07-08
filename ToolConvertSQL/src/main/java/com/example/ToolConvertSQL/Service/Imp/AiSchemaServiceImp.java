package com.example.ToolConvertSQL.Service.Imp;

import java.util.List;
import java.util.Map;

public interface AiSchemaServiceImp {
    String generateSql(String question);

    public String generateSqlWithSchema(

            String question,

            String schema,

            String decomposition,

            List<Map<String,String>> examples
    );
}

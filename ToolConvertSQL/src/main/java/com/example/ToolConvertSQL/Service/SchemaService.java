package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SchemaServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SchemaService implements SchemaServiceImp {

    private final JdbcTemplate jdbcTemplate;

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getSchemaDescription(String databaseName) {

        String sql = """
                SELECT table_name, column_name, data_type
                FROM information_schema.columns
                WHERE table_schema = ?
                ORDER BY table_name;
                """;

        var rows = jdbcTemplate.queryForList(sql, databaseName);

        StringBuilder schema = new StringBuilder();

        for (Map<String, Object> row : rows) {
            schema.append("Table: ")
                    .append(row.get("table_name"))
                    .append(" | Column: ")
                    .append(row.get("column_name"))
                    .append(" | Type: ")
                    .append(row.get("data_type"))
                    .append("\n");
        }

        return schema.toString();
    }
}
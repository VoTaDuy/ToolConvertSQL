package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

@Service
public class SqlSafetyService {

    public boolean isSafe(String sql) {

        if (sql == null) return false;

        String cleaned = sql.trim().toLowerCase();

        if (!cleaned.startsWith("select")) return false;

        String[] forbidden = {
                "delete", "drop", "update",
                "insert", "alter", "truncate",
                "create"
        };

        for (String word : forbidden) {
            if (cleaned.contains(word)) {
                return false;
            }
        }

        return true;
    }
}
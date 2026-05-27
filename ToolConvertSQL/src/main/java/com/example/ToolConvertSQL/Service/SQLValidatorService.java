package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SQLValidatorServiceImp;
import org.springframework.stereotype.Service;

@Service
public class SQLValidatorService
        implements SQLValidatorServiceImp {

    @Override
    public boolean isValid(String sql) {

        if (sql == null || sql.isBlank()) {
            return false;
        }

        String lower =
                sql.toLowerCase();

        if (lower.contains("drop")
                || lower.contains("delete")
                || lower.contains("truncate")) {

            return false;
        }

        return lower.contains("select");
    }
}
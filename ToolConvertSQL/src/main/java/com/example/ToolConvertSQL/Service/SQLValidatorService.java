package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SQLValidatorServiceImp;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Service;

@Service
public class SQLValidatorService
        implements SQLValidatorServiceImp {

    @Override
    public boolean isValid(String sql) {

        try {

            Statement stmt =
                    CCJSqlParserUtil.parse(sql);

            return stmt instanceof Select;

        } catch (Exception e) {

            return false;
        }
    }
}
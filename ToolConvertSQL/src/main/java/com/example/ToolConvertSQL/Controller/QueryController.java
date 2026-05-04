package com.example.ToolConvertSQL.Controller;

import com.example.ToolConvertSQL.DTO.QueryRequest;
import com.example.ToolConvertSQL.DTO.QueryResponse;
import com.example.ToolConvertSQL.Service.*;

import com.example.ToolConvertSQL.Service.Imp.AiSchemaServiceImp;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generate")
public class QueryController {

    private final QueryGenerateService ruleService;
    private final OllamaService ollamaService;
    private final SqlExecutionService sqlExecutionService;
    private final SqlSafetyService sqlSafetyService;
    private final AiSchemaServiceImp aiSchemaServiceImp;

    public QueryController(
            QueryGenerateService ruleService,
            OllamaService ollamaService,
            SqlExecutionService sqlExecutionService,
            AiSchemaServiceImp aiSchemaServiceImp,
            SqlSafetyService sqlSafetyService
    ) {
        this.ruleService = ruleService;
        this.ollamaService = ollamaService;
        this.sqlExecutionService = sqlExecutionService;
        this.aiSchemaServiceImp = aiSchemaServiceImp;
        this.sqlSafetyService = sqlSafetyService;
    }
    private String cleanSql(String sql) {

        if (sql == null) return null;

        sql = sql.replaceAll("```", "");

        sql = sql.trim();

        int index = sql.toLowerCase().indexOf("select");
        if (index != -1) {
            sql = sql.substring(index);
        }

        return sql;
    }

    @PostMapping("/ask")
    public QueryResponse ask(@RequestParam String method,
                             @RequestBody QueryRequest request) {

        String sql;

        if ("rule".equalsIgnoreCase(method)) {
            sql = ruleService.generateSql(request.getQuestion());
        }
        else if ("ai".equalsIgnoreCase(method)) {
            sql = ollamaService.generateSql(request.getQuestion());
        } else if ("aiSchema".equalsIgnoreCase(method)) {
            sql = aiSchemaServiceImp.generateSql(request.getQuestion());
        } else {
            return new QueryResponse("Invalid method", null);
        }
        sql = cleanSql(sql);


        System.out.println("RAW SQL: " + sql);

        if (!sqlSafetyService.isSafe(sql)) {
            return new QueryResponse("Unsafe SQL detected", null);
        }
        List<Map<String, Object>> result =
                sqlExecutionService.execute(sql);

        return new QueryResponse(sql, result);
    }
}
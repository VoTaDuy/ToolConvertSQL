package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagPromptBuilder {

    public String buildPrompt(String schema,
                              String question,
                              List<Map<String, String>> examples) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
You are a strict TEXT-TO-SQL generator.

You MUST follow ALL rules strictly.

RETURN FORMAT:
- Return ONLY a single valid SQL statement.
- Do NOT add explanation.
- Do NOT add comments.
- Do NOT add markdown.
- Do NOT add extra text.
- Do NOT add semicolon explanation.
- Output must start with SELECT.
- Output must end immediately after SQL.
- Single statement only.
""");

        sb.append("\n============================\n");
        sb.append("SIMILAR EXAMPLES:\n\n");

        for (Map<String, String> ex : examples) {
            sb.append("Question: ").append(ex.get("question")).append("\n");
            sb.append("SQL: ").append(ex.get("sql")).append("\n\n");
        }

        sb.append("============================\n");
        sb.append("DATABASE SCHEMA:\n");
        sb.append(schema).append("\n\n");

        sb.append("""
============================
STRICT RULES:

1. Only SELECT statements are allowed.
2. No INSERT, UPDATE, DELETE, DROP, ALTER, TRUNCATE.
3. No comments (-- or // or /* */).
4. No explanations.
5. No natural language.
6. No multiple queries.
7. No sub text after query.
8. Use only tables and columns from schema.
9. All non-aggregated columns must appear in GROUP BY.
10. Aggregation filters must use HAVING.
11. Use correct JOIN conditions based on schema foreign keys.
12. Never invent table or column names.
13. Never use functions not supported by MySQL.
14. No window functions unless explicitly needed.
15. No invalid aliases.
16. No nested SELECT unless required by question.
17. If question asks for average rating → use AVG(r.rating)
18. If counting → use COUNT(*)
19. If grouping → include proper GROUP BY.
20. Never produce malformed SQL.
21. Prefer the simplest correct query.
22. Do not add unnecessary JOIN.
23. Do not add aggregation if question does not require it.
24. If simple SELECT satisfies question, do not use GROUP BY.

If unsure, generate the simplest correct SELECT query.

============================
USER QUESTION:
""");

        sb.append(question);

        sb.append("\n\nSQL:");

        return sb.toString();
    }
}
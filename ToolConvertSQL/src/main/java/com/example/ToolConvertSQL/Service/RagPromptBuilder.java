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
You are a PROFESSIONAL TEXT-TO-SQL ENGINE.

Your task is to convert natural language questions into VALID MySQL SELECT queries.

STRICT RULES:
1. Only generate SELECT statements.
2. Use ONLY tables and columns from the provided schema.
3. Do NOT hallucinate tables or columns.
4. Use proper JOIN conditions.
5. If filtering aggregated values → use HAVING, not WHERE.
6. If asking average rating → use AVG(r.rating) from reviews table.
7. Always use table aliases.
8. Never explain. Only output SQL.
""");

        sb.append("\n\n====================\n");
        sb.append("DATABASE SCHEMA:\n");
        sb.append(schema);
        sb.append("\n====================\n\n");

        // Add schema relationship hints (CRITICAL for accuracy)
        sb.append("""
TABLE RELATIONSHIPS:

users.id = reviews.user_id
users.id = favorites.user_id

movies.id = reviews.movie_id
movies.id = favorites.movie_id
movies.id = movie_genres.movie_id
movies.id = movie_actors.movie_id
movies.id = movie_directors.movie_id

genres.id = movie_genres.genre_id
actors.id = movie_actors.actor_id
directors.id = movie_directors.director_id
""");

        sb.append("\n====================\n\n");

        if (examples != null && !examples.isEmpty()) {
            sb.append("SIMILAR EXAMPLES:\n\n");

            for (Map<String, String> ex : examples) {
                sb.append("Question: ").append(ex.get("question")).append("\n");
                sb.append("SQL: ").append(ex.get("sql")).append("\n\n");
            }

            sb.append("====================\n\n");
        }

        sb.append("USER QUESTION:\n");
        sb.append(question);
        sb.append("\n\nSQL:");

        return sb.toString();
    }
}
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


Your task is to convert natural language questions into VALID MySQL SELECT queries.


SOME EXAMPLES:

-Example1 :
Question:
- liệt kê các phim được sản xuất tại Mỹ
                
Correct SQL:
- SELECT * FROM movies WHERE country = 'USA';
                
Reason:
- country is a direct column in movies table.
- DO NOT JOIN countries table.

-Example2 :
Question:
List movies and their genres.

SQL:
SELECT m.title, g.name FROM movies m JOIN movie_genres mg ON m.id = mg.movie_id JOIN genres g ON mg.genre_id = g.id;

Why:
movies -> movie_genres -> genres

               
IMPORTANT RULES:
- Use ONLY the exact table and column names shown in the schema.
- If JOIN creates duplicate rows for identical values, use DISTINCT.
- When listing unique names/categories/genres/actors/users → prefer SELECT DISTINCT.
- Avoid duplicate results unless the question explicitly requires all rows.
- Do NOT invent columns like runtime, length, director, genre, country.
- duration column is named: duration_minutes
- director is linked via director_id
- nationality is the correct column name (NOT country)
- genre requires JOIN movie_genre and genres
- actor requires JOIN movie_actor and actors
- Return ONLY pure MySQL query.
- No explanation.
- No markdown.
- No ```sql```

STRICT OUTPUT RULES:
- Only output final SQL
- No explanation
- No comments
- Only SELECT
- Use exact table + column names
- If filter condition exists → must include WHERE
- If join required → must include

CRITICAL INSTRUCTIONS:

Output ONLY a single valid MySQL SELECT statement.
DO NOT include explanations, comments, markdown, or code fences.
DO NOT output anything except the final SQL query.
The query MUST start with SELECT.
Use only tables and columns defined in the schema.
DO NOT invent or assume any column names.
Column names and table names must match the schema EXACTLY.
If a condition is mentioned → MUST use WHERE.
If aggregation is used → MUST use proper GROUP BY.
If filtering aggregated results → MUST use HAVING.
If sorting is required → MUST use ORDER BY.
If limiting results → MUST use LIMIT.
If relationship is required → MUST use proper JOIN with correct foreign keys.
Use explicit JOIN syntax (no implicit joins).



SCHEMA CONSTRAINTS:

duration column name: duration_minutes
nationality is the correct column name (NOT country)
Director relationship uses director_id
Genre requires JOIN movie_genres and genres
Actor requires JOIN movie_actors and actors
Reviews link via movie_id and user_id
Favorites link via movie_id and user_id



FORBIDDEN:

INSERT, UPDATE, DELETE, DROP, ALTER
Subqueries unless explicitly required
Non-MySQL syntax
Aliases that change column meaning
Selecting columns that do not exist

RETURN FORMAT:

SELECT ... FROM ... JOIN ... WHERE ... GROUP BY ... HAVING ... ORDER BY ... LIMIT ...;


Country mappings:

- Mỹ → USA
- Hoa Kỳ → USA

- Nhật → Japan
- Nhật Bản → Japan

- Hàn → Korea
- Hàn Quốc → Korea

- Trung Quốc → China
- Trung → China

- Ấn Độ → India
- Ấn → India

- Việt Nam → Vietnam
- Việt → Vietnam

- Anh → United Kingdom

- Pháp → France

- Thái Lan → Thailand

- Hồng Kông → Hong Kong

- Đài Loan → Taiwan
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
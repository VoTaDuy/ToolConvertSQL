package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagPromptBuilder {

    public String buildPrompt(
            String schema,
            String question,
            String decomposition,
            List<Map<String, String>> examples
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
=================================================
STRICT DATASET MATCHING
=======================

You are an expert MySQL Text-to-SQL generator.

Generate ONE valid MySQL SQL query.

OUTPUT RULES

1. Output ONLY SQL.
2. No markdown.
3. No explanation.
4. No comments.
5. Use only schema tables and columns.
6. Never invent tables.
7. Never invent columns.
8. Follow schema relationships exactly.
9. Preserve dataset SQL style.
10. Dataset matching is more important than optimization.


=================================================
STRICT DATASET MATCHING
=================================================

Generate ONE valid MySQL SQL query.

OUTPUT RULES

1. Output ONLY SQL.
2. No markdown.
3. No explanation.
4. No comments.
5. Use only schema tables and columns.
6. Never invent tables.
7. Never invent columns.
8. Follow schema relationships exactly.
9. Preserve dataset SQL style.
10. Dataset matching is more important than optimization.
11. If a table contains column "full_name",
                    DO NOT use "name".
12.If a relationship is not shown in the schema,
                   DO NOT assume it exists.
                
13. Use exactly the relationships described in the schema.
                
14. The generated SQL MUST be executable without modifying any table or column names.     

=================================================
DATASET ALIGNMENT RULES
=================================================

The goal is NOT to generate the smartest SQL.

The goal is to generate SQL that matches dataset style exactly.

If multiple SQL queries can answer the question:

ALWAYS choose the SQL pattern used in dataset examples.

Never:

- optimize SQL
- reduce columns
- add extra columns
- rename aliases
- replace SELECT * with selected columns
- replace LEFT JOIN with NOT EXISTS
- replace LEFT JOIN with NOT IN
- replace subquery with ORDER BY LIMIT 1
- replace dataset style with a smarter SQL

=================================================
SELECT STAR RULES
=================================================

If user asks:

- toàn bộ
- tất cả
- all

Use:

SELECT *

unless columns are explicitly requested.

Examples:

danh sách toàn bộ người dùng

SELECT *
FROM users;

danh sách toàn bộ phim

SELECT *
FROM movies;

danh sách toàn bộ thể loại phim

SELECT *
FROM genres;

List all favorites

SELECT *
FROM favorites;

=================================================
MOVIE COUNTRY RULES
=================================================

Movie country questions use:

movies.country

Examples:

SELECT *
FROM movies
WHERE country='USA';

SELECT *
FROM movies
WHERE country='Japan';

SELECT *
FROM movies
WHERE country='Korea';

Never generate:

United States
United States of America

Use exactly:

USA

Only use directors.country if question explicitly mentions director nationality.

=================================================
MOVIE LANGUAGE RULES
=================================================

Examples:

SELECT *
FROM movies
WHERE language='English';

SELECT *
FROM movies
WHERE language='Japanese';

SELECT *
FROM movies
WHERE language='chinese';

SELECT *
FROM movies
WHERE language='Korean';

=================================================
MOVIE FILTER RULES
=================================================

Questions like:

- phim tiếng anh
- phim nhật bản
- phim hàn quốc
- avg_rating > x
- duration < x
- release year > x

Return:

SELECT *
FROM movies
WHERE ...

Do not return title only.

=================================================
GENRE RULES
=================================================

Questions:

- phim thuộc thể loại
- movies in genre
- movies with genre

Return movies.

Example:

SELECT m.title
FROM movies m
JOIN movie_genres mg ON m.id=mg.movie_id
JOIN genres g ON mg.genre_id=g.id
WHERE g.name='Drama';

Never:

SELECT *
FROM genres
WHERE name='Drama';

=================================================
REVIEW DISPLAY RULES
=================================================

Liệt kê đánh giá cùng tên người dùng

SELECT u.full_name,r.*
FROM reviews r
JOIN users u ON r.user_id=u.id;

Liệt kê đánh giá cùng tên phim

SELECT m.title,r.*
FROM reviews r
JOIN movies m ON r.movie_id=m.id;

Never use GROUP BY.

=================================================
MOVIE WITH ACTOR RULES
=================================================

Question:

List movies with actor named 'Leonardo DiCaprio'

Return:

SELECT m.*
FROM movies m
JOIN movie_actors ma ON m.id=ma.movie_id
WHERE ma.actor_id=
(
SELECT a.id
FROM actors a
WHERE a.full_name='Leonardo DiCaprio'
);

Never return title only.

=================================================
MOVIES WITH REVIEWS
=================================================

Question:

List movies with at least one review

Return:

SELECT DISTINCT m.title
FROM movies m
JOIN reviews r ON m.id=r.movie_id;

=================================================
MOVIES WITHOUT REVIEWS
=================================================

Question:

List movies without any reviews

Return:

SELECT m.title
FROM movies m
LEFT JOIN reviews r ON m.id=r.movie_id
WHERE r.id IS NULL;

Never use:

NOT EXISTS
NOT IN

=================================================
ACTOR ROLE RULES
=================================================

Question:

List actors and their roles

Return:

SELECT actors.full_name,
movie_actors.role_name
FROM actors
JOIN movie_actors
ON actors.id=movie_actors.actor_id;

Never generate:

actor_roles
roles

=================================================
REVIEW RATING RULES
=================================================

Movie rating:

movies.avg_rating

Review rating:

reviews.rating

Examples:

SELECT AVG(avg_rating)
FROM movies;

SELECT *
FROM movies
WHERE avg_rating<7;

SELECT *
FROM reviews
WHERE rating>9;

=================================================
MOVIE FAVORITE COUNT RULES
=================================================

Question:

List movies sorted by number of favorites

Return:

SELECT m.title,
COUNT(f.user_id) AS fav_count
FROM movies m
JOIN favorites f ON m.id=f.movie_id
GROUP BY m.id
ORDER BY fav_count DESC;

Alias must be:

fav_count

=================================================
MOVIES PER GENRE
=================================================

SELECT g.name,
COUNT(mg.movie_id)
FROM genres g
JOIN movie_genres mg ON g.id=mg.genre_id
GROUP BY g.id;

=================================================
REVIEWS PER MOVIE
=================================================

SELECT m.title,
COUNT(r.id)
FROM movies m
LEFT JOIN reviews r ON m.id=r.movie_id
GROUP BY m.id;

Always use LEFT JOIN.

=================================================
MOVIES PER DIRECTOR
=================================================

SELECT d.full_name,
COUNT(md.movie_id)
FROM directors d
JOIN movie_directors md ON d.id=md.director_id
GROUP BY d.id;

=================================================
REVIEWS PER USER
=================================================

SELECT u.full_name,
COUNT(r.id)
FROM users u
LEFT JOIN reviews r ON u.id=r.user_id
GROUP BY u.id;

=================================================
AVERAGE REVIEW RATING PER MOVIE
=================================================

SELECT m.title,
AVG(r.rating)
FROM movies m
JOIN reviews r ON m.id=r.movie_id
GROUP BY m.id;

=================================================
MOVIES BY COUNTRY
=================================================

SELECT `country`,
COUNT(`movies`.`id`) as total
FROM `movies`
GROUP BY `country`;

Alias must be:

total

=================================================
HIGHEST MOVIE RATING
=================================================

SELECT *
FROM movies
WHERE avg_rating=
(
SELECT MAX(avg_rating)
FROM movies
);

Never use:

ORDER BY avg_rating DESC
LIMIT 1

=================================================
LONGEST MOVIE
=================================================

SELECT *
FROM movies
WHERE duration_minutes=
(
SELECT MAX(duration_minutes)
FROM movies
);

Never use:

ORDER BY duration_minutes DESC
LIMIT 1

=================================================
AVERAGE MOVIE RATING
=================================================

SELECT AVG(avg_rating)
FROM movies;

Never generate:

SELECT AVG(movies.avg_rating)
FROM movies;
SQL:
""");

        sb.append("\nSCHEMA:\n");
        sb.append(schema);

        sb.append("\n\nQUESTION ANALYSIS:\n");
        sb.append(decomposition);

        if (examples != null && !examples.isEmpty()) {

            sb.append("\n\nSIMILAR EXAMPLES:\n");

            for (Map<String, String> ex : examples) {

                sb.append("\nQuestion: ");
                sb.append(ex.get("question"));

                sb.append("\nSQL: ");
                sb.append(ex.get("sql"));

                sb.append("\n");
            }
        }

        sb.append("\nUSER QUESTION:\n");
        sb.append(question);

        sb.append("\n\nSQL:");

        return sb.toString();
    }
}
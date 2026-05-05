package com.example.ToolConvertSQL.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SchemaService {

    private final JdbcTemplate jdbcTemplate;

    private static final Set<String> ALLOWED_TABLES = Set.of(
            "movies",
            "reviews",
            "users",
            "actors",
            "directors",
            "genres",
            "movie_actors",
            "movie_directors",
            "movie_genres",
            "favorites"
    );

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== GET SINGLE TABLE SCHEMA =====
    public String getTable(String tableName) {

        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Table not allowed: " + tableName);
        }

        String sql = """
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_schema = 'movie_db'
              AND table_name = ?
            ORDER BY ordinal_position
        """;

        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(sql, tableName);

        if (rows.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        sb.append("TABLE ").append(tableName).append(" {\n");

        for (Map<String, Object> row : rows) {
            sb.append("  ")
                    .append(row.get("column_name"))
                    .append(" : ")
                    .append(row.get("data_type"))
                    .append("\n");
        }

        sb.append("}\n");

        // ===== ADD RELATION HINT (RẤT QUAN TRỌNG CHO LLM) =====
        sb.append(getRelationHint(tableName));

        return sb.toString();
    }

    // ===== RELATIONSHIP HINT FOR LLM (RAG BOOST) =====
    private String getRelationHint(String table) {

        return switch (table) {

            case "movies" -> """
            RELATIONS:
            - movies.id = reviews.movie_id
            - movies.id = movie_actors.movie_id
            - movies.id = movie_directors.movie_id
            - movies.id = movie_genres.movie_id
            """;

            case "reviews" -> """
            RELATIONS:
            - reviews.movie_id = movies.id
            - reviews.user_id = users.id
            """;

            case "actors" -> """
            RELATIONS:
            - actors.id = movie_actors.actor_id
            """;

            case "directors" -> """
            RELATIONS:
            - directors.id = movie_directors.director_id
            """;

            case "genres" -> """
            RELATIONS:
            - genres.id = movie_genres.genre_id
            """;

            default -> "";
        };
    }

    // ===== FULL SCHEMA (RAG CONTEXT) =====
    public String getFullSchema() {

        StringBuilder sb = new StringBuilder();

        for (String table : ALLOWED_TABLES) {
            sb.append(getTable(table)).append("\n");
        }

        return sb.toString();
    }

    // ===== SAFE TABLE LIST =====
    public Set<String> getAllowedTables() {
        return ALLOWED_TABLES;
    }
}
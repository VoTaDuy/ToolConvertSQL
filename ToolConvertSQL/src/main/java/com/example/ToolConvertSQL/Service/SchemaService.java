package com.example.ToolConvertSQL.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SchemaService {

    private final JdbcTemplate jdbcTemplate;

    private static final String DATABASE_NAME = "movie_db";

    private static final List<String> ALLOWED_TABLES = List.of(
            "users",
            "movies",
            "genres",
            "actors",
            "directors",
            "reviews",
            "favorites",
            "movie_genres",
            "movie_actors",
            "movie_directors"
    );

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public String getTable(String tableName) {

        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("Table not allowed: " + tableName);
        }

        String sql = """
            SELECT column_name, data_type
            FROM information_schema.columns
            WHERE table_schema = ?
              AND table_name = ?
            ORDER BY ordinal_position
        """;

        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(sql, DATABASE_NAME, tableName);

        if (rows.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        sb.append("TABLE ").append(tableName).append(" (\n");

        for (Map<String, Object> row : rows) {
            sb.append("  ")
                    .append(row.get("column_name"))
                    .append(" ")
                    .append(row.get("data_type"))
                    .append(",\n");
        }

        // remove last comma
        sb.setLength(sb.length() - 2);

        sb.append("\n)\n");

        sb.append(getRelationHint(tableName));
        sb.append("\n");

        return sb.toString();
    }


    private String getRelationHint(String table) {

        return switch (table) {

            case "movies" -> """
            RELATIONS:
              movies.id = reviews.movie_id
              movies.id = movie_actors.movie_id
              movies.id = movie_directors.movie_id
              movies.id = movie_genres.movie_id
              movies.id = favorites.movie_id
            """;

            case "reviews" -> """
            RELATIONS:
              reviews.movie_id = movies.id
              reviews.user_id = users.id
            """;

            case "movie_actors" -> """
            RELATIONS:
              movie_actors.movie_id = movies.id
              movie_actors.actor_id = actors.id
            """;

            case "movie_directors" -> """
            RELATIONS:
              movie_directors.movie_id = movies.id
              movie_directors.director_id = directors.id
            """;

            case "movie_genres" -> """
            RELATIONS:
              movie_genres.movie_id = movies.id
              movie_genres.genre_id = genres.id
            """;

            case "favorites" -> """
            RELATIONS:
              favorites.user_id = users.id
              favorites.movie_id = movies.id
            """;

            default -> "";
        };
    }


    public String getFullSchema() {

        StringBuilder sb = new StringBuilder();

        sb.append("DATABASE: ").append(DATABASE_NAME).append("\n\n");

        for (String table : ALLOWED_TABLES) {
            sb.append(getTable(table));
            sb.append("\n");
        }

        sb.append("GLOBAL RELATIONSHIPS:\n");
        sb.append("""
            reviews.user_id = users.id
            reviews.movie_id = movies.id
            favorites.user_id = users.id
            favorites.movie_id = movies.id
            movie_actors.movie_id = movies.id
            movie_actors.actor_id = actors.id
            movie_directors.movie_id = movies.id
            movie_directors.director_id = directors.id
            movie_genres.movie_id = movies.id
            movie_genres.genre_id = genres.id
        """);

        return sb.toString();
    }
    public String getTableSchema(String tableName) {

        if (!ALLOWED_TABLES.contains(tableName)) {
            return "";
        }

        return getTable(tableName);
    }

    public Set<String> getAllowedTables() {
        return Set.copyOf(ALLOWED_TABLES);
    }
}
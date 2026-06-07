package com.example.ToolConvertSQL.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class SqlExecutionService {

    private final JdbcTemplate jdbcTemplate;

    private static final List<String> ALLOWED_TABLES = List.of(
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

    public SqlExecutionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> execute(String sql) {

        validate(sql);

        return jdbcTemplate.queryForList(sql);
    }


    private void validate(String sql) {

        String lower = sql.toLowerCase();

        if (lower.contains("drop ")
                || lower.contains("delete ")
                || lower.contains("update ")
                || lower.contains("insert ")
                || lower.contains("alter ")
                || lower.contains("truncate ")) {

            throw new RuntimeException(
                    "Blocked unsafe SQL"
            );
        }

        if (!lower.trim().startsWith("select")) {

            throw new RuntimeException(
                    "Only SELECT queries are allowed"
            );
        }
    }
}
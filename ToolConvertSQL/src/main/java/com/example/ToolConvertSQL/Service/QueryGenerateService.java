package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.QueryGenerateServiceImp;
import org.springframework.stereotype.Service;


@Service
public class QueryGenerateService implements QueryGenerateServiceImp {


    @Override
    public String generateSql(String question) {
        String q = question.toLowerCase();

        if (q.contains("all movies") || q.contains("tất cả phim")) {
            return "SELECT * FROM movies";
        }

        if (q.contains("rating") && q.contains("8")) {
            return "SELECT title, rating FROM movies WHERE rating > 8";
        }

        if (q.contains("nolan") || q.contains("christopher nolan")) {
            return """
                    SELECT m.title
                    FROM movies m
                    JOIN directors d ON m.director_id = d.director_id
                    WHERE d.name = 'Christopher Nolan'
                    """;
        }

        return "SELECT title FROM movies";
    }
    
}

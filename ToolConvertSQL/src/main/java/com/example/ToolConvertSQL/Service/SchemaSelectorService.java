package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.SchemaSelectorServiceImp;
import org.springframework.stereotype.Service;

@Service
public class SchemaSelectorService
        implements SchemaSelectorServiceImp {

    private final SchemaService schemaService;

    public SchemaSelectorService(
            SchemaService schemaService
    ) {
        this.schemaService = schemaService;
    }

    @Override
    public String selectRelevantSchema(
            String question
    ) {

        String q = question.toLowerCase();

        StringBuilder schema = new StringBuilder();

        if (q.contains("user")
                || q.contains("người dùng")) {

            schema.append(
                    schemaService   .getTableSchema("users")
            );
        }

        if (q.contains("movie")
                || q.contains("phim")) {

            schema.append(
                    schemaService.getTableSchema("movies")
            );

            schema.append(
                    schemaService.getTableSchema("movie_directors")
            );

            schema.append(
                    schemaService.getTableSchema("movie_actors")
            );

            schema.append(
                    schemaService.getTableSchema("movie_genres")
            );
        }

        if (q.contains("director")
                || q.contains("đạo diễn")) {

            schema.append(
                    schemaService.getTableSchema("directors")
            );
        }

        if (q.contains("actor")
                || q.contains("diễn viên")) {

            schema.append(
                    schemaService.getTableSchema("actors")
            );
        }

        if (q.contains("genre")
                || q.contains("thể loại")) {

            schema.append(
                    schemaService.getTableSchema("genres")
            );
        }

        if (q.contains("review")
                || q.contains("đánh giá")) {

            schema.append(
                    schemaService.getTableSchema("reviews")
            );
        }

        if (schema.isEmpty()) {
            return schemaService.getFullSchema();
        }

        return schema.toString();
    }
}
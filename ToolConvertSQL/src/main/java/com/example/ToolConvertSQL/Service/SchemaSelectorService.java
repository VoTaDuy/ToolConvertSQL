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

        String fullSchema =
                schemaService.getFullSchema();

        question = question.toLowerCase();

        StringBuilder filtered =
                new StringBuilder();

        String[] lines =
                fullSchema.split("\n");

        for (String line : lines) {

            String lower =
                    line.toLowerCase();

            if (question.contains("movie")
                    && lower.contains("movie")) {

                filtered.append(line)
                        .append("\n");
            }

            if (question.contains("rating")
                    && lower.contains("rating")) {

                filtered.append(line)
                        .append("\n");
            }

            if (question.contains("actor")
                    && lower.contains("actor")) {

                filtered.append(line)
                        .append("\n");
            }

            if (question.contains("director")
                    && lower.contains("director")) {

                filtered.append(line)
                        .append("\n");
            }
        }

        if (filtered.isEmpty()) {
            return fullSchema;
        }

        return filtered.toString();
    }
}
package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.Example;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingIndexer {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;
    private final ObjectMapper mapper = new ObjectMapper();

    public EmbeddingIndexer(
            JdbcTemplate jdbcTemplate,
            EmbeddingService embeddingService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public void buildIndex() {

        List<Example> examples = jdbcTemplate.query(

                """
                SELECT id,
                       question,
                       sql_text
                FROM nl2sql_examples
                """,

                (rs, rowNum) -> new Example(
                        rs.getLong("id"),
                        rs.getString("question"),
                        rs.getString("sql_text")
                )
        );

        for (Example example : examples) {

            System.out.println("Embedding : "
                    + example.getQuestion());

            List<Double> embedding =
                    embeddingService.embed(
                            example.getQuestion()
                    );

            if (embedding == null) {
                continue;
            }

            try {

                String json =
                        mapper.writeValueAsString(
                                embedding
                        );

                jdbcTemplate.update(

                        """
                        INSERT INTO nl2sql_embeddings
                        (question,
                         sql_text,
                         embedding)
                        VALUES (?,?,?)
                        """,

                        example.getQuestion(),
                        example.getSqlText(),
                        json
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        System.out.println("========== INDEX COMPLETED ==========");
    }
}
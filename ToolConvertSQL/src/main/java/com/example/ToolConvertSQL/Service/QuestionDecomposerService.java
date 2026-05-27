package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.Service.Imp.QuestionDecomposerServiceImp;
import org.springframework.stereotype.Service;

@Service
public class QuestionDecomposerService
        implements QuestionDecomposerServiceImp {

    private final AiSchemaService aiSchemaService;

    public QuestionDecomposerService(
            AiSchemaService aiSchemaService
    ) {
        this.aiSchemaService = aiSchemaService;
    }

    @Override
    public String decompose(
            String question
    ) {

        String prompt = """
                Analyze the natural language question.

                Extract:
                - main entity
                - filters
                - aggregation
                - ordering
                - limit

                Return structured text only.

                Question:
                %s
                """.formatted(question);

        return aiSchemaService.generateRaw(prompt);
    }
}
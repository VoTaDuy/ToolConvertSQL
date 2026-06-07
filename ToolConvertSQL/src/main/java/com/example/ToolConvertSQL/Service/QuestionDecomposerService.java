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
    public String decompose(String question) {

        String prompt = """

Analyze the question.

Return ONLY:

Main entity: ...
Aggregation: ...
Ordering: ...
Limit: ...

EXAMPLES

Question:
Find movie with highest avg_rating

Output:
Main entity: movies
Aggregation: MAX
Ordering: DESC
Limit: 1

Question:
Count total number of users

Output:
Main entity: users
Aggregation: COUNT
Ordering: none
Limit: none

Question:
Calculate average review rating per movie

Output:
Main entity: reviews
Aggregation: AVG
Ordering: none
Limit: none

Question:
List movies sorted by number of favorites

Output:
Main entity: favorites
Aggregation: COUNT
Ordering: DESC
Limit: none

Question:
Find earliest release year

Output:
Main entity: movies
Aggregation: MIN
Ordering: ASC
Limit: 1

Question:
List movies with more than one genre

Output:
Main entity: movie_genres
Aggregation: COUNT
Ordering: none
Limit: none

Question:
Find movie with most reviews

Output:
Main entity: reviews
Aggregation: COUNT
Ordering: DESC
Limit: 1

Question:
List all movies

Output:
Main entity: movies
Aggregation: none
Ordering: none
Limit: none

Question:
List actors in movie with id = 1

Output:
Main entity: movie_actors
Aggregation: none
Ordering: none
Limit: none

Question:
danh sách các phim sản xuất tại hoa kỳ

Output:
Main entity: movies
Aggregation: none
Ordering: none
Limit: none

Question:
danh sách các phim sản xuất tại hàn quốc

Output:
Main entity: movies
Aggregation: none
Ordering: none
Limit: none

Question:
%s
""".formatted(question);

        return aiSchemaService.generateRaw(prompt);
    }
}
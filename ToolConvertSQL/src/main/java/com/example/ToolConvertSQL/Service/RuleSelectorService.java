package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

@Service
public class RuleSelectorService {

    public String getRules(String intent){

        return switch (intent){

            case "COUNT" -> """
                    Use COUNT
                    Use GROUP BY if needed
                    """;

            case "AVG" -> """
                    Use AVG
                    Use GROUP BY if needed
                    """;

            case "MAX" -> """
                    Use MAX aggregation
                    """;

            case "MIN" -> """
                    Use MIN aggregation
                    """;

            case "JOIN" -> """
                    Use JOIN only when relationship required
                    """;

            default -> """
                    Use SELECT
                    """;
        };
    }
}
package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

@Service
public class IntentClassifierService {

    public String classify(String question) {

        String q = question.toLowerCase();

        if(q.contains("count"))
            return "COUNT";

        if(q.contains("average")
                || q.contains("avg"))
            return "AVG";

        if(q.contains("highest")
                || q.contains("max")
                || q.contains("longest"))
            return "MAX";

        if(q.contains("minimum")
                || q.contains("earliest")
                || q.contains("min"))
            return "MIN";

        if(q.contains("and their")
                || q.contains("join"))
            return "JOIN";

        return "SELECT";
    }
}
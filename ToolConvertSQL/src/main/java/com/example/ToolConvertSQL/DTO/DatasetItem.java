package com.example.ToolConvertSQL.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatasetItem {

    private int id;
    private String question;
    @JsonProperty("ground_truth_sql")
    private String groundTruthSql;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGroundTruthSql() {
        return groundTruthSql;
    }

    public void setGroundTruthSql(String groundTruthSql) {
        this.groundTruthSql = groundTruthSql;
    }
}
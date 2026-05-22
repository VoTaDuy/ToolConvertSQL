package com.example.ToolConvertSQL.DTO;

public class FailedCase {

    private int id;
    private String question;
    private String predictedSql;
    private String groundTruthSql;
    private String error;

    public FailedCase() {
    }

    public FailedCase(int id, String question,
                      String predictedSql,
                      String groundTruthSql,
                      String error) {
        this.id = id;
        this.question = question;
        this.predictedSql = predictedSql;
        this.groundTruthSql = groundTruthSql;
        this.error = error;
    }

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

    public String getPredictedSql() {
        return predictedSql;
    }

    public void setPredictedSql(String predictedSql) {
        this.predictedSql = predictedSql;
    }

    public String getGroundTruthSql() {
        return groundTruthSql;
    }

    public void setGroundTruthSql(String groundTruthSql) {
        this.groundTruthSql = groundTruthSql;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
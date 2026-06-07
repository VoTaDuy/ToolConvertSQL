package com.example.ToolConvertSQL.DTO;

import java.util.List;

public class IntentResult {

    private boolean databaseQuestion;

    private double confidence;

    private List<String> relevantTables;

    private IntentCategory category;

    private String reason;

    public boolean isDatabaseQuestion() {
        return databaseQuestion;
    }

    public void setDatabaseQuestion(boolean databaseQuestion) {
        this.databaseQuestion = databaseQuestion;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public List<String> getRelevantTables() {
        return relevantTables;
    }

    public void setRelevantTables(List<String> relevantTables) {
        this.relevantTables = relevantTables;
    }

    public IntentCategory getCategory() {
        return category;
    }

    public void setCategory(IntentCategory category) {
        this.category = category;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
package com.example.ToolConvertSQL.DTO;

public class EvaluationResult {

    private int total;
    private int validSql;
    private int executionCorrect;

    public double getVA() {
        return (double) validSql / total;
    }

    public double getExecutionAccuracy() {
        return (double) executionCorrect / total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getValidSql() {
        return validSql;
    }

    public void setValidSql(int validSql) {
        this.validSql = validSql;
    }

    public int getExecutionCorrect() {
        return executionCorrect;
    }

    public void setExecutionCorrect(int executionCorrect) {
        this.executionCorrect = executionCorrect;
    }
}
package com.example.ToolConvertSQL.DTO;

public class Example {

    private Long id;
    private String question;
    private String sqlText;

    public Example() {
    }

    public Example(Long id, String question, String sqlText) {
        this.id = id;
        this.question = question;
        this.sqlText = sqlText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }
}
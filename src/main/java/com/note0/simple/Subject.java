package com.note0.simple;

/**
 * A POJO representing a Subject.
 * This class now includes branch and semester information for better organization and filtering.
 */
public class Subject {
    private long id;
    private String name;
    
    // New fields for the subject
    private String branch;
    private int semester;

    // --- Getters and Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}
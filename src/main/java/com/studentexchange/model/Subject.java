package com.studentexchange.model;

public class Subject {
    private int subjectId;
    private String subjectName;
    private String courseCode;
    private Integer semester;

    public Subject(int subjectId, String subjectName, String courseCode, Integer semester) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.courseCode = courseCode;
        this.semester = semester;
    }

    public int getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public String getCourseCode() { return courseCode != null ? courseCode : ""; }
    public Integer getSemester() { return semester; }

    @Override
    public String toString() {
        if (courseCode != null && !courseCode.isBlank()) {
            return subjectName + " (" + courseCode + ")";
        }
        return subjectName;
    }
}

package com.studentexchange.model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private int userId;
    private String authorName;
    private Integer subjectId;
    private String subjectName;
    private String title;
    private String body;
    private String postType;
    private String status;
    private Timestamp createdAt;

    public Post(int postId, int userId, String authorName, Integer subjectId,
                String subjectName, String title, String body, String postType,
                String status, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.title = title;
        this.body = body;
        this.postType = postType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getPostId() { return postId; }
    public int getUserId() { return userId; }
    public String getAuthorName() { return authorName; }
    public Integer getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName != null ? subjectName : "General"; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getPostType() { return postType; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}

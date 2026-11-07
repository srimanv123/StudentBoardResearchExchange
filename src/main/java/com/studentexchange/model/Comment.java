package com.studentexchange.model;

import java.sql.Timestamp;

public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String authorName;
    private Integer parentCommentId;
    private String commentText;
    private String status;
    private Timestamp createdAt;

    public Comment(int commentId, int postId, int userId, String authorName,
                   Integer parentCommentId, String commentText, String status,
                   Timestamp createdAt) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.parentCommentId = parentCommentId;
        this.commentText = commentText;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getCommentId() { return commentId; }
    public int getPostId() { return postId; }
    public int getUserId() { return userId; }
    public String getAuthorName() { return authorName; }
    public Integer getParentCommentId() { return parentCommentId; }
    public boolean isReply() { return parentCommentId != null; }
    public String getCommentText() { return commentText; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}

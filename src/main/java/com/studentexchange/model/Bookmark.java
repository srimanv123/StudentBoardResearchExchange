package com.studentexchange.model;

import java.sql.Timestamp;

public class Bookmark {
    private int bookmarkId;
    private int userId;
    private int resourceId;
    private String resourceTitle;
    private String description;
    private String resourceType;
    private String resourceUrl;
    private String subjectName;
    private String uploaderName;
    private Timestamp createdAt;

    public Bookmark(int bookmarkId, int userId, int resourceId,
                    String resourceTitle, String description,
                    String resourceType, String resourceUrl,
                    String subjectName, String uploaderName,
                    Timestamp createdAt) {
        this.bookmarkId = bookmarkId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceTitle = resourceTitle;
        this.description = description;
        this.resourceType = resourceType;
        this.resourceUrl = resourceUrl;
        this.subjectName = subjectName;
        this.uploaderName = uploaderName;
        this.createdAt = createdAt;
    }

    public int getBookmarkId() { return bookmarkId; }
    public int getUserId() { return userId; }
    public int getResourceId() { return resourceId; }
    public String getResourceTitle() { return resourceTitle; }
    public String getDescription() { return description != null ? description : ""; }
    public String getResourceType() { return resourceType; }
    public String getResourceUrl() { return resourceUrl != null ? resourceUrl : ""; }
    public String getSubjectName() { return subjectName != null ? subjectName : "General"; }
    public String getUploaderName() { return uploaderName; }
    public Timestamp getCreatedAt() { return createdAt; }
}

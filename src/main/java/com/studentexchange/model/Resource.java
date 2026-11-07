package com.studentexchange.model;

import java.sql.Timestamp;

public class Resource {
    private int resourceId;
    private int userId;
    private String uploaderName;
    private Integer subjectId;
    private String subjectName;
    private String title;
    private String description;
    private String resourceType;
    private String resourceUrl;
    private String status;
    private Timestamp createdAt;

    public Resource(int resourceId, int userId, String uploaderName,
                    Integer subjectId, String subjectName, String title,
                    String description, String resourceType, String resourceUrl,
                    String status, Timestamp createdAt) {
        this.resourceId = resourceId;
        this.userId = userId;
        this.uploaderName = uploaderName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.title = title;
        this.description = description;
        this.resourceType = resourceType;
        this.resourceUrl = resourceUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getResourceId() { return resourceId; }
    public int getUserId() { return userId; }
    public String getUploaderName() { return uploaderName; }
    public Integer getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName != null ? subjectName : "General"; }
    public String getTitle() { return title; }
    public String getDescription() { return description != null ? description : ""; }
    public String getResourceType() { return resourceType; }
    public String getResourceUrl() { return resourceUrl != null ? resourceUrl : ""; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}

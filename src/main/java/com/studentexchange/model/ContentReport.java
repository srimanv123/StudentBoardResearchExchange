package com.studentexchange.model;

import java.sql.Timestamp;

public class ContentReport {
    private int reportId;
    private int reporterId;
    private String reporterName;
    private Integer postId;
    private String postTitle;
    private Integer resourceId;
    private String resourceTitle;
    private String reason;
    private String reportStatus;
    private Timestamp createdAt;

    public ContentReport(int reportId, int reporterId, String reporterName,
                         Integer postId, String postTitle,
                         Integer resourceId, String resourceTitle,
                         String reason, String reportStatus,
                         Timestamp createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.postId = postId;
        this.postTitle = postTitle;
        this.resourceId = resourceId;
        this.resourceTitle = resourceTitle;
        this.reason = reason;
        this.reportStatus = reportStatus;
        this.createdAt = createdAt;
    }

    public int getReportId() { return reportId; }
    public int getReporterId() { return reporterId; }
    public String getReporterName() { return reporterName; }
    public Integer getPostId() { return postId; }
    public String getPostTitle() { return postTitle; }
    public Integer getResourceId() { return resourceId; }
    public String getResourceTitle() { return resourceTitle; }
    public String getReason() { return reason; }
    public String getReportStatus() { return reportStatus; }
    public Timestamp getCreatedAt() { return createdAt; }

    public String getTargetType() {
        if (postId != null) return "Discussion Post";
        if (resourceId != null) return "Study Resource";
        return "Unknown";
    }

    public String getTargetTitle() {
        if (postTitle != null && !postTitle.isBlank()) return postTitle;
        if (resourceTitle != null && !resourceTitle.isBlank()) return resourceTitle;
        return "N/A";
    }
}

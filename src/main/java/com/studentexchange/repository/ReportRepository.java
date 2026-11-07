package com.studentexchange.repository;

import com.studentexchange.model.ContentReport;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    // Check whether a user is an active student or admin.
    private boolean isActiveUser(Connection connection, int userId)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM users
                WHERE user_id = ?
                  AND account_status = 'ACTIVE'
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    // Check whether a user is an active admin.
    private boolean isActiveAdmin(Connection connection, int adminId)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM users
                WHERE user_id = ?
                  AND role = 'ADMIN'
                  AND account_status = 'ACTIVE'
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, adminId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    // Report a post.
    public boolean reportPost(
            int reporterId,
            int postId,
            String reason) {

        if (reason == null || reason.isBlank()) {
            System.err.println("Report reason cannot be empty.");
            return false;
        }

        String sql = """
                INSERT INTO content_reports
                (reporter_id, post_id, resource_id, reason, report_status)
                SELECT ?, p.post_id, NULL, ?, 'PENDING'
                FROM posts p
                WHERE p.post_id = ?
                  AND p.status = 'ACTIVE'
                """;

        try (Connection connection = DBConnection.getConnection()) {

            if (!isActiveUser(connection, reporterId)) {
                System.err.println(
                        "Only active users can report content."
                );
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, reporterId);
                statement.setString(2, reason.trim());
                statement.setInt(3, postId);

                return statement.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.err.println("Error reporting post: " + e.getMessage());
            return false;
        }
    }

    // Report a resource.
    public boolean reportResource(
            int reporterId,
            int resourceId,
            String reason) {

        if (reason == null || reason.isBlank()) {
            System.err.println("Report reason cannot be empty.");
            return false;
        }

        String sql = """
                INSERT INTO content_reports
                (reporter_id, post_id, resource_id, reason, report_status)
                SELECT ?, NULL, r.resource_id, ?, 'PENDING'
                FROM resources r
                WHERE r.resource_id = ?
                  AND r.status = 'ACTIVE'
                """;

        try (Connection connection = DBConnection.getConnection()) {

            if (!isActiveUser(connection, reporterId)) {
                System.err.println(
                        "Only active users can report content."
                );
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, reporterId);
                statement.setString(2, reason.trim());
                statement.setInt(3, resourceId);

                return statement.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.err.println("Error reporting resource: " + e.getMessage());
            return false;
        }
    }

    // Display pending reports. Only active admins may view the queue.
    public void displayPendingReports(int adminId) {
        List<ContentReport> list = getPendingReports(adminId);
        if (list.isEmpty()) {
            System.out.println("No pending reports found.");
            return;
        }

        for (ContentReport cr : list) {
            System.out.println("--------------------------------");
            System.out.println("Report ID: " + cr.getReportId());
            System.out.println("Reporter: " + cr.getReporterName());
            System.out.println("Reason: " + cr.getReason());
            System.out.println("Post: " + cr.getPostTitle());
            System.out.println("Resource: " + cr.getResourceTitle());
            System.out.println("Status: " + cr.getReportStatus());
            System.out.println("Created: " + cr.getCreatedAt());
        }
    }

    public List<ContentReport> getPendingReports(int adminId) {
        List<ContentReport> reports = new ArrayList<>();
        String sql = """
                SELECT cr.report_id, cr.reporter_id, cr.post_id, cr.resource_id,
                       cr.reason, cr.report_status, cr.created_at,
                       u.name AS reporter,
                       p.title AS post_title,
                       r.title AS resource_title
                FROM content_reports cr
                JOIN users u ON cr.reporter_id = u.user_id
                LEFT JOIN posts p ON cr.post_id = p.post_id
                LEFT JOIN resources r ON cr.resource_id = r.resource_id
                WHERE cr.report_status = 'PENDING'
                ORDER BY cr.created_at ASC, cr.report_id ASC
                """;

        try (Connection connection = DBConnection.getConnection()) {

            if (!isActiveAdmin(connection, adminId)) {
                System.err.println(
                        "Access denied: an active admin account is required."
                );
                return reports;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    int pVal = result.getInt("post_id");
                    Integer postId = result.wasNull() ? null : pVal;

                    int rVal = result.getInt("resource_id");
                    Integer resourceId = result.wasNull() ? null : rVal;

                    reports.add(new ContentReport(
                            result.getInt("report_id"),
                            result.getInt("reporter_id"),
                            result.getString("reporter"),
                            postId,
                            result.getString("post_title"),
                            resourceId,
                            result.getString("resource_title"),
                            result.getString("reason"),
                            result.getString("report_status"),
                            result.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error displaying reports: " + e.getMessage());
        }

        return reports;
    }

    public int getPendingReportCount(int adminId) {
        return getPendingReports(adminId).size();
    }

    // Review a report, optionally hiding the reported content.
    public boolean reviewReport(
            int reportId,
            int adminId,
            String decision,
            boolean hideContent) {

        if (!"REVIEWED".equals(decision)
                && !"DISMISSED".equals(decision)
                && !"ACTION_TAKEN".equals(decision)) {

            System.err.println("Invalid report decision.");
            return false;
        }

        if (hideContent && !"ACTION_TAKEN".equals(decision)) {
            System.err.println(
                    "Content can only be hidden when decision is ACTION_TAKEN."
            );
            return false;
        }

        String selectSql = """
                SELECT post_id, resource_id
                FROM content_reports
                WHERE report_id = ?
                  AND report_status = 'PENDING'
                FOR UPDATE
                """;

        String updateReportSql = """
                UPDATE content_reports
                SET report_status = ?,
                    reviewed_by = ?,
                    reviewed_at = CURRENT_TIMESTAMP
                WHERE report_id = ?
                  AND report_status = 'PENDING'
                """;

        String hidePostSql = """
                UPDATE posts
                SET status = 'HIDDEN'
                WHERE post_id = ?
                """;

        String hideResourceSql = """
                UPDATE resources
                SET status = 'HIDDEN'
                WHERE resource_id = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                if (!isActiveAdmin(connection, adminId)) {
                    connection.rollback();
                    System.err.println(
                            "Access denied: an active admin account is required."
                    );
                    return false;
                }

                Integer postId = null;
                Integer resourceId = null;

                try (PreparedStatement select =
                             connection.prepareStatement(selectSql)) {

                    select.setInt(1, reportId);

                    try (ResultSet result = select.executeQuery()) {

                        if (!result.next()) {
                            connection.rollback();
                            System.err.println(
                                    "Report not found or already reviewed."
                            );
                            return false;
                        }

                        int value = result.getInt("post_id");
                        if (!result.wasNull()) {
                            postId = value;
                        }

                        value = result.getInt("resource_id");
                        if (!result.wasNull()) {
                            resourceId = value;
                        }
                    }
                }

                if (hideContent) {
                    if (postId != null) {
                        try (PreparedStatement hide =
                                     connection.prepareStatement(hidePostSql)) {
                            hide.setInt(1, postId);
                            hide.executeUpdate();
                        }
                    } else if (resourceId != null) {
                        try (PreparedStatement hide =
                                     connection.prepareStatement(hideResourceSql)) {
                            hide.setInt(1, resourceId);
                            hide.executeUpdate();
                        }
                    }
                }

                try (PreparedStatement update =
                             connection.prepareStatement(updateReportSql)) {

                    update.setString(1, decision);
                    update.setInt(2, adminId);
                    update.setInt(3, reportId);

                    if (update.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error reviewing report: " + e.getMessage());
            return false;
        }
    }
}

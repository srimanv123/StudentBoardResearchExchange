package com.studentexchange.repository;

import com.studentexchange.model.Resource;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceRepository {

    public boolean addResource(
            int userId,
            Integer subjectId,
            String title,
            String description,
            String resourceType,
            String resourceUrl) {

        String sql = """
                INSERT INTO resources
                (user_id, subject_id, title, description, resource_type,
                 file_path, resource_url, status)
                VALUES (?, ?, ?, ?, ?, NULL, ?, 'ACTIVE')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            if (subjectId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, subjectId);
            }

            statement.setString(3, title);
            statement.setString(4, description);
            statement.setString(5, resourceType);
            statement.setString(6, resourceUrl);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error adding resource: " + e.getMessage());
            return false;
        }
    }

    public void displayActiveResources() {
        for (Resource r : getActiveResources()) {
            System.out.println("--------------------------------");
            System.out.println("Resource ID: " + r.getResourceId());
            System.out.println("Title: " + r.getTitle());
            System.out.println("Description: " + r.getDescription());
            System.out.println("Type: " + r.getResourceType());
            System.out.println("Uploader: " + r.getUploaderName());
            System.out.println("Subject: " + r.getSubjectName());
            System.out.println("URL: " + r.getResourceUrl());
            System.out.println("Created: " + r.getCreatedAt());
        }
    }

    public void displayResourcesBySubject(int subjectId) {
        for (Resource r : getResourcesBySubject(subjectId)) {
            System.out.println(
                    r.getResourceId() + " | " +
                    r.getTitle() + " | " +
                    r.getResourceType() + " | " +
                    r.getUploaderName()
            );
        }
    }

    public List<Resource> getActiveResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = """
                SELECT r.resource_id, r.user_id, r.subject_id, r.title,
                       r.description, r.resource_type, r.resource_url,
                       r.status, r.created_at,
                       u.name AS uploader, s.subject_name
                FROM resources r
                JOIN users u ON r.user_id = u.user_id
                LEFT JOIN subjects s ON r.subject_id = s.subject_id
                WHERE r.status = 'ACTIVE'
                ORDER BY r.created_at DESC, r.resource_id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                int subjectId = result.getInt("subject_id");
                Integer subId = result.wasNull() ? null : subjectId;

                resources.add(new Resource(
                        result.getInt("resource_id"),
                        result.getInt("user_id"),
                        result.getString("uploader"),
                        subId,
                        result.getString("subject_name"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getString("resource_type"),
                        result.getString("resource_url"),
                        result.getString("status"),
                        result.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving active resources: " + e.getMessage());
        }

        return resources;
    }

    public List<Resource> getResourcesBySubject(int subjectId) {
        List<Resource> resources = new ArrayList<>();
        String sql = """
                SELECT r.resource_id, r.user_id, r.subject_id, r.title,
                       r.description, r.resource_type, r.resource_url,
                       r.status, r.created_at,
                       u.name AS uploader, s.subject_name
                FROM resources r
                JOIN users u ON r.user_id = u.user_id
                LEFT JOIN subjects s ON r.subject_id = s.subject_id
                WHERE r.status = 'ACTIVE' AND r.subject_id = ?
                ORDER BY r.created_at DESC, r.resource_id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    resources.add(new Resource(
                            result.getInt("resource_id"),
                            result.getInt("user_id"),
                            result.getString("uploader"),
                            result.getInt("subject_id"),
                            result.getString("subject_name"),
                            result.getString("title"),
                            result.getString("description"),
                            result.getString("resource_type"),
                            result.getString("resource_url"),
                            result.getString("status"),
                            result.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error filtering resources by subject: " + e.getMessage());
        }

        return resources;
    }

    public int getActiveResourceCount() {
        String sql = "SELECT COUNT(*) FROM resources WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Could not count resources: " + e.getMessage());
        }
        return 0;
    }
}

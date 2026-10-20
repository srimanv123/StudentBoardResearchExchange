
package com.studentexchange.repository;

import com.studentexchange.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = """
                SELECT r.resource_id, r.title, r.description,
                       r.resource_type, r.resource_url, r.created_at,
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

            boolean found = false;

            while (result.next()) {
                found = true;

                System.out.println("--------------------------------");
                System.out.println("Resource ID: " + result.getInt("resource_id"));
                System.out.println("Title: " + result.getString("title"));
                System.out.println("Description: " + result.getString("description"));
                System.out.println("Type: " + result.getString("resource_type"));
                System.out.println("Uploader: " + result.getString("uploader"));
                System.out.println("Subject: " + result.getString("subject_name"));
                System.out.println("URL: " + result.getString("resource_url"));
                System.out.println("Created: " + result.getTimestamp("created_at"));
            }

            if (!found) {
                System.out.println("No active resources found.");
            }

        } catch (SQLException e) {
            System.err.println("Error displaying resources: " + e.getMessage());
        }
    }

    public void displayResourcesBySubject(int subjectId) {
        String sql = """
                SELECT r.resource_id, r.title, r.description,
                       r.resource_type, r.resource_url, r.created_at,
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
                boolean found = false;

                while (result.next()) {
                    found = true;

                    System.out.println("--------------------------------");
                    System.out.println("Resource ID: " + result.getInt("resource_id"));
                    System.out.println("Title: " + result.getString("title"));
                    System.out.println("Description: " + result.getString("description"));
                    System.out.println("Type: " + result.getString("resource_type"));
                    System.out.println("Uploader: " + result.getString("uploader"));
                    System.out.println("Subject: " + result.getString("subject_name"));
                    System.out.println("URL: " + result.getString("resource_url"));
                }

                if (!found) {
                    System.out.println("No resources found for this subject.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching resources: " + e.getMessage());
        }
    }
}

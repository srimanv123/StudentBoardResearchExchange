
package com.studentexchange.repository;

import com.studentexchange.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookmarkRepository {

    public boolean addBookmark(int userId, int resourceId) {
        String sql = """
                INSERT INTO bookmarks (user_id, resource_id)
                SELECT ?, r.resource_id
                FROM resources r
                WHERE r.resource_id = ? AND r.status = 'ACTIVE'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, resourceId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error adding bookmark: " + e.getMessage());
            return false;
        }
    }

    public boolean removeBookmark(int userId, int resourceId) {
        String sql = """
                DELETE FROM bookmarks
                WHERE user_id = ? AND resource_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, resourceId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error removing bookmark: " + e.getMessage());
            return false;
        }
    }

    public void displayBookmarks(int userId) {
        String sql = """
                SELECT b.bookmark_id, r.resource_id, r.title,
                       r.description, r.resource_type, r.resource_url,
                       s.subject_name, u.name AS uploader, b.created_at
                FROM bookmarks b
                JOIN resources r ON b.resource_id = r.resource_id
                LEFT JOIN subjects s ON r.subject_id = s.subject_id
                JOIN users u ON r.user_id = u.user_id
                WHERE b.user_id = ? AND r.status = 'ACTIVE'
                ORDER BY b.created_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                boolean found = false;

                while (result.next()) {
                    found = true;

                    System.out.println("--------------------------------");
                    System.out.println("Bookmark ID: " + result.getInt("bookmark_id"));
                    System.out.println("Resource ID: " + result.getInt("resource_id"));
                    System.out.println("Title: " + result.getString("title"));
                    System.out.println("Description: " + result.getString("description"));
                    System.out.println("Type: " + result.getString("resource_type"));
                    System.out.println("Subject: " + result.getString("subject_name"));
                    System.out.println("Uploader: " + result.getString("uploader"));
                    System.out.println("URL: " + result.getString("resource_url"));
                    System.out.println("Bookmarked: " + result.getTimestamp("created_at"));
                }

                if (!found) {
                    System.out.println("No bookmarks found.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error displaying bookmarks: " + e.getMessage());
        }
    }
}

package com.studentexchange.repository;

import com.studentexchange.model.Bookmark;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public boolean isBookmarked(int userId, int resourceId) {
        String sql = "SELECT 1 FROM bookmarks WHERE user_id = ? AND resource_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, resourceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking bookmark: " + e.getMessage());
            return false;
        }
    }

    public void displayBookmarks(int userId) {
        List<Bookmark> list = getUserBookmarks(userId);
        if (list.isEmpty()) {
            System.out.println("No bookmarks found.");
            return;
        }
        for (Bookmark b : list) {
            System.out.println("--------------------------------");
            System.out.println("Bookmark ID: " + b.getBookmarkId());
            System.out.println("Resource ID: " + b.getResourceId());
            System.out.println("Title: " + b.getResourceTitle());
            System.out.println("Description: " + b.getDescription());
            System.out.println("Type: " + b.getResourceType());
            System.out.println("Subject: " + b.getSubjectName());
            System.out.println("Uploader: " + b.getUploaderName());
            System.out.println("URL: " + b.getResourceUrl());
            System.out.println("Bookmarked: " + b.getCreatedAt());
        }
    }

    public List<Bookmark> getUserBookmarks(int userId) {
        List<Bookmark> bookmarks = new ArrayList<>();
        String sql = """
                SELECT b.bookmark_id, b.user_id, b.resource_id, b.created_at,
                       r.title, r.description, r.resource_type, r.resource_url,
                       s.subject_name, u.name AS uploader
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
                while (result.next()) {
                    bookmarks.add(new Bookmark(
                            result.getInt("bookmark_id"),
                            result.getInt("user_id"),
                            result.getInt("resource_id"),
                            result.getString("title"),
                            result.getString("description"),
                            result.getString("resource_type"),
                            result.getString("resource_url"),
                            result.getString("subject_name"),
                            result.getString("uploader"),
                            result.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error displaying bookmarks: " + e.getMessage());
        }

        return bookmarks;
    }

    public int getBookmarkCount(int userId) {
        String sql = """
                SELECT COUNT(*)
                FROM bookmarks b
                JOIN resources r ON b.resource_id = r.resource_id
                WHERE b.user_id = ? AND r.status = 'ACTIVE'
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bookmarks: " + e.getMessage());
        }
        return 0;
    }
}

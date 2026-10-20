
package com.studentexchange.repository;

import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostRepository {

    public boolean createPost(
            int userId,
            Integer subjectId,
            String title,
            String body,
            String postType) {

        String sql = """
                INSERT INTO posts
                (user_id, subject_id, title, body, post_type)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            if (subjectId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, subjectId);
            }

            statement.setString(3, title);
            statement.setString(4, body);
            statement.setString(5, postType);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Could not create post: " + e.getMessage());
            return false;
        }
    }

    public void displayActivePosts() {

        String sql = """
                SELECT p.post_id, p.title, p.body,
                       p.post_type, u.name AS author,
                       s.subject_name, p.created_at
                FROM posts p
                JOIN users u ON p.user_id = u.user_id
                LEFT JOIN subjects s ON p.subject_id = s.subject_id
                WHERE p.status = 'ACTIVE'
                ORDER BY p.created_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAvailable Discussion Posts");

            while (resultSet.next()) {
                System.out.println("------------------------------");
                System.out.println("Post ID: " + resultSet.getInt("post_id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Body: " + resultSet.getString("body"));
                System.out.println("Type: " + resultSet.getString("post_type"));
                System.out.println("Author: " + resultSet.getString("author"));
                System.out.println("Subject: " +
                        resultSet.getString("subject_name"));
                System.out.println("Created: " +
                        resultSet.getTimestamp("created_at"));
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve posts: " + e.getMessage());
        }
    }

    public void displayPostsBySubject(int subjectId) {

        String sql = """
                SELECT p.post_id, p.title, p.body,
                       p.post_type, u.name AS author,
                       s.subject_name
                FROM posts p
                JOIN users u ON p.user_id = u.user_id
                LEFT JOIN subjects s ON p.subject_id = s.subject_id
                WHERE p.status = 'ACTIVE'
                  AND p.subject_id = ?
                ORDER BY p.created_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println(
                            resultSet.getInt("post_id") + " | " +
                            resultSet.getString("title") + " | " +
                            resultSet.getString("author") + " | " +
                            resultSet.getString("subject_name")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not filter posts: " + e.getMessage());
        }
    }
}

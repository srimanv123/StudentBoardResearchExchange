package com.studentexchange.repository;

import com.studentexchange.model.Post;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        for (Post p : getActivePosts()) {
            System.out.println("------------------------------");
            System.out.println("Post ID: " + p.getPostId());
            System.out.println("Title: " + p.getTitle());
            System.out.println("Body: " + p.getBody());
            System.out.println("Type: " + p.getPostType());
            System.out.println("Author: " + p.getAuthorName());
            System.out.println("Subject: " + p.getSubjectName());
            System.out.println("Created: " + p.getCreatedAt());
        }
    }

    public void displayPostsBySubject(int subjectId) {
        for (Post p : getPostsBySubject(subjectId)) {
            System.out.println(
                    p.getPostId() + " | " +
                    p.getTitle() + " | " +
                    p.getAuthorName() + " | " +
                    p.getSubjectName()
            );
        }
    }

    public List<Post> getActivePosts() {
        List<Post> posts = new ArrayList<>();
        String sql = """
                SELECT p.post_id, p.user_id, p.subject_id, p.title, p.body,
                       p.post_type, p.status, p.created_at,
                       u.name AS author, s.subject_name
                FROM posts p
                JOIN users u ON p.user_id = u.user_id
                LEFT JOIN subjects s ON p.subject_id = s.subject_id
                WHERE p.status = 'ACTIVE'
                ORDER BY p.created_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int subjectId = resultSet.getInt("subject_id");
                Integer subId = resultSet.wasNull() ? null : subjectId;
                posts.add(new Post(
                        resultSet.getInt("post_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("author"),
                        subId,
                        resultSet.getString("subject_name"),
                        resultSet.getString("title"),
                        resultSet.getString("body"),
                        resultSet.getString("post_type"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve posts: " + e.getMessage());
        }

        return posts;
    }

    public List<Post> getPostsBySubject(int subjectId) {
        List<Post> posts = new ArrayList<>();
        String sql = """
                SELECT p.post_id, p.user_id, p.subject_id, p.title, p.body,
                       p.post_type, p.status, p.created_at,
                       u.name AS author, s.subject_name
                FROM posts p
                JOIN users u ON p.user_id = u.user_id
                LEFT JOIN subjects s ON p.subject_id = s.subject_id
                WHERE p.status = 'ACTIVE'
                  AND p.subject_id = ?
                ORDER BY p.created_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(new Post(
                            resultSet.getInt("post_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("author"),
                            resultSet.getInt("subject_id"),
                            resultSet.getString("subject_name"),
                            resultSet.getString("title"),
                            resultSet.getString("body"),
                            resultSet.getString("post_type"),
                            resultSet.getString("status"),
                            resultSet.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not filter posts: " + e.getMessage());
        }

        return posts;
    }

    public Post getPostById(int postId) {
        String sql = """
                SELECT p.post_id, p.user_id, p.subject_id, p.title, p.body,
                       p.post_type, p.status, p.created_at,
                       u.name AS author, s.subject_name
                FROM posts p
                JOIN users u ON p.user_id = u.user_id
                LEFT JOIN subjects s ON p.subject_id = s.subject_id
                WHERE p.post_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int subjectId = resultSet.getInt("subject_id");
                    Integer subId = resultSet.wasNull() ? null : subjectId;
                    return new Post(
                            resultSet.getInt("post_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("author"),
                            subId,
                            resultSet.getString("subject_name"),
                            resultSet.getString("title"),
                            resultSet.getString("body"),
                            resultSet.getString("post_type"),
                            resultSet.getString("status"),
                            resultSet.getTimestamp("created_at")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not find post: " + e.getMessage());
        }

        return null;
    }

    public int getActivePostCount() {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Could not count posts: " + e.getMessage());
        }
        return 0;
    }
}

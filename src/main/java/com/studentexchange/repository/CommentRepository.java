package com.studentexchange.repository;

import com.studentexchange.model.Comment;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    public boolean addComment(int postId, int userId, Integer parentCommentId, String commentText) {
        String sql = """
                INSERT INTO comments
                (post_id, user_id, parent_comment_id, comment_text, status)
                VALUES (?, ?, ?, ?, 'ACTIVE')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);
            statement.setInt(2, userId);

            if (parentCommentId == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, parentCommentId);
            }

            statement.setString(4, commentText);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            return false;
        }
    }

    public void displayComments(int postId) {
        for (Comment c : getCommentsForPost(postId)) {
            System.out.println("--------------------------------");
            System.out.println("Comment ID: " + c.getCommentId());
            if (c.isReply()) {
                System.out.println("Reply to Comment ID: " + c.getParentCommentId());
            }
            System.out.println("Author: " + c.getAuthorName());
            System.out.println("Comment: " + c.getCommentText());
            System.out.println("Created: " + c.getCreatedAt());
        }
    }

    public List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = """
                SELECT c.comment_id, c.post_id, c.user_id, c.parent_comment_id,
                       c.comment_text, c.status, c.created_at, u.name AS author_name
                FROM comments c
                JOIN users u ON c.user_id = u.user_id
                WHERE c.post_id = ? AND c.status = 'ACTIVE'
                ORDER BY c.created_at ASC, c.comment_id ASC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int parentId = result.getInt("parent_comment_id");
                    Integer pId = result.wasNull() ? null : parentId;

                    comments.add(new Comment(
                            result.getInt("comment_id"),
                            result.getInt("post_id"),
                            result.getInt("user_id"),
                            result.getString("author_name"),
                            pId,
                            result.getString("comment_text"),
                            result.getString("status"),
                            result.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching comments: " + e.getMessage());
        }

        return comments;
    }

    public int getCommentCountForPost(int postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ? AND status = 'ACTIVE'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting comments: " + e.getMessage());
        }
        return 0;
    }
}

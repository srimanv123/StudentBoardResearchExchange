
package com.studentexchange.repository;

import com.studentexchange.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = """
                SELECT c.comment_id, c.parent_comment_id, c.comment_text,
                       c.created_at, u.name
                FROM comments c
                JOIN users u ON c.user_id = u.user_id
                WHERE c.post_id = ? AND c.status = 'ACTIVE'
                ORDER BY c.created_at ASC, c.comment_id ASC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, postId);

            try (ResultSet result = statement.executeQuery()) {
                boolean found = false;

                while (result.next()) {
                    found = true;

                    int commentId = result.getInt("comment_id");
                    int parentId = result.getInt("parent_comment_id");
                    boolean isReply = !result.wasNull();

                    System.out.println("--------------------------------");
                    System.out.println("Comment ID: " + commentId);

                    if (isReply) {
                        System.out.println("Reply to Comment ID: " + parentId);
                    }

                    System.out.println("Author: " + result.getString("name"));
                    System.out.println("Comment: " + result.getString("comment_text"));
                    System.out.println("Created: " + result.getTimestamp("created_at"));
                }

                if (!found) {
                    System.out.println("No comments found for this post.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error displaying comments: " + e.getMessage());
        }
    }
}

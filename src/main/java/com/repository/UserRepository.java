
package com.studentexchange.repository;

import com.studentexchange.model.User;
import com.studentexchange.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public boolean registerUser(
            String name, String email, String password) {

        String sql = """
                INSERT INTO users
                (name, email, password_hash, role, account_status)
                VALUES (?, ?, ?, 'STUDENT', 'ACTIVE')
                """;

        String passwordHash = BCrypt.hashpw(
                password, BCrypt.gensalt(12));

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, passwordHash);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Registration failed: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String email, String password) {

        String sql = """
                SELECT user_id, name, email, password_hash,
                       role, account_status
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    String hash =
                            resultSet.getString("password_hash");
                    String status =
                            resultSet.getString("account_status");

                    if (BCrypt.checkpw(password, hash)
                            && "ACTIVE".equals(status)) {

                        return new User(
                                resultSet.getInt("user_id"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                hash,
                                resultSet.getString("role"),
                                status
                        );
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println(
                    "Login failed: " + e.getMessage());
        }

        return null;
    }
}

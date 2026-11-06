package com.studentexchange.repository;

import com.studentexchange.model.User;
import com.studentexchange.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    // ================= REGISTER USER =================

    public boolean registerUser(
            String name,
            String email,
            String password) {

        String sql = """
                INSERT INTO users
                (name, email, password_hash, role, account_status)
                VALUES (?, ?, ?, 'STUDENT', 'ACTIVE')
                """;

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("Database connected for registration.");

            // Check whether email already exists
            String checkSql =
                    "SELECT user_id FROM users WHERE email = ?";

            try (PreparedStatement checkStatement =
                         connection.prepareStatement(checkSql)) {

                checkStatement.setString(1, email);

                try (ResultSet resultSet =
                             checkStatement.executeQuery()) {

                    if (resultSet.next()) {
                        System.out.println(
                                "Registration failed: Email already exists."
                        );
                        return false;
                    }
                }
            }

            // Hash password using BCrypt
            String passwordHash = BCrypt.hashpw(
                    password,
                    BCrypt.gensalt(12)
            );

            // Insert new user
            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, passwordHash);

                int rows = statement.executeUpdate();

                if (rows == 1) {
                    System.out.println(
                            "Registration successful for: " + email
                    );
                    return true;
                }

                System.out.println(
                        "Registration failed: No row inserted."
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println("DATABASE REGISTRATION ERROR:");
            e.printStackTrace();

            return false;

        } catch (Exception e) {

            System.out.println("REGISTRATION ERROR:");
            e.printStackTrace();

            return false;
        }
    }

    // ================= LOGIN USER =================

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

                        System.out.println(
                                "Login successful: " + email
                        );

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

            System.out.println("DATABASE LOGIN ERROR:");
            e.printStackTrace();

        } catch (Exception e) {

            System.out.println("LOGIN ERROR:");
            e.printStackTrace();
        }

        return null;
    }
}
package com.studentexchange.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/student_exchange?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static String getUser() {
        String user = System.getenv("STUDENT_DB_USER");
        if (user != null && !user.isBlank()) {
            return user;
        }
        return "student_app";
    }

    private static String getPassword() {
        String pass = System.getenv("STUDENT_DB_PASSWORD");
        if (pass != null && !pass.isBlank()) {
            return pass;
        }
        return "sriman";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, getUser(), getPassword());
    }
}


package com.studentexchange.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/student_exchange";

    private static final String USER =
            System.getenv("STUDENT_DB_USER");

    private static final String PASSWORD =
            System.getenv("STUDENT_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "Set STUDENT_DB_USER and STUDENT_DB_PASSWORD first."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

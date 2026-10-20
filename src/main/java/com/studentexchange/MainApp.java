
package com.studentexchange;

import com.studentexchange.repository.SubjectRepository;
import com.studentexchange.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Student Board and Research Exchange System");

        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Database connected successfully!");
            System.out.println("Connected database: " + connection.getCatalog());

            SubjectRepository repository = new SubjectRepository();
            repository.displaySubjects();

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());
        }
    }
}

package com.studentexchange.repository;

import com.studentexchange.model.Subject;
import com.studentexchange.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    public void displaySubjects() {
        String sql = """
                SELECT subject_id, subject_name, course_code, semester
                FROM subjects
                ORDER BY semester, subject_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAvailable Subjects");

            while (resultSet.next()) {
                System.out.println(
                        resultSet.getInt("subject_id") + " | " +
                        resultSet.getString("subject_name") + " | " +
                        resultSet.getString("course_code") + " | Semester " +
                        resultSet.getInt("semester")
                );
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve subjects: " + e.getMessage());
        }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = """
                SELECT subject_id, subject_name, course_code, semester
                FROM subjects
                ORDER BY semester, subject_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int semester = resultSet.getInt("semester");
                Integer sem = resultSet.wasNull() ? null : semester;
                subjects.add(new Subject(
                        resultSet.getInt("subject_id"),
                        resultSet.getString("subject_name"),
                        resultSet.getString("course_code"),
                        sem
                ));
            }

        } catch (SQLException e) {
            System.err.println("Could not fetch subjects: " + e.getMessage());
        }

        return subjects;
    }
}

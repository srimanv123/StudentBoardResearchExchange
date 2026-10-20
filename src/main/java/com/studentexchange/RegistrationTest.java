
package com.studentexchange;

import com.studentexchange.repository.UserRepository;

public class RegistrationTest {

    public static void main(String[] args) {
        UserRepository repository = new UserRepository();

        boolean registered = repository.registerUser(
                "Test Student",
                "teststudent@example.com",
                "TestPassword@123"
        );

        System.out.println(
                registered
                ? "Student registered successfully!"
                : "Student registration failed."
        );
    }
}

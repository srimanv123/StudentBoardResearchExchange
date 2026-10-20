
package com.studentexchange;

import com.studentexchange.model.User;
import com.studentexchange.repository.UserRepository;

public class LoginTest {

    public static void main(String[] args) {
        UserRepository repository = new UserRepository();

        User user = repository.loginUser(
                "teststudent@example.com",
                "TestPassword@123"
        );

        if (user != null) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + user.getName());
            System.out.println("Role: " + user.getRole());
        } else {
            System.out.println("Invalid credentials or inactive account.");
        }
    }
}

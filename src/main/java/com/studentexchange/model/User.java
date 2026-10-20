
package com.studentexchange.model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private String accountStatus;

    public User(int userId, String name, String email,
                String passwordHash, String role,
                String accountStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public User(String name, String email, String passwordHash) {
        this(0, name, email, passwordHash, "STUDENT", "ACTIVE");
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }
}

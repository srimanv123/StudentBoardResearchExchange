package com.studentexchange;

import com.studentexchange.model.User;
import com.studentexchange.repository.UserRepository;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final UserRepository userRepository =
            new UserRepository();

    private Stage stage;

    // ================= APPLICATION START =================

    @Override
    public void start(Stage stage) {

        this.stage = stage;

        showLoginScreen();
    }

    // ================= LOGIN SCREEN =================

    private void showLoginScreen() {

        Label title = new Label("Student Exchange");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#1e293b"));

        Label subtitle = new Label(
                "Student Board and Research Exchange System"
        );
        subtitle.setTextFill(Color.web("#64748b"));

        Label welcome = new Label("Welcome Back!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        welcome.setTextFill(Color.web("#0f172a"));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Button loginButton = createButton("Login");

        Button registerButton =
                new Button("Create New Account");

        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #2563eb;"
        );

        loginButton.setOnAction(event -> {

            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {

                showAlert(
                        "Validation Error",
                        "Please enter your email and password."
                );

                return;
            }

            User user = userRepository.loginUser(
                    email,
                    password
            );

            if (user != null) {

                System.out.println(
                        "Login successful: " + user.getName()
                );

                showDashboard(user);

            } else {

                showAlert(
                        "Login Failed",
                        "Invalid email or password, or your account is inactive."
                );
            }
        });

        registerButton.setOnAction(
                event -> showRegisterScreen()
        );

        VBox form = new VBox(
                12,
                welcome,
                new Label("Email Address"),
                emailField,
                new Label("Password"),
                passwordField,
                loginButton,
                registerButton
        );

        styleForm(form);

        VBox root = new VBox(
                20,
                title,
                subtitle,
                form
        );

        styleRoot(root);

        stage.setScene(new Scene(root, 900, 650));
        stage.setTitle("Student Exchange - Login");
        stage.show();
    }

    // ================= REGISTRATION SCREEN =================

    private void showRegisterScreen() {

        Label title = new Label("Create Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1e293b"));

        Label subtitle = new Label(
                "Join the Student Board and Research Exchange"
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");

        PasswordField confirmPasswordField =
                new PasswordField();

        confirmPasswordField.setPromptText(
                "Confirm your password"
        );

        Button createButton = createButton("Register");

        Button backButton = new Button("Back to Login");

        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #2563eb;"
        );

        createButton.setOnAction(event -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword =
                    confirmPasswordField.getText();

            if (name.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()
                    || confirmPassword.isEmpty()) {

                showAlert(
                        "Validation Error",
                        "Please fill in all fields."
                );

                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                showAlert(
                        "Invalid Email",
                        "Please enter a valid email address."
                );

                return;
            }

            if (password.length() < 8) {

                showAlert(
                        "Weak Password",
                        "Password must contain at least 8 characters."
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                showAlert(
                        "Password Mismatch",
                        "Passwords do not match."
                );

                return;
            }

            System.out.println(
                    "Registering user: " + email
            );

            boolean registered =
                    userRepository.registerUser(
                            name,
                            email,
                            password
                    );

            if (registered) {

                showAlert(
                        "Registration Successful",
                        "Your account has been created successfully!"
                );

                showLoginScreen();

            } else {

                showAlert(
                        "Registration Failed",
                        "Unable to create account. Check the terminal for errors."
                );
            }
        });

        backButton.setOnAction(
                event -> showLoginScreen()
        );

        VBox form = new VBox(
                12,
                new Label("Full Name"),
                nameField,
                new Label("Email Address"),
                emailField,
                new Label("Password"),
                passwordField,
                new Label("Confirm Password"),
                confirmPasswordField,
                createButton,
                backButton
        );

        styleForm(form);

        VBox root = new VBox(
                18,
                title,
                subtitle,
                form
        );

        styleRoot(root);

        stage.setScene(new Scene(root, 900, 700));
        stage.setTitle("Student Exchange - Registration");
        stage.show();
    }

    // ================= STUDENT DASHBOARD =================

    private void showDashboard(User user) {

        BorderPane mainLayout = new BorderPane();

        mainLayout.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        // SIDEBAR

        VBox sidebar = new VBox(18);

        sidebar.setPadding(
                new Insets(30, 18, 25, 18)
        );

        sidebar.setPrefWidth(220);

        sidebar.setStyle(
                "-fx-background-color: #172554;"
        );

        Label logo = new Label("Student Exchange");

        logo.setFont(
                Font.font("Arial", FontWeight.BOLD, 19)
        );

        logo.setTextFill(Color.WHITE);

        Label menuLabel = new Label("MAIN MENU");

        menuLabel.setTextFill(
                Color.web("#93c5fd")
        );

        menuLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 11)
        );

        Button dashboardButton =
                createMenuButton("Dashboard");

        Button discussionButton =
                createMenuButton("Discussion Board");

        Button resourcesButton =
                createMenuButton("Resources");

        Button bookmarksButton =
                createMenuButton("My Bookmarks");

        Button profileButton =
                createMenuButton("My Profile");

        Region spacer = new Region();

        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutButton =
                createMenuButton("Logout");

        logoutButton.setStyle(
                "-fx-background-color: #991b1b;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 12px;"
        );

        sidebar.getChildren().addAll(
                logo,
                new Separator(),
                menuLabel,
                dashboardButton,
                discussionButton,
                resourcesButton,
                bookmarksButton,
                profileButton,
                spacer,
                logoutButton
        );

        // TOP BAR

        Label pageTitle = new Label("Dashboard");

        pageTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 24)
        );

        pageTitle.setTextFill(
                Color.web("#0f172a")
        );

        Label userLabel = new Label(user.getName());

        userLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
        );

        userLabel.setTextFill(
                Color.web("#1e40af")
        );

        Region topSpacer = new Region();

        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(
                20,
                pageTitle,
                topSpacer,
                userLabel
        );

        topBar.setAlignment(Pos.CENTER_LEFT);

        topBar.setPadding(
                new Insets(20, 30, 20, 30)
        );

        topBar.setStyle(
                "-fx-background-color: white;"
        );

        // WELCOME SECTION

        Label welcomeLabel = new Label(
                "Welcome, " + user.getName() + "!"
        );

        welcomeLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 27)
        );

        welcomeLabel.setTextFill(
                Color.web("#0f172a")
        );

        Label description = new Label(
                "Learn, discuss and share knowledge with fellow students."
        );

        description.setTextFill(
                Color.web("#64748b")
        );

        VBox welcomeSection = new VBox(
                8,
                welcomeLabel,
                description
        );

        // DASHBOARD CARDS

        GridPane cards = new GridPane();

        cards.setHgap(18);
        cards.setVgap(18);

        VBox discussionCard = createDashboardCard(
                "Discussion Board",
                "Ask questions and interact with other students."
        );

        VBox resourceCard = createDashboardCard(
                "Study Resources",
                "Explore notes, research papers and learning materials."
        );

        VBox bookmarkCard = createDashboardCard(
                "My Bookmarks",
                "Access your saved study materials."
        );

        VBox profileCard = createDashboardCard(
                "My Profile",
                "View your student account information."
        );

        cards.add(discussionCard, 0, 0);
        cards.add(resourceCard, 1, 0);
        cards.add(bookmarkCard, 0, 1);
        cards.add(profileCard, 1, 1);

        // CARD ACTIONS

        discussionCard.setOnMouseClicked(event ->
                showAlert(
                        "Discussion Board",
                        "Discussion module is coming next."
                )
        );

        resourceCard.setOnMouseClicked(event ->
                showAlert(
                        "Resources",
                        "Resource sharing module is coming next."
                )
        );

        bookmarkCard.setOnMouseClicked(event ->
                showAlert(
                        "Bookmarks",
                        "Bookmark module is coming next."
                )
        );

        profileCard.setOnMouseClicked(event ->
                showProfile(user)
        );

        VBox content = new VBox(
                25,
                welcomeSection,
                cards
        );

        content.setPadding(
                new Insets(35)
        );

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background: #f1f5f9;" +
                "-fx-background-color: #f1f5f9;"
        );

        // NAVIGATION ACTIONS

        dashboardButton.setOnAction(
                event -> showDashboard(user)
        );

        discussionButton.setOnAction(event ->
                showAlert(
                        "Discussion Board",
                        "Discussion module is coming next."
                )
        );

        resourcesButton.setOnAction(event ->
                showAlert(
                        "Resources",
                        "Resource sharing module is coming next."
                )
        );

        bookmarksButton.setOnAction(event ->
                showAlert(
                        "Bookmarks",
                        "Bookmark module is coming next."
                )
        );

        profileButton.setOnAction(
                event -> showProfile(user)
        );

        logoutButton.setOnAction(event -> {

            showAlert(
                    "Logout",
                    "You have been logged out successfully."
            );

            showLoginScreen();
        });

        mainLayout.setLeft(sidebar);
        mainLayout.setTop(topBar);
        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(
                mainLayout,
                1100,
                750
        );

        stage.setScene(scene);

        stage.setTitle(
                "Student Exchange - Dashboard"
        );

        stage.setMinWidth(850);
        stage.setMinHeight(600);

        stage.show();
    }

    // ================= PROFILE =================

    private void showProfile(User user) {

        showAlert(
                "My Profile",
                "Student Name: " + user.getName() +
                "\nEmail: " + user.getEmail() +
                "\nRole: " + user.getRole()
        );
    }

    // ================= DASHBOARD CARD =================

    private VBox createDashboardCard(
            String heading,
            String description) {

        Label title = new Label(heading);

        title.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        title.setTextFill(
                Color.web("#1e3a8a")
        );

        Label details = new Label(description);

        details.setWrapText(true);

        details.setTextFill(
                Color.web("#64748b")
        );

        VBox card = new VBox(
                15,
                title,
                details
        );

        card.setPadding(
                new Insets(25)
        );

        card.setPrefSize(300, 150);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12px;"
        );

        card.setCursor(Cursor.HAND);

        return card;
    }

    // ================= SIDEBAR BUTTON =================

    private Button createMenuButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 12px;" +
                "-fx-background-radius: 8px;"
        );

        return button;
    }

    // ================= COMMON BUTTON =================

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 12px;"
        );

        return button;
    }

    // ================= FORM STYLING =================

    private void styleForm(VBox form) {

        form.setPadding(
                new Insets(30)
        );

        form.setMaxWidth(420);

        form.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15px;"
        );
    }

    private void styleRoot(VBox root) {

        root.setAlignment(Pos.CENTER);

        root.setPadding(
                new Insets(30)
        );

        root.setStyle(
                "-fx-background-color: #eff6ff;"
        );
    }

    // ================= ALERT =================

    private void showAlert(
            String title,
            String message) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {

        launch(args);
    }
}
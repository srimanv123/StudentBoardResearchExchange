package com.studentexchange;

import com.studentexchange.model.*;
import com.studentexchange.repository.*;

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private final UserRepository userRepository = new UserRepository();
    private final SubjectRepository subjectRepository = new SubjectRepository();
    private final PostRepository postRepository = new PostRepository();
    private final CommentRepository commentRepository = new CommentRepository();
    private final ResourceRepository resourceRepository = new ResourceRepository();
    private final BookmarkRepository bookmarkRepository = new BookmarkRepository();
    private final ReportRepository reportRepository = new ReportRepository();

    private Stage stage;

    // ================= APPLICATION START =================

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setMinWidth(960);
        stage.setMinHeight(680);
        showLoginScreen();
    }

    // ================= 1. LOGIN SCREEN =================

    private void showLoginScreen() {
        Label title = new Label("Student Exchange");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#1e293b"));

        Label subtitle = new Label("Student Board & Research Exchange System");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        Label welcome = new Label("Welcome Back!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        welcome.setTextFill(Color.web("#0f172a"));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        Button loginButton = createPrimaryButton("Sign In");
        Button registerButton = new Button("Don't have an account? Register here");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-cursor: hand; -fx-font-weight: bold;");

        Label hintLabel = new Label("Faculty Demo Accounts:\nAdmin: teststudent@example.com (pass: TestPassword@123)\nStudent: sriman@gmai.com");
        hintLabel.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-padding: 8px; -fx-border-radius: 6px; -fx-text-fill: #475569; -fx-font-size: 11px;");

        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter both your email and password.");
                return;
            }

            User user = userRepository.loginUser(email, password);
            if (user != null) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    showAdminDashboard(user);
                } else {
                    showDashboard(user);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password, or your account is suspended.");
            }
        });

        registerButton.setOnAction(event -> showRegisterScreen());

        VBox form = new VBox(12,
                welcome,
                new Label("Email Address"), emailField,
                new Label("Password"), passwordField,
                new Region(),
                loginButton,
                registerButton,
                hintLabel
        );
        styleFormBox(form);

        VBox root = new VBox(20, title, subtitle, form);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f1f5f9;");

        stage.setScene(new Scene(root, 960, 680));
        stage.setTitle("Student Exchange - Login");
        stage.show();
    }

    // ================= 2. REGISTRATION SCREEN =================

    private void showRegisterScreen() {
        Label title = new Label("Create New Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1e293b"));

        Label subtitle = new Label("Join the Student Board and Research Exchange Platform");
        subtitle.setTextFill(Color.web("#64748b"));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your student email");
        emailField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create password (min 8 characters)");
        passwordField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setStyle("-fx-padding: 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        Button registerButton = createPrimaryButton("Create Student Account");
        Button backButton = new Button("Already registered? Back to Login");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-cursor: hand; -fx-font-weight: bold;");

        registerButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid email address.");
                return;
            }

            if (password.length() < 8) {
                showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must contain at least 8 characters.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match.");
                return;
            }

            boolean registered = userRepository.registerUser(name, email, password);
            if (registered) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your student account has been created successfully! You can now log in.");
                showLoginScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email is already registered or a database error occurred.");
            }
        });

        backButton.setOnAction(event -> showLoginScreen());

        VBox form = new VBox(10,
                new Label("Full Name"), nameField,
                new Label("Email Address"), emailField,
                new Label("Password"), passwordField,
                new Label("Confirm Password"), confirmPasswordField,
                new Region(),
                registerButton,
                backButton
        );
        styleFormBox(form);

        VBox root = new VBox(15, title, subtitle, form);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f1f5f9;");

        stage.setScene(new Scene(root, 960, 680));
        stage.setTitle("Student Exchange - Register");
        stage.show();
    }

    // ================= 3. STUDENT DASHBOARD =================

    private void showDashboard(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "Dashboard"));
        layout.setTop(createTopBar(user, "Dashboard"));

        // Content
        Label welcomeHeader = new Label("Welcome back, " + user.getName() + "!");
        welcomeHeader.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcomeHeader.setTextFill(Color.web("#0f172a"));

        Label welcomeSub = new Label("Collaborate with peers, participate in research discussions, and explore study resources.");
        welcomeSub.setFont(Font.font("Arial", 14));
        welcomeSub.setTextFill(Color.web("#64748b"));

        VBox welcomeBanner = new VBox(6, welcomeHeader, welcomeSub);
        welcomeBanner.setPadding(new Insets(20));
        welcomeBanner.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px;");

        // Metrics from DB
        int activePostsCount = postRepository.getActivePostCount();
        int activeResourcesCount = resourceRepository.getActiveResourceCount();
        int userBookmarksCount = bookmarkRepository.getBookmarkCount(user.getUserId());

        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);

        VBox discCard = createStatCard("Discussion Board", activePostsCount + " Active Threads", "Explore questions, share insights, and discuss academic topics.", "#2563eb");
        discCard.setOnMouseClicked(e -> showDiscussionBoard(user));

        VBox resCard = createStatCard("Study Resources", activeResourcesCount + " Available Links", "Browse and share research papers, lecture notes, and study links.", "#059669");
        resCard.setOnMouseClicked(e -> showResources(user));

        VBox bookCard = createStatCard("My Bookmarks", userBookmarksCount + " Saved Resources", "Quickly access resources you have saved for later review.", "#d97706");
        bookCard.setOnMouseClicked(e -> showBookmarks(user));

        VBox profCard = createStatCard("My Profile", user.getRole(), "View your account details and participation metrics.", "#7c3aed");
        profCard.setOnMouseClicked(e -> showProfileView(user));

        cardsGrid.add(discCard, 0, 0);
        cardsGrid.add(resCard, 1, 0);
        cardsGrid.add(bookCard, 0, 1);
        cardsGrid.add(profCard, 1, 1);

        VBox content = new VBox(25, welcomeBanner, cardsGrid);
        content.setPadding(new Insets(30));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
        stage.setTitle("Student Exchange - Dashboard");
    }

    // ================= 4. DISCUSSION BOARD SCREEN =================

    private void showDiscussionBoard(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "Discussion Board"));
        layout.setTop(createTopBar(user, "Discussion Board"));

        // Controls bar: Subject filter and "New Post" button
        ComboBox<SubjectFilterItem> filterCombo = new ComboBox<>();
        filterCombo.getItems().add(new SubjectFilterItem(null, "All Subjects"));
        for (Subject s : subjectRepository.getAllSubjects()) {
            filterCombo.getItems().add(new SubjectFilterItem(s.getSubjectId(), s.getSubjectName() + " (" + s.getCourseCode() + ")"));
        }
        filterCombo.getSelectionModel().selectFirst();
        filterCombo.setStyle("-fx-padding: 6px; -fx-background-radius: 6px;");

        Button newPostBtn = createSuccessButton("+ Start New Discussion");
        newPostBtn.setOnAction(e -> openNewPostDialog(user));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox filterBar = new HBox(12, new Label("Filter by Course:"), filterCombo, spacer, newPostBtn);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(15, 20, 15, 20));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");

        VBox postsContainer = new VBox(15);

        Runnable loadPosts = () -> {
            postsContainer.getChildren().clear();
            SubjectFilterItem selected = filterCombo.getValue();
            List<Post> posts;
            if (selected == null || selected.subjectId == null) {
                posts = postRepository.getActivePosts();
            } else {
                posts = postRepository.getPostsBySubject(selected.subjectId);
            }

            if (posts.isEmpty()) {
                Label emptyLabel = new Label("No discussion posts found for this course.");
                emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-padding: 40px;");
                postsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Post post : posts) {
                VBox card = createPostCard(user, post);
                postsContainer.getChildren().add(card);
            }
        };

        filterCombo.setOnAction(e -> loadPosts.run());
        loadPosts.run();

        VBox content = new VBox(20, filterBar, postsContainer);
        content.setPadding(new Insets(25));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
        stage.setTitle("Student Exchange - Discussion Board");
    }

    private VBox createPostCard(User user, Post post) {
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        titleLabel.setTextFill(Color.web("#1e3a8a"));

        Label typeTag = new Label(post.getPostType());
        typeTag.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label subjectTag = new Label(post.getSubjectName());
        subjectTag.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-size: 11px;");

        Label metaLabel = new Label("Posted by " + post.getAuthorName() + " on " + post.getCreatedAt().toString().substring(0, 16));
        metaLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        HBox headerLine = new HBox(10, titleLabel, typeTag, subjectTag);
        headerLine.setAlignment(Pos.CENTER_LEFT);

        Label bodySnippet = new Label(post.getBody().length() > 220 ? post.getBody().substring(0, 220) + "..." : post.getBody());
        bodySnippet.setWrapText(true);
        bodySnippet.setTextFill(Color.web("#334155"));

        int commentCount = commentRepository.getCommentCountForPost(post.getPostId());
        Button viewBtn = new Button("💬 View Discussion & Replies (" + commentCount + ")");
        viewBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
        viewBtn.setOnAction(e -> showPostDetail(user, post));

        Button reportBtn = new Button("⚠️ Report");
        reportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-cursor: hand;");
        reportBtn.setOnAction(e -> openReportDialog(user, post.getPostId(), null));

        Region cardSpacer = new Region();
        HBox.setHgrow(cardSpacer, Priority.ALWAYS);

        HBox footer = new HBox(10, viewBtn, cardSpacer, reportBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, headerLine, metaLabel, bodySnippet, new Separator(), footer);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");
        return card;
    }

    private void openNewPostDialog(User user) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Start New Discussion");
        dialog.setHeaderText("Create a new discussion or ask a question");

        TextField titleField = new TextField();
        titleField.setPromptText("Enter a clear, concise question or title");

        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(subjectRepository.getAllSubjects());
        if (!subjectCombo.getItems().isEmpty()) subjectCombo.getSelectionModel().selectFirst();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("QUESTION", "DISCUSSION", "ANNOUNCEMENT");
        typeCombo.getSelectionModel().selectFirst();

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Write your question or thoughts here...");
        bodyArea.setPrefRowCount(6);
        bodyArea.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Course/Subject:"), 0, 1);
        grid.add(subjectCombo, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Details:"), 0, 3);
        grid.add(bodyArea, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = titleField.getText().trim();
                String body = bodyArea.getText().trim();
                Subject subject = subjectCombo.getValue();
                String type = typeCombo.getValue();

                if (title.isEmpty() || body.isEmpty() || subject == null) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please provide a title, course, and body text.");
                    return false;
                }

                boolean created = postRepository.createPost(user.getUserId(), subject.getSubjectId(), title, body, type);
                if (created) {
                    showAlert(Alert.AlertType.INFORMATION, "Discussion Created", "Your post has been successfully published!");
                    showDiscussionBoard(user);
                    return true;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not publish post. Database error.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    // ================= 5. POST DETAIL & COMMENTS SCREEN =================

    private void showPostDetail(User user, Post post) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "Discussion Board"));
        layout.setTop(createTopBar(user, "Discussion Details"));

        // Back button
        Button backBtn = new Button("← Back to Discussions");
        backBtn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-padding: 8px 14px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> showDiscussionBoard(user));

        // Original Post Card
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#0f172a"));

        Label typeTag = new Label(post.getPostType());
        typeTag.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-padding: 4px 10px; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label subjectTag = new Label(post.getSubjectName());
        subjectTag.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 4px 10px; -fx-background-radius: 4px; -fx-font-size: 11px;");

        HBox badges = new HBox(10, typeTag, subjectTag);
        Label metaLabel = new Label("Posted by " + post.getAuthorName() + " on " + post.getCreatedAt().toString().substring(0, 16));
        metaLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        Label bodyLabel = new Label(post.getBody());
        bodyLabel.setWrapText(true);
        bodyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e293b; -fx-line-spacing: 4px;");

        Button reportBtn = new Button("⚠️ Report Post");
        reportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-cursor: hand; -fx-font-size: 12px;");
        reportBtn.setOnAction(e -> openReportDialog(user, post.getPostId(), null));

        HBox postFooter = new HBox(reportBtn);
        postFooter.setAlignment(Pos.CENTER_RIGHT);

        VBox postCard = new VBox(12, titleLabel, badges, metaLabel, new Separator(), bodyLabel, postFooter);
        postCard.setPadding(new Insets(22));
        postCard.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #cbd5e1; -fx-border-radius: 12px;");

        // Comments section
        Label commentsHeader = new Label("Discussion & Replies");
        commentsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        commentsHeader.setTextFill(Color.web("#0f172a"));

        VBox commentsList = new VBox(12);

        // Reply tracker
        final Integer[] replyToParentId = new Integer[]{null};
        Label replyTrackerLabel = new Label();
        replyTrackerLabel.setStyle("-fx-text-fill: #0369a1; -fx-font-style: italic; -fx-font-size: 12px;");
        replyTrackerLabel.setVisible(false);

        Button cancelReplyBtn = new Button("✕ Cancel reply");
        cancelReplyBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-font-size: 11px; -fx-cursor: hand;");
        cancelReplyBtn.setVisible(false);

        cancelReplyBtn.setOnAction(e -> {
            replyToParentId[0] = null;
            replyTrackerLabel.setVisible(false);
            cancelReplyBtn.setVisible(false);
        });

        HBox replyTrackerBox = new HBox(10, replyTrackerLabel, cancelReplyBtn);
        replyTrackerBox.setAlignment(Pos.CENTER_LEFT);

        Runnable loadComments = () -> {
            commentsList.getChildren().clear();
            List<Comment> comments = commentRepository.getCommentsForPost(post.getPostId());

            if (comments.isEmpty()) {
                Label noComments = new Label("No replies yet. Be the first to join the conversation!");
                noComments.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic; -fx-padding: 15px;");
                commentsList.getChildren().add(noComments);
                return;
            }

            for (Comment c : comments) {
                VBox cBox = new VBox(6);
                cBox.setPadding(new Insets(12, 16, 12, 16));

                Label authorLabel = new Label(c.getAuthorName() + " • " + c.getCreatedAt().toString().substring(0, 16));
                authorLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #334155;");

                if (c.isReply()) {
                    cBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8px; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-border-width: 0 0 0 4px;");
                    Label replyTag = new Label("↳ Reply to comment #" + c.getParentCommentId());
                    replyTag.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: bold;");
                    cBox.getChildren().add(replyTag);
                } else {
                    cBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px;");
                }

                Label textLabel = new Label(c.getCommentText());
                textLabel.setWrapText(true);
                textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");

                Button replyBtn = new Button("Reply");
                replyBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-cursor: hand; -fx-font-weight: bold;");
                replyBtn.setOnAction(e -> {
                    replyToParentId[0] = c.getCommentId();
                    replyTrackerLabel.setText("Replying to " + c.getAuthorName() + " (Comment #" + c.getCommentId() + ")");
                    replyTrackerLabel.setVisible(true);
                    cancelReplyBtn.setVisible(true);
                });

                HBox cFooter = new HBox(replyBtn);
                cFooter.setAlignment(Pos.CENTER_RIGHT);

                cBox.getChildren().addAll(authorLabel, textLabel, cFooter);
                commentsList.getChildren().add(cBox);
            }
        };

        loadComments.run();

        // New Comment Input
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Add your reply or insight...");
        commentArea.setPrefRowCount(3);
        commentArea.setWrapText(true);
        commentArea.setStyle("-fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px;");

        Button submitCommentBtn = createPrimaryButton("Submit Reply");
        submitCommentBtn.setOnAction(e -> {
            String text = commentArea.getText().trim();
            if (text.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Comment", "Please write a comment before submitting.");
                return;
            }

            boolean added = commentRepository.addComment(post.getPostId(), user.getUserId(), replyToParentId[0], text);
            if (added) {
                commentArea.clear();
                replyToParentId[0] = null;
                replyTrackerLabel.setVisible(false);
                cancelReplyBtn.setVisible(false);
                loadComments.run();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not submit comment.");
            }
        });

        VBox commentInputBox = new VBox(8, replyTrackerBox, commentArea, submitCommentBtn);
        commentInputBox.setPadding(new Insets(16));
        commentInputBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");

        VBox content = new VBox(20, backBtn, postCard, commentsHeader, commentsList, commentInputBox);
        content.setPadding(new Insets(25));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
    }

    // ================= 6. STUDY RESOURCES SCREEN =================

    private void showResources(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "Resources"));
        layout.setTop(createTopBar(user, "Study Resources"));

        ComboBox<SubjectFilterItem> filterCombo = new ComboBox<>();
        filterCombo.getItems().add(new SubjectFilterItem(null, "All Subjects"));
        for (Subject s : subjectRepository.getAllSubjects()) {
            filterCombo.getItems().add(new SubjectFilterItem(s.getSubjectId(), s.getSubjectName() + " (" + s.getCourseCode() + ")"));
        }
        filterCombo.getSelectionModel().selectFirst();
        filterCombo.setStyle("-fx-padding: 6px; -fx-background-radius: 6px;");

        Button shareBtn = createSuccessButton("+ Share Study Resource Link");
        shareBtn.setOnAction(e -> openShareResourceDialog(user));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox filterBar = new HBox(12, new Label("Filter by Subject:"), filterCombo, spacer, shareBtn);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(15, 20, 15, 20));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");

        VBox resourcesContainer = new VBox(15);

        Runnable loadResources = () -> {
            resourcesContainer.getChildren().clear();
            SubjectFilterItem selected = filterCombo.getValue();
            List<Resource> list;
            if (selected == null || selected.subjectId == null) {
                list = resourceRepository.getActiveResources();
            } else {
                list = resourceRepository.getResourcesBySubject(selected.subjectId);
            }

            if (list.isEmpty()) {
                Label emptyLabel = new Label("No study resources shared yet for this selection.");
                emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-padding: 40px;");
                resourcesContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Resource r : list) {
                VBox card = createResourceCard(user, r, () -> filterCombo.getOnAction().handle(null));
                resourcesContainer.getChildren().add(card);
            }
        };

        filterCombo.setOnAction(e -> loadResources.run());
        loadResources.run();

        VBox content = new VBox(20, filterBar, resourcesContainer);
        content.setPadding(new Insets(25));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
        stage.setTitle("Student Exchange - Study Resources");
    }

    private VBox createResourceCard(User user, Resource r, Runnable onStateChange) {
        Label titleLabel = new Label(r.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        titleLabel.setTextFill(Color.web("#0f172a"));

        Label typeTag = new Label(r.getResourceType());
        typeTag.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label subjectTag = new Label(r.getSubjectName());
        subjectTag.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-size: 11px;");

        HBox topBadges = new HBox(10, titleLabel, typeTag, subjectTag);
        topBadges.setAlignment(Pos.CENTER_LEFT);

        Label metaLabel = new Label("Uploaded by " + r.getUploaderName() + " on " + r.getCreatedAt().toString().substring(0, 16));
        metaLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        Label descLabel = new Label(r.getDescription());
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.web("#334155"));

        Label urlLabel = new Label("🔗 " + r.getResourceUrl());
        urlLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 12px; -fx-underline: true;");

        Button openUrlBtn = new Button("🌐 Open Resource Link");
        openUrlBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
        openUrlBtn.setOnAction(e -> openInBrowser(r.getResourceUrl()));

        boolean isBookmarked = bookmarkRepository.isBookmarked(user.getUserId(), r.getResourceId());
        Button bookmarkBtn = new Button(isBookmarked ? "★ Bookmarked" : "☆ Save Bookmark");
        bookmarkBtn.setStyle(isBookmarked
                ? "-fx-background-color: #fef3c7; -fx-text-fill: #b45309; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6px 12px; -fx-background-radius: 6px; -fx-cursor: hand;"
                : "-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 12px; -fx-padding: 6px 12px; -fx-background-radius: 6px; -fx-cursor: hand;"
        );

        bookmarkBtn.setOnAction(e -> {
            if (isBookmarked) {
                bookmarkRepository.removeBookmark(user.getUserId(), r.getResourceId());
            } else {
                bookmarkRepository.addBookmark(user.getUserId(), r.getResourceId());
            }
            if (onStateChange != null) onStateChange.run();
        });

        Button reportBtn = new Button("⚠️ Report");
        reportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-cursor: hand;");
        reportBtn.setOnAction(e -> openReportDialog(user, null, r.getResourceId()));

        Region cardSpacer = new Region();
        HBox.setHgrow(cardSpacer, Priority.ALWAYS);

        HBox footer = new HBox(10, openUrlBtn, bookmarkBtn, cardSpacer, reportBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, topBadges, metaLabel, descLabel, urlLabel, new Separator(), footer);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");
        return card;
    }

    private void openShareResourceDialog(User user) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Share Study Resource Link");
        dialog.setHeaderText("Share lecture slides, research papers, or external learning links");

        TextField titleField = new TextField();
        titleField.setPromptText("Resource title (e.g. Distributed Systems Lecture Notes)");

        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(subjectRepository.getAllSubjects());
        if (!subjectCombo.getItems().isEmpty()) subjectCombo.getSelectionModel().selectFirst();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("NOTES", "RESEARCH_PAPER", "PRESENTATION", "LAB_MATERIAL", "REFERENCE_LINK", "OTHER");
        typeCombo.getSelectionModel().selectFirst();

        TextField urlField = new TextField();
        urlField.setPromptText("https://example.com/materials.pdf");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Brief description of what this resource covers...");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Course/Subject:"), 0, 1);
        grid.add(subjectCombo, 1, 1);
        grid.add(new Label("Resource Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("External Web URL:"), 0, 3);
        grid.add(urlField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = titleField.getText().trim();
                String url = urlField.getText().trim();
                String desc = descArea.getText().trim();
                Subject subject = subjectCombo.getValue();
                String type = typeCombo.getValue();

                if (title.isEmpty() || url.isEmpty() || subject == null) {
                    showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please provide a title, course, and valid URL.");
                    return false;
                }

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    showAlert(Alert.AlertType.WARNING, "Invalid URL", "Resource link must start with http:// or https://");
                    return false;
                }

                boolean added = resourceRepository.addResource(user.getUserId(), subject.getSubjectId(), title, desc, type, url);
                if (added) {
                    showAlert(Alert.AlertType.INFORMATION, "Resource Shared", "Study resource has been successfully posted!");
                    showResources(user);
                    return true;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not share resource.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    // ================= 7. BOOKMARKS SCREEN =================

    private void showBookmarks(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "My Bookmarks"));
        layout.setTop(createTopBar(user, "My Saved Bookmarks"));

        VBox bookmarksContainer = new VBox(15);

        Runnable loadBookmarks = () -> {
            bookmarksContainer.getChildren().clear();
            List<Bookmark> list = bookmarkRepository.getUserBookmarks(user.getUserId());

            if (list.isEmpty()) {
                Label emptyLabel = new Label("You haven't bookmarked any study resources yet.\nVisit the Study Resources tab and click 'Save Bookmark' on items you want to keep handy.");
                emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-padding: 40px; -fx-line-spacing: 4px;");
                bookmarksContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Bookmark b : list) {
                Label titleLabel = new Label(b.getResourceTitle());
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                titleLabel.setTextFill(Color.web("#0f172a"));

                Label typeTag = new Label(b.getResourceType());
                typeTag.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #047857; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");

                Label subjectTag = new Label(b.getSubjectName());
                subjectTag.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-size: 11px;");

                HBox badges = new HBox(10, titleLabel, typeTag, subjectTag);
                badges.setAlignment(Pos.CENTER_LEFT);

                Label meta = new Label("Uploader: " + b.getUploaderName() + " • Bookmarked on " + b.getCreatedAt().toString().substring(0, 16));
                meta.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                Label desc = new Label(b.getDescription());
                desc.setWrapText(true);
                desc.setTextFill(Color.web("#334155"));

                Button openBtn = new Button("🌐 Open Resource");
                openBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
                openBtn.setOnAction(e -> openInBrowser(b.getResourceUrl()));

                Button removeBtn = new Button("🗑️ Remove Bookmark");
                removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-size: 12px; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
                removeBtn.setOnAction(e -> {
                    bookmarkRepository.removeBookmark(user.getUserId(), b.getResourceId());
                    showBookmarks(user);
                });

                HBox actions = new HBox(12, openBtn, removeBtn);

                VBox card = new VBox(10, badges, meta, desc, new Separator(), actions);
                card.setPadding(new Insets(18));
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0; -fx-border-radius: 10px;");
                bookmarksContainer.getChildren().add(card);
            }
        };

        loadBookmarks.run();

        VBox content = new VBox(20, bookmarksContainer);
        content.setPadding(new Insets(25));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
        stage.setTitle("Student Exchange - Bookmarks");
    }

    // ================= 8. PROFILE SCREEN =================

    private void showProfileView(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createSidebar(user, "My Profile"));
        layout.setTop(createTopBar(user, "My Profile"));

        VBox profileCard = new VBox(15);
        profileCard.setPadding(new Insets(30));
        profileCard.setMaxWidth(600);
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px;");

        Label cardHeader = new Label("Student Account Profile");
        cardHeader.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        cardHeader.setTextFill(Color.web("#0f172a"));

        GridPane details = new GridPane();
        details.setHgap(20);
        details.setVgap(12);

        details.add(new Label("Full Name:"), 0, 0);
        Label nameVal = new Label(user.getName());
        nameVal.setStyle("-fx-font-weight: bold;");
        details.add(nameVal, 1, 0);

        details.add(new Label("Email Address:"), 0, 1);
        Label emailVal = new Label(user.getEmail());
        emailVal.setStyle("-fx-font-weight: bold;");
        details.add(emailVal, 1, 1);

        details.add(new Label("User Role:"), 0, 2);
        Label roleVal = new Label(user.getRole());
        roleVal.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-padding: 2px 8px; -fx-background-radius: 4px; -fx-font-weight: bold;");
        details.add(roleVal, 1, 2);

        details.add(new Label("Account Status:"), 0, 3);
        Label statusVal = new Label(user.getAccountStatus());
        statusVal.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-padding: 2px 8px; -fx-background-radius: 4px; -fx-font-weight: bold;");
        details.add(statusVal, 1, 3);

        // Stats
        Map<String, Integer> stats = userRepository.getUserStats(user.getUserId());
        Label statsHeader = new Label("Activity Statistics");
        statsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statsHeader.setTextFill(Color.web("#0f172a"));

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(10);
        statsGrid.add(new Label("Discussions Started: " + stats.get("posts")), 0, 0);
        statsGrid.add(new Label("Replies Posted: " + stats.get("comments")), 1, 0);
        statsGrid.add(new Label("Resources Shared: " + stats.get("resources")), 0, 1);
        statsGrid.add(new Label("Saved Bookmarks: " + stats.get("bookmarks")), 1, 1);

        profileCard.getChildren().addAll(cardHeader, new Separator(), details, new Separator(), statsHeader, statsGrid);

        VBox content = new VBox(20, profileCard);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(35));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
    }

    // ================= 9. ADMIN MODERATION DASHBOARD =================

    private void showAdminDashboard(User user) {
        BorderPane layout = new BorderPane();
        layout.setLeft(createAdminSidebar(user, "Moderation"));
        layout.setTop(createTopBar(user, "Admin Moderation Panel"));

        Label queueHeader = new Label("Pending Content Reports Queue");
        queueHeader.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        queueHeader.setTextFill(Color.web("#0f172a"));

        Label queueSub = new Label("Review reported discussions and study resources flagged by students. Take moderation actions or dismiss.");
        queueSub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        VBox reportsContainer = new VBox(15);

        Runnable loadReports = () -> {
            reportsContainer.getChildren().clear();
            List<ContentReport> reports = reportRepository.getPendingReports(user.getUserId());

            if (reports.isEmpty()) {
                VBox emptyBox = new VBox(10);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(40));
                emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #e2e8f0;");
                Label cleanLabel = new Label("🎉 All Clean! No pending reports in the moderation queue.");
                cleanLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                cleanLabel.setTextFill(Color.web("#059669"));
                emptyBox.getChildren().add(cleanLabel);
                reportsContainer.getChildren().add(emptyBox);
                return;
            }

            for (ContentReport r : reports) {
                VBox reportCard = new VBox(12);
                reportCard.setPadding(new Insets(18));
                reportCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #fca5a5; -fx-border-width: 1px 1px 1px 4px;");

                Label rId = new Label("Report #" + r.getReportId() + " • Target: " + r.getTargetType());
                rId.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                rId.setTextFill(Color.web("#991b1b"));

                Label targetTitle = new Label("Reported Content: \"" + r.getTargetTitle() + "\"");
                targetTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");

                Label reasonLabel = new Label("Reason: " + r.getReason());
                reasonLabel.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #b91c1c; -fx-padding: 8px 12px; -fx-background-radius: 6px;");

                Label meta = new Label("Flagged by " + r.getReporterName() + " on " + r.getCreatedAt().toString().substring(0, 16));
                meta.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                Button dismissBtn = new Button("Dismiss Report");
                dismissBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
                dismissBtn.setOnAction(e -> {
                    boolean reviewed = reportRepository.reviewReport(r.getReportId(), user.getUserId(), "DISMISSED", false);
                    if (reviewed) {
                        showAlert(Alert.AlertType.INFORMATION, "Report Dismissed", "Report #" + r.getReportId() + " has been dismissed.");
                        showAdminDashboard(user);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not dismiss report.");
                    }
                });

                Button hideBtn = new Button("⚠️ Take Action (Hide Content)");
                hideBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
                hideBtn.setOnAction(e -> {
                    boolean actionTaken = reportRepository.reviewReport(r.getReportId(), user.getUserId(), "ACTION_TAKEN", true);
                    if (actionTaken) {
                        showAlert(Alert.AlertType.INFORMATION, "Action Taken", "Content hidden successfully and report marked as resolved!");
                        showAdminDashboard(user);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not complete moderation action.");
                    }
                });

                HBox actions = new HBox(12, dismissBtn, hideBtn);
                actions.setAlignment(Pos.CENTER_RIGHT);

                reportCard.getChildren().addAll(rId, targetTitle, reasonLabel, meta, new Separator(), actions);
                reportsContainer.getChildren().add(reportCard);
            }
        };

        loadReports.run();

        VBox content = new VBox(20, queueHeader, queueSub, reportsContainer);
        content.setPadding(new Insets(30));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");

        layout.setCenter(scroll);
        stage.setScene(new Scene(layout, 1050, 720));
        stage.setTitle("Student Exchange - Admin Moderation");
    }

    // ================= MODAL REPORT DIALOG =================

    private void openReportDialog(User user, Integer postId, Integer resourceId) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Report Content");
        dialog.setHeaderText("Report inappropriate, inaccurate, or offensive material for faculty/admin review.");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("State the specific reason for this report...");
        reasonArea.setPrefRowCount(4);
        reasonArea.setWrapText(true);

        VBox box = new VBox(10, new Label("Reason for reporting:"), reasonArea);
        box.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return reasonArea.getText().trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reason -> {
            if (reason.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Reason", "You must provide a reason when reporting content.");
                return;
            }

            boolean reported;
            if (postId != null) {
                reported = reportRepository.reportPost(user.getUserId(), postId, reason);
            } else if (resourceId != null) {
                reported = reportRepository.reportResource(user.getUserId(), resourceId, reason);
            } else {
                return;
            }

            if (reported) {
                showAlert(Alert.AlertType.INFORMATION, "Report Submitted", "Thank you. The report has been placed in the moderation queue.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Report Failed", "Could not submit report. Database error or inactive account.");
            }
        });
    }

    // ================= SIDEBAR & TOPBAR HELPERS =================

    private VBox createSidebar(User user, String activeScreen) {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(25, 16, 25, 16));
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: #0f172a;");

        Label logo = new Label("Student Exchange");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logo.setTextFill(Color.WHITE);

        Label sublogo = new Label("Research & Board System");
        sublogo.setFont(Font.font("Arial", 11));
        sublogo.setTextFill(Color.web("#94a3b8"));

        VBox brandBox = new VBox(2, logo, sublogo);

        Label navLabel = new Label("NAVIGATION");
        navLabel.setTextFill(Color.web("#64748b"));
        navLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        Button dashboardBtn = createSidebarNavButton("📊 Dashboard", "Dashboard".equals(activeScreen));
        dashboardBtn.setOnAction(e -> showDashboard(user));

        Button discBtn = createSidebarNavButton("💬 Discussion Board", "Discussion Board".equals(activeScreen));
        discBtn.setOnAction(e -> showDiscussionBoard(user));

        Button resBtn = createSidebarNavButton("📚 Study Resources", "Resources".equals(activeScreen));
        resBtn.setOnAction(e -> showResources(user));

        Button bookmarksBtn = createSidebarNavButton("⭐ My Bookmarks", "My Bookmarks".equals(activeScreen));
        bookmarksBtn.setOnAction(e -> showBookmarks(user));

        Button profileBtn = createSidebarNavButton("👤 My Profile", "My Profile".equals(activeScreen));
        profileBtn.setOnAction(e -> showProfileView(user));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandBox, new Separator(), navLabel, dashboardBtn, discBtn, resBtn, bookmarksBtn, profileBtn);

        // If admin, show switch to Admin Panel
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            Button adminBtn = createSidebarNavButton("🛡️ Admin Panel", false);
            adminBtn.setStyle("-fx-background-color: #831843; -fx-text-fill: white; -fx-background-radius: 6px; -fx-padding: 10px; -fx-cursor: hand; -fx-font-weight: bold;");
            adminBtn.setOnAction(e -> showAdminDashboard(user));
            sidebar.getChildren().add(adminBtn);
        }

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6px; -fx-padding: 10px; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been logged out successfully.");
            showLoginScreen();
        });

        sidebar.getChildren().addAll(spacer, logoutBtn);
        return sidebar;
    }

    private VBox createAdminSidebar(User user, String activeScreen) {
        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(25, 16, 25, 16));
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: #1e1b4b;");

        Label logo = new Label("Admin Center");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logo.setTextFill(Color.WHITE);

        Label sublogo = new Label("Faculty Moderation Panel");
        sublogo.setFont(Font.font("Arial", 11));
        sublogo.setTextFill(Color.web("#a5b4fc"));

        VBox brandBox = new VBox(2, logo, sublogo);

        Button queueBtn = createSidebarNavButton("🛡️ Moderation Queue", true);
        queueBtn.setOnAction(e -> showAdminDashboard(user));

        Button studentViewBtn = createSidebarNavButton("👁️ Student View Mode", false);
        studentViewBtn.setOnAction(e -> showDashboard(user));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6px; -fx-padding: 10px; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been logged out.");
            showLoginScreen();
        });

        sidebar.getChildren().addAll(brandBox, new Separator(), queueBtn, studentViewBtn, spacer, logoutBtn);
        return sidebar;
    }

    private Button createSidebarNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        if (active) {
            btn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10px 14px; -fx-background-radius: 6px; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 13px; -fx-padding: 10px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");
        }
        return btn;
    }

    private HBox createTopBar(User user, String title) {
        Label headerTitle = new Label(title);
        headerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        headerTitle.setTextFill(Color.web("#0f172a"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userBadge = new Label(user.getName() + " (" + user.getRole() + ")");
        userBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e3a8a; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 20px;");

        HBox topBar = new HBox(15, headerTitle, spacer, userBadge);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 25, 16, 25));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1px 0;");
        return topBar;
    }

    // ================= CARD & BUTTON HELPERS =================

    private VBox createStatCard(String title, String stat, String desc, String colorHex) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        titleLabel.setTextFill(Color.web(colorHex));

        Label statLabel = new Label(stat);
        statLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statLabel.setTextFill(Color.web("#0f172a"));

        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.web("#64748b"));
        descLabel.setFont(Font.font("Arial", 12));

        Label actionPrompt = new Label("Click to open →");
        actionPrompt.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        VBox card = new VBox(10, titleLabel, statLabel, descLabel, new Region(), actionPrompt);
        card.setPadding(new Insets(20));
        card.setPrefSize(340, 170);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-cursor: hand;");
        return card;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10px; -fx-cursor: hand;");
        return button;
    }

    private Button createSuccessButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        return button;
    }

    private void styleFormBox(VBox form) {
        form.setPadding(new Insets(25));
        form.setMaxWidth(420);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                getHostServices().showDocument(url);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.INFORMATION, "Resource Link", "Resource Link: " + url);
        }
    }

    // Subject Filter Item wrapper
    private static class SubjectFilterItem {
        final Integer subjectId;
        final String name;

        SubjectFilterItem(Integer id, String name) {
            this.subjectId = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // ================= MAIN LAUNCHER =================

    public static void main(String[] args) {
        launch(args);
    }
}

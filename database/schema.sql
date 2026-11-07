-- ========================================================
-- Student Board and Research Exchange System
-- Database Schema (MySQL 8.0+)
-- ========================================================

CREATE DATABASE IF NOT EXISTS `student_exchange`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `student_exchange`;

-- ========================================================
-- 1. USERS TABLE
-- ========================================================
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'STUDENT',
  `account_status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  CONSTRAINT `users_chk_1` CHECK (`role` IN ('STUDENT', 'ADMIN')),
  CONSTRAINT `users_chk_2` CHECK (`account_status` IN ('ACTIVE', 'SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 2. SUBJECTS TABLE
-- ========================================================
CREATE TABLE IF NOT EXISTS `subjects` (
  `subject_id` int NOT NULL AUTO_INCREMENT,
  `subject_name` varchar(100) NOT NULL,
  `course_code` varchar(30) DEFAULT NULL,
  `semester` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subject_id`),
  UNIQUE KEY `course_code` (`course_code`),
  CONSTRAINT `subjects_chk_1` CHECK (`semester` IS NULL OR `semester` BETWEEN 1 AND 12)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 3. POSTS TABLE (DISCUSSION BOARD)
-- ========================================================
CREATE TABLE IF NOT EXISTS `posts` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `subject_id` int DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `body` text NOT NULL,
  `post_type` varchar(30) NOT NULL DEFAULT 'QUESTION',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`),
  KEY `user_id` (`user_id`),
  KEY `subject_id` (`subject_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  CONSTRAINT `posts_chk_1` CHECK (`post_type` IN ('QUESTION', 'DISCUSSION', 'ANNOUNCEMENT')),
  CONSTRAINT `posts_chk_2` CHECK (`status` IN ('ACTIVE', 'HIDDEN', 'DELETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 4. COMMENTS TABLE (DISCUSSION COMMENTS & REPLIES)
-- ========================================================
CREATE TABLE IF NOT EXISTS `comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `parent_comment_id` int DEFAULT NULL,
  `comment_text` text NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  KEY `parent_comment_id` (`parent_comment_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`),
  CONSTRAINT `comments_chk_1` CHECK (`status` IN ('ACTIVE', 'HIDDEN', 'DELETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 5. RESOURCES TABLE (STUDY RESOURCES USING EXTERNAL LINKS)
-- ========================================================
CREATE TABLE IF NOT EXISTS `resources` (
  `resource_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `subject_id` int DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `description` text,
  `resource_type` varchar(30) NOT NULL,
  `file_path` varchar(500) DEFAULT NULL,
  `resource_url` varchar(1000) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`resource_id`),
  KEY `user_id` (`user_id`),
  KEY `subject_id` (`subject_id`),
  CONSTRAINT `resources_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `resources_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  CONSTRAINT `resources_chk_1` CHECK (`resource_type` IN ('NOTES', 'RESEARCH_PAPER', 'PRESENTATION', 'LAB_MATERIAL', 'REFERENCE_LINK', 'OTHER')),
  CONSTRAINT `resources_chk_2` CHECK (`status` IN ('ACTIVE', 'HIDDEN', 'DELETED')),
  CONSTRAINT `resources_chk_3` CHECK (((`file_path` IS NOT NULL AND `resource_url` IS NULL) OR (`file_path` IS NULL AND `resource_url` IS NOT NULL)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 6. BOOKMARKS TABLE (SAVED STUDY RESOURCES)
-- ========================================================
CREATE TABLE IF NOT EXISTS `bookmarks` (
  `bookmark_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `resource_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bookmark_id`),
  UNIQUE KEY `user_resource_unique` (`user_id`, `resource_id`),
  KEY `resource_id` (`resource_id`),
  CONSTRAINT `bookmarks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `bookmarks_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- 7. CONTENT REPORTS TABLE (MODERATION)
-- ========================================================
CREATE TABLE IF NOT EXISTS `content_reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `reporter_id` int NOT NULL,
  `post_id` int DEFAULT NULL,
  `resource_id` int DEFAULT NULL,
  `reason` varchar(500) NOT NULL,
  `report_status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `reviewed_by` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `reporter_id` (`reporter_id`),
  KEY `post_id` (`post_id`),
  KEY `resource_id` (`resource_id`),
  KEY `reviewed_by` (`reviewed_by`),
  CONSTRAINT `content_reports_ibfk_1` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `content_reports_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `content_reports_ibfk_3` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`resource_id`),
  CONSTRAINT `content_reports_ibfk_4` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `content_reports_chk_1` CHECK (((`post_id` IS NOT NULL AND `resource_id` IS NULL) OR (`post_id` IS NULL AND `resource_id` IS NOT NULL))),
  CONSTRAINT `content_reports_chk_2` CHECK (`report_status` IN ('PENDING', 'REVIEWED', 'DISMISSED', 'ACTION_TAKEN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- SEED DATA (CORE SUBJECTS & DEMO USERS)
-- ========================================================
INSERT IGNORE INTO `subjects` (`subject_id`, `subject_name`, `course_code`, `semester`) VALUES
(1, 'Java Programming', 'CSE201', 3),
(2, 'Database Management Systems', 'CSE202', 3),
(3, 'Data Structures and Algorithms', 'CSE203', 3),
(4, 'Operating Systems', 'CSE301', 5),
(5, 'Computer Networks', 'CSE302', 5);

-- Admin user (email: teststudent@example.com, password: TestPassword@123)
INSERT IGNORE INTO `users` (`user_id`, `name`, `email`, `password_hash`, `role`, `account_status`) VALUES
(1, 'Faculty Admin', 'teststudent@example.com', '$2a$12$e6V7R0I4vYlQfQoXq8w5g.bKxJjRz0s9qM8o8I1qB1Xv3tF4hP6eq', 'ADMIN', 'ACTIVE');

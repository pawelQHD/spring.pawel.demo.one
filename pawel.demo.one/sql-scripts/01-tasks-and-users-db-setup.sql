CREATE DATABASE IF NOT EXISTS `to_do_tracker`;
USE `to_do_tracker`;

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) UNIQUE,
  `enabled` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `tasks`
--
CREATE TABLE `tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `completed` TINYINT(1) DEFAULT 0,
  `priority` ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
  `category` VARCHAR(50),
  `due_date` DATE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_completed` (`completed`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
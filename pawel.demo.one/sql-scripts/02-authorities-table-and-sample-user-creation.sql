USE `to_do_tracker`;

DROP TABLE IF EXISTS `authorities`;

--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `authority` VARCHAR(50) NOT NULL,
  UNIQUE KEY `authorities_idx_1` (`username`, `authority`),
  CONSTRAINT `authorities_ibfk_1` 
    FOREIGN KEY (`username`) 
    REFERENCES `users` (`username`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Inserting data for table `authorities`
--

INSERT INTO `authorities` (username, authority)
VALUES 
('john','ROLE_USER'),
('pawel','ROLE_USER'),
('pawel','ROLE_ADMOIN');
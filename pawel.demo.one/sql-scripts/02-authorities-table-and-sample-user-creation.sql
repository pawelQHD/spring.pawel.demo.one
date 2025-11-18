USE `to_do_tracker`;

DROP TABLE IF EXISTS `authorities`;

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `enabled` tinyint NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Inserting data for table `users`
--

INSERT INTO `users` (username, password, email)
VALUES 
('john','{noop}test123','john@example.com'),
('pawel','{noop}test123','pawel@example.com');


--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
  CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Inserting data for table `authorities`
--

INSERT INTO `authorities` 
VALUES 
('john','ROLE_USER'),
('pawel','ROLE_USER'),
('pawel','ROLE_ADMOIN');
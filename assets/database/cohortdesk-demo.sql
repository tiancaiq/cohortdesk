-- Synthetic English-language demo data for a new cohortdesk_demo database.
CREATE DATABASE IF NOT EXISTS cohortdesk_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE cohortdesk_demo;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `clazz` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `room` varchar(20) DEFAULT NULL,
  `begin_date` date NOT NULL,
  `end_date` date NOT NULL,
  `master_id` int unsigned DEFAULT NULL,
  `subject` tinyint unsigned NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `dept` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `emp` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(50) DEFAULT '123456',
  `name` varchar(10) NOT NULL,
  `gender` tinyint unsigned NOT NULL,
  `phone` char(11) NOT NULL,
  `job` tinyint unsigned DEFAULT NULL,
  `salary` int unsigned DEFAULT NULL,
  `image` varchar(300) DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `dept_id` int unsigned DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `emp_expr` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `emp_id` int unsigned DEFAULT NULL,
  `begin` date DEFAULT NULL,
  `end` date DEFAULT NULL,
  `company` varchar(50) DEFAULT NULL,
  `job` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `emp_login_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(32) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `is_success` tinyint unsigned DEFAULT NULL,
  `jwt` varchar(1000) DEFAULT NULL,
  `cost_time` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `operate_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `operate_emp_id` int unsigned DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `class_name` varchar(100) DEFAULT NULL,
  `method_name` varchar(100) DEFAULT NULL,
  `method_params` varchar(1000) DEFAULT NULL,
  `return_value` varchar(2000) DEFAULT NULL,
  `cost_time` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `student` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `no` char(10) NOT NULL,
  `gender` tinyint unsigned NOT NULL,
  `phone` varchar(11) NOT NULL,
  `id_card` char(18) NOT NULL,
  `is_college` tinyint unsigned NOT NULL,
  `address` varchar(100) DEFAULT NULL,
  `degree` tinyint unsigned DEFAULT NULL,
  `graduation_date` date DEFAULT NULL,
  `clazz_id` int unsigned NOT NULL,
  `violation_count` tinyint unsigned NOT NULL DEFAULT '0',
  `violation_score` tinyint unsigned NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `no` (`no`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `id_card` (`id_card`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO dept (id, name, create_time, update_time) VALUES
(1, 'Operations', NOW(), NOW()),
(2, 'Curriculum', NOW(), NOW()),
(3, 'Admissions', NOW(), NOW()),
(4, 'Support', NOW(), NOW()),
(5, 'Finance', NOW(), NOW());

INSERT INTO emp (id, username, password, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) VALUES
(10, 'linchong', '123456', 'Alex Lee', 1, '55500000001', 1, 75000, NULL, '2022-08-15', 1, NOW(), NOW()),
(1, 'miachen', '123456', 'Mia Chen', 2, '55500000002', 2, 82000, NULL, '2023-02-01', 2, NOW(), NOW()),
(2, 'sampatel', '123456', 'Sam Patel', 1, '55500000003', 3, 88000, NULL, '2021-11-10', 4, NOW(), NOW()),
(3, 'norakim', '123456', 'Nora Kim', 2, '55500000004', 4, 94000, NULL, '2020-05-20', 2, NOW(), NOW()),
(4, 'owenpark', '123456', 'Owen Park', 1, '55500000005', 5, 70000, NULL, '2024-03-11', 3, NOW(), NOW()),
(5, 'lenaross', '123456', 'Lena Ross', 2, '55500000006', 2, 81000, NULL, '2023-09-04', 2, NOW(), NOW());

INSERT INTO emp_expr (id, emp_id, begin, end, company, job) VALUES
(1, 10, '2019-06-01', '2022-07-31', 'Northstar Learning', 'Program Coordinator'),
(2, 1, '2020-01-15', '2023-01-15', 'Lakeview Academy', 'Instructor'),
(3, 2, '2018-04-01', '2021-10-31', 'Summit Education', 'Student Services Lead'),
(4, 3, '2017-02-01', '2020-05-01', 'Cedar Training', 'Curriculum Designer');

INSERT INTO clazz (id, name, room, begin_date, end_date, master_id, subject, create_time, update_time) VALUES
(1, 'Java Foundations Fall 2026', 'A-101', '2026-09-01', '2026-12-15', 10, 1, NOW(), NOW()),
(2, 'Frontend Essentials', 'B-204', '2026-11-01', '2027-02-28', 1, 2, NOW(), NOW()),
(3, 'Data Engineering Spring 2026', 'C-310', '2026-02-01', '2026-06-30', 2, 3, NOW(), NOW()),
(4, 'Python Automation Fall 2026', 'A-103', '2026-08-20', '2026-12-20', 3, 4, NOW(), NOW()),
(5, 'Go Services Winter 2027', 'B-201', '2027-01-10', '2027-04-30', 4, 5, NOW(), NOW()),
(6, 'Embedded Systems 2026', 'Lab-1', '2026-07-01', '2026-11-30', 5, 6, NOW(), NOW());

INSERT INTO student (id, name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id, violation_count, violation_score, create_time, update_time) VALUES
(1, 'Jordan Lee', '2026000001', 1, '55510000001', 'DEMO00000000000001', 1, 'Portland, OR', 4, '2025-06-01', 1, 0, 0, NOW(), NOW()),
(2, 'Sofia Reed', '2026000002', 2, '55510000002', 'DEMO00000000000002', 1, 'Austin, TX', 4, '2024-06-01', 1, 0, 0, NOW(), NOW()),
(3, 'Avery Kim', '2026000003', 2, '55510000003', 'DEMO00000000000003', 0, 'Seattle, WA', 5, '2023-06-01', 2, 0, 0, NOW(), NOW()),
(4, 'Noah Patel', '2026000004', 1, '55510000004', 'DEMO00000000000004', 0, 'Denver, CO', 3, '2022-06-01', 2, 0, 0, NOW(), NOW()),
(5, 'Emma Chen', '2026000005', 2, '55510000005', 'DEMO00000000000005', 1, 'Boston, MA', 4, '2024-06-01', 3, 0, 0, NOW(), NOW()),
(6, 'Liam Park', '2026000006', 1, '55510000006', 'DEMO00000000000006', 0, 'Chicago, IL', 2, '2020-06-01', 3, 0, 0, NOW(), NOW()),
(7, 'Maya Ross', '2026000007', 2, '55510000007', 'DEMO00000000000007', 1, 'Phoenix, AZ', 4, '2025-06-01', 4, 0, 0, NOW(), NOW()),
(8, 'Ethan Cole', '2026000008', 1, '55510000008', 'DEMO00000000000008', 0, 'Raleigh, NC', 5, '2023-06-01', 4, 0, 0, NOW(), NOW()),
(9, 'Isla Gomez', '2026000009', 2, '55510000009', 'DEMO00000000000009', 1, 'Miami, FL', 3, '2021-06-01', 5, 0, 0, NOW(), NOW()),
(10, 'Leo Martin', '2026000010', 1, '55510000010', 'DEMO00000000000010', 0, 'Atlanta, GA', 4, '2024-06-01', 5, 0, 0, NOW(), NOW()),
(11, 'Zoe Turner', '2026000011', 2, '55510000011', 'DEMO00000000000011', 1, 'Madison, WI', 4, '2025-06-01', 6, 0, 0, NOW(), NOW()),
(12, 'Mila Davis', '2026000012', 2, '55510000012', 'DEMO00000000000012', 0, 'Boise, ID', 4, '2025-06-01', 0, 0, 0, NOW(), NOW());

INSERT INTO emp_login_log (id, username, password, login_time, is_success, jwt, cost_time) VALUES
(1, 'linchong', NULL, '2026-10-08 08:30:00', 1, NULL, 54),
(2, 'miachen', NULL, '2026-10-08 09:10:00', 1, NULL, 62),
(3, 'unknown', NULL, '2026-10-08 09:25:00', 0, NULL, 28),
(4, 'linchong', NULL, '2026-10-09 08:15:00', 1, NULL, 48),
(5, 'unknown', NULL, '2026-10-09 08:20:00', 0, NULL, 31);

INSERT INTO operate_log (id, operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time) VALUES
(1, 10, '2026-10-08 10:00:00', 'StudentController', 'save', '[]', 'success', 32),
(2, 1, '2026-10-08 10:30:00', 'ClazzController', 'update', '[]', 'success', 46),
(3, 10, '2026-10-09 09:00:00', 'EmpController', 'save', '[]', 'success', 39),
(4, 2, '2026-10-09 09:20:00', 'StudentController', 'update', '[]', 'success', 35),
(5, 3, '2026-10-09 09:45:00', 'DeptController', 'delete', '[]', 'success', 27);

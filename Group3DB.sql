-- Drop existing database and recreate cleanly
DROP DATABASE IF EXISTS iste330;
CREATE DATABASE iste330;
USE iste330;

-- USERS TABLE
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password_hash CHAR(40) NOT NULL, -- SHA-1 hash
    role ENUM('admin', 'client') NOT NULL
);

-- COURSES TABLE
CREATE TABLE courses (
    course_id VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    instructor VARCHAR(100)
);

-- ENROLLMENTS TABLE
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    course_id VARCHAR(20),
    grade VARCHAR(5),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ACCOUNTS TABLE (Credits/Balance)
CREATE TABLE accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE,
    balance DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- TRANSACTIONS TABLE
CREATE TABLE transactions (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    from_user VARCHAR(50),
    to_user VARCHAR(50),
    amount DECIMAL(10,2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user) REFERENCES users(user_id),
    FOREIGN KEY (to_user) REFERENCES users(user_id)
);

-- Insert sample users
INSERT INTO users (user_id, name, password_hash, role) VALUES
('admin1', 'Alice Admin', SHA1('admin123'), 'admin'),
('stu1', 'John Student', SHA1('student1'), 'client'),
('stu2', 'Mary Learner', SHA1('student2'), 'client');

-- Insert sample accounts
INSERT INTO accounts (account_id, user_id, balance) VALUES
('acc1', 'stu1', 150.00),
('acc2', 'stu2', 200.00);

-- Insert sample courses
INSERT INTO courses (course_id, course_name, instructor) VALUES
('CSE101', 'Intro to CS', 'Dr. Smith'),
('MAT202', 'Linear Algebra', 'Prof. Davis');

-- Insert sample enrollments
INSERT INTO enrollments (user_id, course_id, grade) VALUES
('stu1', 'CSE101', 'A'),
('stu1', 'MAT202', 'B+'),
('stu2', 'CSE101', 'A-');

-- Insert sample transactions
INSERT INTO transactions (from_user, to_user, amount) VALUES
('stu1', 'stu2', 20.00),
('stu2', 'stu1', 10.00);

-- Stored Procedure for Credit Transfer
DELIMITER //

CREATE PROCEDURE transfer_credits(IN fromUser VARCHAR(50), IN toUser VARCHAR(50), IN amt DECIMAL(10,2))
BEGIN
    DECLARE fromBalance DECIMAL(10,2);

    START TRANSACTION;

    SELECT balance INTO fromBalance FROM accounts WHERE user_id = fromUser FOR UPDATE;

    IF fromBalance >= amt THEN
        UPDATE accounts SET balance = balance - amt WHERE user_id = fromUser;
        UPDATE accounts SET balance = balance + amt WHERE user_id = toUser;
        INSERT INTO transactions (from_user, to_user, amount) VALUES (fromUser, toUser, amt);
        COMMIT;
    ELSE
        ROLLBACK;
    END IF;
END //

DELIMITER ;
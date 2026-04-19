
-- Course table
CREATE TABLE IF NOT EXISTS course (
    code INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL
);

-- Student table
CREATE TABLE IF NOT EXISTS student (
    roll_number INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    aadhar_card_number VARCHAR(12) NOT NULL UNIQUE,
    course_code INT,
    gender VARCHAR(10) NOT NULL,
    date_of_birth DATE NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course(code)
);

-- Sample Data
INSERT INTO course (title) VALUES ('Computer Science'), ('Information Technology');
INSERT INTO student (roll_number, first_name, last_name, aadhar_card_number, course_code, gender, date_of_birth) 
VALUES (101, 'Bobby', 'Tables', '123456789012', 1, 'Male', '2005-01-01');

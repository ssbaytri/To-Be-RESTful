INSERT INTO users (first_name, last_name, role, login, password)
VALUES ('Best', 'Teacher', 'TEACHER', 'bteacher', 'teacher123'),
       ('Second', 'Teacher', 'TEACHER', 'steacher', 'teacher123'),
       ('Alice', 'Student', 'STUDENT', 'astudent', 'student123'),
       ('Bob', 'Student', 'STUDENT', 'bstudent', 'student123'),
       ('Admin', 'Istrator', 'ADMINISTRATOR', 'admin', 'admin123') ON CONFLICT (login) DO NOTHING;
-- Creation a database. 
CREATE DATABASE `sena_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Creation an user.for the database. 
GRANT ALL PRIVILEGES ON `sena_db`.* TO username@localhost IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON `sena_db`.* TO username@'%' IDENTIFIED BY 'password';

-- Reflects the all privileges as above
FLUSH PRIVILEGES;

-- Crear tabla users
DROP TABLE IF EXISTS users;
CREATE TABLE users (
                       user_id INT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL,
                       surname VARCHAR(50) NOT NULL,
                       password CHAR(68) NOT NULL,
                       email VARCHAR(68) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (user_id),
                       UNIQUE (email)
);

-- Crear tabla permissions
DROP TABLE IF EXISTS permissions;
CREATE TABLE permissions (
                             permission_id INT NOT NULL AUTO_INCREMENT,
                             permission_name VARCHAR(50),
                             PRIMARY KEY (permission_id),
                             UNIQUE (permission_name)
);

-- Crear tabla roles
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
                       role_id INT NOT NULL AUTO_INCREMENT,
                       role_name VARCHAR(50),
                       PRIMARY KEY (role_id),
                       UNIQUE (role_name)
);

-- Crear tabla role_permission
DROP TABLE IF EXISTS role_permission;
CREATE TABLE role_permission (
                                 role_id INT NOT NULL,
                                 permission_id INT NOT NULL,
                                 PRIMARY KEY (role_id, permission_id),
                                 FOREIGN KEY (role_id) REFERENCES roles (role_id),
                                 FOREIGN KEY (permission_id) REFERENCES permissions (permission_id)
);

-- Crear tabla user_role
DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
                           user_id INT NOT NULL,
                           role_id INT NOT NULL,
                           PRIMARY KEY (user_id, role_id),
                           FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
                           FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

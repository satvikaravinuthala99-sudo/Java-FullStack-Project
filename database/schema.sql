CREATE DATABASE IF NOT EXISTS echovault_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE echovault_db;
CREATE TABLE IF NOT EXISTS users (
 id INT PRIMARY KEY AUTO_INCREMENT, full_name VARCHAR(100) NOT NULL,
 email VARCHAR(190) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS admins (
 id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS capsules (
 id INT PRIMARY KEY AUTO_INCREMENT, owner_id INT NOT NULL,
 title VARCHAR(150) NOT NULL, description TEXT,
 recipient_email VARCHAR(190), visibility ENUM('PRIVATE','SHARED') NOT NULL DEFAULT 'PRIVATE',
 status ENUM('SEALED','OPEN') NOT NULL DEFAULT 'SEALED', unlock_at DATETIME NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_capsule_owner FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE,
 INDEX idx_capsule_unlock(unlock_at), INDEX idx_capsule_recipient(recipient_email)
);
CREATE TABLE IF NOT EXISTS memories (
 id INT PRIMARY KEY AUTO_INCREMENT, capsule_id INT NOT NULL,
 original_name VARCHAR(255) NOT NULL, stored_name VARCHAR(100) NOT NULL UNIQUE,
 mime_type VARCHAR(120), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_memory_capsule FOREIGN KEY(capsule_id) REFERENCES capsules(id) ON DELETE CASCADE
);
-- The application creates a first admin on startup if admins is empty.
-- Demo login: admin / EchoAdmin@123 (change this before any real deployment).

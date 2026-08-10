CREATE TABLE roles(
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(16) NOT NULL UNIQUE
);

-- =============================
-- 2) Create table roles
-- =============================
-- Distribution:
-- Two roles only
INSERT INTO roles (name)
VALUES ('ROLE_USER'), ('ROLE_ADMIN');

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name TEXT NOT NULL,
                       surname TEXT NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       nick VARCHAR(25) NOT NULL UNIQUE,
                       password VARCHAR(60),
                       verified BOOLEAN DEFAULT FALSE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_roles(
                           user_id BIGINT NOT NULL,
                           role_id BIGINT NOT NULL,

                           CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),

                           CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
                               REFERENCES users (id) ON DELETE CASCADE,

                           CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
                               REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE email_verification_token(
                                         id BIGSERIAL PRIMARY KEY,
                                         token VARCHAR(36) UNIQUE NOT NULL,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                         expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '1 day') NOT NULL,
                                         used BOOLEAN DEFAULT FALSE NOT NULL,
                                         user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,
                               token VARCHAR(36) UNIQUE NOT NULL,
                               revoked BOOLEAN DEFAULT FALSE NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '7 day') NOT NULL,
                               user_id BIGINT NOT NULL REFERENCES users(id)
);


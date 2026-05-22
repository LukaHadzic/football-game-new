CREATE TABLE roles(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES
                             ('ROLE_USER'),
                             ('ROLE_ADMIN');
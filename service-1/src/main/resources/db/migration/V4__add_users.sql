CREATE TABLE users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    login    VARCHAR(20)                             NOT NULL,
    email    VARCHAR(50)                             NOT NULL,
    password VARCHAR(120),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role    VARCHAR(255)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_login UNIQUE (login);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);
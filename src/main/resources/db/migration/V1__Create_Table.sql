CREATE TABLE users (
                    id CHAR(36) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    first_name VARCHAR(255) NOT NULL,
                    last_name VARCHAR(255) NOT NULL,
                    role VARCHAR(255) NOT NULL,
                    gender BOOLEAN NOT NULL,
                    phone_number VARCHAR(255) NOT NULL,
                    date_of_birth TIMESTAMP NOT NULL,
                    mail VARCHAR(255) NOT NULL,
                    avatar VARCHAR(255) NULL,
                    enable BOOLEAN NOT NULL,
                    address VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


DROP TABLE IF EXISTS `posts`;
CREATE TABLE posts (
                    id CHAR(36) PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    body TEXT NOT NULL ,
                    status VARCHAR(255) NOT NULL ,
                    total_like INT NOT NULL ,
                    total_comment INT NOT NULL ,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_by CHAR(36),
                    FOREIGN KEY (created_by) REFERENCES users(id));


DROP TABLE IF EXISTS medias;
CREATE TABLE medias (
                    id CHAR(36) PRIMARY KEY,
                    base_name VARCHAR(255) NOT NULL,
                    public_url VARCHAR(255) NOT NULL,
                    post_id CHAR(36),
                    FOREIGN KEY (post_id) REFERENCES posts(id));


DROP TABLE IF EXISTS favorites;
CREATE TABLE favorites (
                           post_id CHAR(36),
                           user_id CHAR(36),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (post_id) REFERENCES posts(id),
                           FOREIGN KEY (user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS `comments`;
CREATE TABLE comments (
                         id CHAR(36) PRIMARY KEY,
                         content VARCHAR(255) NOT NULL,
                         post_id CHAR(36),
                         total_like INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by CHAR(36),
                         FOREIGN KEY (created_by) REFERENCES users(id),
                         FOREIGN KEY (post_id) REFERENCES posts(id));


DROP TABLE IF EXISTS `reactions`;
CREATE TABLE reactions (
                        id CHAR(36) PRIMARY KEY,
                        object_type VARCHAR(255) NOT NULL,
                        object_id CHAR(36) NOT NULL,
                        type INT NOT NULL,
                        created_by CHAR(36) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (created_by) REFERENCES users(id));


DROP TABLE IF EXISTS `follows`;
CREATE TABLE follows (
                    following_user_id CHAR(36),
                    user_id CHAR(36),
                    FOREIGN KEY (following_user_id) REFERENCES users(id),
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


DROP TABLE IF EXISTS shares;
CREATE TABLE shares (
                        id CHAR(36) PRIMARY KEY,
                        post_id CHAR(36) NOT NULL,
                        user_id CHAR(36) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (post_id) REFERENCES posts(id),
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

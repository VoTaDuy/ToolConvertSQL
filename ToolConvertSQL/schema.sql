-- CREATE DATABASE
CREATE DATABASE film_db;
USE film_db;

-- DIRECTORS
CREATE TABLE directors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100)
);

-- MOVIES
CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INT,
    rating FLOAT,
    duration_minutes INT,
    director_id INT,
    FOREIGN KEY (director_id) REFERENCES directors(id)
);

-- ACTORS
CREATE TABLE actors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100)
);

-- GENRES
CREATE TABLE genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- MOVIE_ACTOR
CREATE TABLE movie_actor (
    movie_id INT,
    actor_id INT,
    role_name VARCHAR(255),
    PRIMARY KEY (movie_id, actor_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (actor_id) REFERENCES actors(id)
);

-- MOVIE_GENRE
CREATE TABLE movie_genre (
    movie_id INT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- REVIEWS
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    reviewer_name VARCHAR(255),
    score FLOAT,
    comment TEXT,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);
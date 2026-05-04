drop  database film_db;
create database film_db;
USE film_db;

DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS movie_genre;
DROP TABLE IF EXISTS movie_actor;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS directors;
-- DIRECTORS
CREATE TABLE directors (
    director_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100)
);

-- MOVIES
CREATE TABLE movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INT,
    rating FLOAT,
    duration_minutes INT,
    director_id INT,
    FOREIGN KEY (director_id) REFERENCES directors(director_id)
);

-- ACTORS
CREATE TABLE actors(
    actor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100)
);

-- GENRES
CREATE TABLE genres (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- MOVIE_ACTOR
CREATE TABLE movie_actor (
    movie_id INT,
    actor_id INT,
    role_name VARCHAR(255),
    PRIMARY KEY (movie_id, actor_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (actor_id) REFERENCES actors(actor_id)
);

-- MOVIE_GENRE
CREATE TABLE movie_genre (
    movie_id INT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

-- REVIEWS
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    reviewer_name VARCHAR(255),
    score FLOAT,
    comment TEXT,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);
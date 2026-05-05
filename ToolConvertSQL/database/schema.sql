CREATE DATABASE IF NOT EXISTS movie_db;
USE movie_db;

-- USERS
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(255),
  email VARCHAR(255),
  created_at DATETIME
);

-- MOVIES
CREATE TABLE movies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  description TEXT,
  release_year INT,
  duration_minutes INT,
  age_rating VARCHAR(20),
  language VARCHAR(100),
  country VARCHAR(100),
  created_at DATETIME
);

-- GENRES
CREATE TABLE genres (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100)
);

-- ACTORS
CREATE TABLE actors (
  id INT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(255),
  birth_year INT,
  country VARCHAR(100)
);

-- DIRECTORS
CREATE TABLE directors (
  id INT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(255),
  country VARCHAR(100)
);

-- MOVIE_GENRES
CREATE TABLE movie_genres (
  movie_id INT,
  genre_id INT,
  PRIMARY KEY (movie_id, genre_id),
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- MOVIE_ACTORS
CREATE TABLE movie_actors (
  movie_id INT,
  actor_id INT,
  role_name VARCHAR(255),
  PRIMARY KEY (movie_id, actor_id),
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (actor_id) REFERENCES actors(id)
);

-- MOVIE_DIRECTORS
CREATE TABLE movie_directors (
  movie_id INT,
  director_id INT,
  PRIMARY KEY (movie_id, director_id),
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (director_id) REFERENCES directors(id)
);

-- REVIEWS
CREATE TABLE reviews (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  movie_id INT,
  rating DECIMAL(3,1),
  comment TEXT,
  created_at DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- FAVORITES
CREATE TABLE favorites (
  user_id INT,
  movie_id INT,
  created_at DATETIME,
  PRIMARY KEY (user_id, movie_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (movie_id) REFERENCES movies(id)
);
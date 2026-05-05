USE movie_db;

INSERT INTO users (full_name, email, created_at) VALUES
('Alice Johnson', 'alice@gmail.com', NOW()),
('Bob Smith', 'bob@gmail.com', NOW()),
('Charlie Brown', 'charlie@gmail.com', NOW());

INSERT INTO directors (full_name, country) VALUES
('Christopher Nolan', 'United Kingdom'),
('Steven Spielberg', 'United States'),
('Quentin Tarantino', 'United States');

-- MOVIES
INSERT INTO movies (title, description, release_year, duration_minutes, age_rating, language, country, created_at) VALUES
('Inception', 'Dream within a dream concept', 2010, 148, 'PG-13', 'English', 'USA', NOW()),
('Interstellar', 'Space exploration and time dilation', 2014, 169, 'PG-13', 'English', 'USA', NOW()),
('Django Unchained', 'A freed slave becomes bounty hunter', 2012, 165, 'R', 'English', 'USA', NOW()),
('Jurassic Park', 'Dinosaurs theme park disaster', 1993, 127, 'PG-13', 'English', 'USA', NOW()),
('The Dark Knight', 'Batman vs Joker', 2008, 152, 'PG-13', 'English', 'USA', NOW());

-- GENRES
INSERT INTO genres (name) VALUES
('Action'),
('Drama'),
('Sci-Fi'),
('Adventure');

-- ACTORS
INSERT INTO actors (full_name, birth_year, country) VALUES
('Leonardo DiCaprio', 1974, 'United States'),
('Matthew McConaughey', 1969, 'United States'),
('Christian Bale', 1974, 'United Kingdom'),
('Samuel L. Jackson', 1948, 'United States');

-- MOVIE_DIRECTORS
INSERT INTO movie_directors (movie_id, director_id) VALUES
(1, 1),
(2, 1),
(3, 3),
(4, 2),
(5, 1);

-- MOVIE_ACTORS
INSERT INTO movie_actors (movie_id, actor_id, role_name) VALUES
(1, 1, 'Cobb'),
(2, 2, 'Cooper'),
(5, 3, 'Batman'),
(3, 4, 'Stephen');

-- MOVIE_GENRES
INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1, 3),
(2, 3),
(5, 1),
(4, 4),
(3, 2);

-- REVIEWS
INSERT INTO reviews (user_id, movie_id, rating, comment, created_at) VALUES
(1, 1, 9.0, 'Great movie', NOW()),
(2, 1, 8.5, 'Mind-blowing', NOW()),
(3, 2, 9.2, 'Amazing visuals', NOW()),
(1, 5, 9.5, 'Best Batman movie', NOW()),
(2, 3, 8.0, 'Very good', NOW());

-- FAVORITES
INSERT INTO favorites (user_id, movie_id, created_at) VALUES
(1, 1, NOW()),
(1, 2, NOW()),
(2, 5, NOW()),
(3, 3, NOW());
USE film_db;

-- DIRECTORS
INSERT INTO directors (name, nationality) VALUES
('Christopher Nolan', 'United Kingdom'),
('Steven Spielberg', 'United States'),
('Quentin Tarantino', 'United States');

-- MOVIES
INSERT INTO movies (title, release_year, rating, duration_minutes, director_id) VALUES
('Inception', 2010, 8.8, 148, 1),
('Interstellar', 2014, 8.6, 169, 1),
('Django Unchained', 2012, 8.4, 165, 3),
('Jurassic Park', 1993, 8.1, 127, 2),
('The Dark Knight', 2008, 9.0, 152, 1);

-- ACTORS
INSERT INTO actors (name, nationality) VALUES
('Leonardo DiCaprio', 'United States'),
('Matthew McConaughey', 'United States'),
('Christian Bale', 'United Kingdom'),
('Samuel L. Jackson', 'United States');

-- GENRES
INSERT INTO genres (name) VALUES
('Action'),
('Drama'),
('Sci-Fi'),
('Adventure');

-- MOVIE_ACTOR
INSERT INTO movie_actor VALUES
(1, 1, 'Cobb'),
(2, 2, 'Cooper'),
(5, 3, 'Batman'),
(3, 4, 'Stephen');

-- MOVIE_GENRE
INSERT INTO movie_genre VALUES
(1, 3),
(2, 3),
(5, 1),
(4, 4),
(3, 2);

-- REVIEWS
INSERT INTO reviews (movie_id, reviewer_name, score, comment) VALUES
(1, 'Alice', 9.0, 'Great movie'),
(1, 'Bob', 8.5, 'Mind-blowing'),
(2, 'Charlie', 9.2, 'Amazing visuals'),
(5, 'David', 9.5, 'Best Batman movie'),
(3, 'Eve', 8.0, 'Very good');
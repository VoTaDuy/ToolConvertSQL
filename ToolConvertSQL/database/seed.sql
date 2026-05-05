USE movie_db;

-- ======================
-- USERS
-- ======================
INSERT INTO users (full_name, email, created_at) VALUES
('Nguyen Van A', 'a@gmail.com', NOW()),
('Tran Thi B', 'b@gmail.com', NOW()),
('Le Van C', 'c@gmail.com', NOW());

-- ======================
-- MOVIES
-- ======================
INSERT INTO movies (title, description, release_year, duration_minutes, age_rating, language, country, avg_rating, created_at) VALUES
('Inception', 'Dream manipulation movie', 2010, 148, 'PG-13', 'English', 'USA', 0, NOW()),
('Interstellar', 'Space exploration', 2014, 169, 'PG-13', 'English', 'USA', 0, NOW()),
('The Dark Knight', 'Batman vs Joker', 2008, 152, 'PG-13', 'English', 'USA', 0, NOW()),
('Parasite', 'Social class conflict', 2019, 132, 'R', 'Korean', 'Korea', 0, NOW());

-- ======================
-- GENRES
-- ======================
INSERT INTO genres (name) VALUES
('Action'),
('Drama'),
('Sci-Fi'),
('Thriller');

-- ======================
-- ACTORS
-- ======================
INSERT INTO actors (full_name, birth_year, country) VALUES
('Leonardo DiCaprio', 1974, 'USA'),
('Joseph Gordon-Levitt', 1981, 'USA'),
('Matthew McConaughey', 1969, 'USA'),
('Christian Bale', 1974, 'UK');

-- ======================
-- DIRECTORS
-- ======================
INSERT INTO directors (full_name, country) VALUES
('Christopher Nolan', 'UK'),
('Bong Joon-ho', 'Korea');

-- ======================
-- MOVIE_GENRES
-- ======================
INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1, 1),
(1, 3),
(2, 3),
(2, 2),
(3, 1),
(3, 2),
(4, 2);

-- ======================
-- MOVIE_ACTORS
-- ======================
INSERT INTO movie_actors (movie_id, actor_id, role_name) VALUES
(1, 1, 'Cobb'),
(1, 2, 'Arthur'),
(2, 3, 'Cooper'),
(3, 4, 'Batman');

-- ======================
-- MOVIE_DIRECTORS
-- ======================
INSERT INTO movie_directors (movie_id, director_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 2);

-- ======================
-- REVIEWS
-- ======================
INSERT INTO reviews (user_id, movie_id, rating, comment, created_at) VALUES
(1, 1, 9.0, 'Amazing movie', NOW()),
(2, 1, 8.5, 'Very good', NOW()),
(1, 2, 8.7, 'Great visuals', NOW()),
(3, 3, 9.5, 'Masterpiece', NOW()),
(2, 4, 8.0, 'Very deep meaning', NOW());

-- ======================
-- FAVORITES
-- ======================
INSERT INTO favorites (user_id, movie_id, created_at) VALUES
(1, 1, NOW()),
(1, 2, NOW()),
(2, 3, NOW()),
(3, 4, NOW());

-- ======================
-- SYNC AVG RATING
-- ======================
UPDATE movies m
JOIN (
    SELECT movie_id, AVG(rating) AS avg_r
    FROM reviews
    GROUP BY movie_id
) r ON m.id = r.movie_id
SET m.avg_rating = r.avg_r;
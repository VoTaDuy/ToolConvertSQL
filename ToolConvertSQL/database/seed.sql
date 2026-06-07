USE movie_db;


INSERT INTO users (full_name, email, created_at)
VALUES
('Nguyen Van An','an@gmail.com',NOW()),
('Tran Thi Bich','bich@gmail.com',NOW()),
('Le Quang Huy','huy@gmail.com',NOW()),
('Pham Minh Tuan','tuan@gmail.com',NOW()),
('Hoang Gia Bao','bao@gmail.com',NOW()),
('Vo Thanh Dat','dat@gmail.com',NOW()),
('Nguyen Thu Ha','ha@gmail.com',NOW()),
('Tran Ngoc Anh','ngocanh@gmail.com',NOW()),
('Le Minh Chau','chau@gmail.com',NOW()),
('Pham Quoc Viet','viet@gmail.com',NOW()),
('Do Tuan Kiet','kiet@gmail.com',NOW()),
('Bui Thanh Long','long@gmail.com',NOW()),
('Dang Hoang Nam','nam@gmail.com',NOW()),
('Phan Minh Quan','quan@gmail.com',NOW()),
('Ngo Duc Anh','ducanh@gmail.com',NOW()),
('Truong Gia Huy','giahuy@gmail.com',NOW()),
('Mai Khanh Linh','linh@gmail.com',NOW()),
('Nguyen Bao Ngoc','baongoc@gmail.com',NOW()),
('Tran Duc Minh','ducminh@gmail.com',NOW()),
('Le Hoang Phuc','phuc@gmail.com',NOW());


INSERT INTO movies
(title, description, release_year, duration_minutes, age_rating, language, country, avg_rating, created_at)
VALUES
('Inception', 'Dream manipulation movie', 2010, 148, 'PG-13', 'English', 'USA', 0, NOW()),
('Interstellar', 'Space exploration', 2014, 169, 'PG-13', 'English', 'USA', 0, NOW()),
('The Dark Knight', 'Batman versus Joker', 2008, 152, 'PG-13', 'English', 'USA', 0, NOW()),
('Parasite', 'Social class conflict', 2019, 132, 'R', 'Korean', 'South Korea', 0, NOW()),
('Your Name', 'Body swap romance', 2016, 106, 'PG', 'Japanese', 'Japan', 0, NOW()),
('Spirited Away', 'Fantasy adventure', 2001, 125, 'PG', 'Japanese', 'Japan', 0, NOW()),
('Train to Busan', 'Zombie survival', 2016, 118, 'R', 'Korean', 'South Korea', 0, NOW()),
('Oldboy', 'Mystery revenge thriller', 2003, 120, 'R', 'Korean', 'South Korea', 0, NOW()),
('Amelie', 'Romantic comedy in Paris', 2001, 122, 'R', 'French', 'France', 0, NOW()),
('The Intouchables', 'Friendship drama', 2011, 112, 'PG-13', 'French', 'France', 0, NOW()),
('City of God', 'Crime drama in Brazil', 2002, 130, 'R', 'Portuguese', 'Brazil', 0, NOW()),
('Crouching Tiger Hidden Dragon', 'Martial arts epic', 2000, 120, 'PG-13', 'Chinese', 'China', 0, NOW()),
('Dune', 'Sci-fi epic', 2021, 155, 'PG-13', 'English', 'Canada', 0, NOW()),
('Avatar', 'Pandora adventure', 2009, 162, 'PG-13', 'English', 'USA', 0, NOW()),
('Titanic', 'Romantic disaster film', 1997, 194, 'PG-13', 'English', 'USA', 0, NOW()),
('Fight Club', 'Underground fighting club', 1999, 139, 'R', 'English', 'USA', 0, NOW()),
('The Matrix', 'Virtual reality rebellion', 1999, 136, 'R', 'English', 'Australia', 0, NOW()),
('Joker', 'Origin of Joker', 2019, 122, 'R', 'English', 'USA', 0, NOW()),
('Whiplash', 'Jazz drummer story', 2014, 106, 'R', 'English', 'USA', 0, NOW()),
('The Batman', 'Dark detective story', 2022, 176, 'PG-13', 'English', 'USA', 0, NOW());

-- ======================
-- ACTORS (20)
-- ======================
INSERT INTO actors (full_name, birth_year, country)
VALUES
('Leonardo DiCaprio', 1974, 'USA'),
('Joseph Gordon-Levitt', 1981, 'USA'),
('Matthew McConaughey', 1969, 'USA'),
('Christian Bale', 1974, 'United Kingdom'),
('Robert Downey Jr.', 1965, 'USA'),
('Chris Evans', 1981, 'USA'),
('Tom Holland', 1996, 'United Kingdom'),
('Brad Pitt', 1963, 'USA'),
('Morgan Freeman', 1937, 'USA'),
('Song Kang-ho', 1967, 'South Korea'),
('Choi Min-sik', 1962, 'South Korea'),
('Timothee Chalamet', 1995, 'USA'),
('Zendaya', 1996, 'USA'),
('Scarlett Johansson', 1984, 'USA'),
('Keanu Reeves', 1964, 'Canada'),
('Ryan Gosling', 1980, 'Canada'),
('Emma Stone', 1988, 'USA'),
('Tom Cruise', 1962, 'USA'),
('Jackie Chan', 1954, 'China'),
('Donnie Yen', 1963, 'Hong Kong');

-- ======================
-- DIRECTORS (20)
-- ======================
INSERT INTO directors (full_name, country)
VALUES
('Christopher Nolan', 'United Kingdom'),
('Bong Joon-ho', 'South Korea'),
('James Cameron', 'Canada'),
('Steven Spielberg', 'USA'),
('Quentin Tarantino', 'USA'),
('Denis Villeneuve', 'Canada'),
('Hayao Miyazaki', 'Japan'),
('Park Chan-wook', 'South Korea'),
('Ridley Scott', 'United Kingdom'),
('Martin Scorsese', 'USA'),
('Peter Jackson', 'New Zealand'),
('David Fincher', 'USA'),
('Guy Ritchie', 'United Kingdom'),
('Ang Lee', 'Taiwan'),
('Luc Besson', 'France'),
('Wong Kar-wai', 'Hong Kong'),
('Zhang Yimou', 'China'),
('Greta Gerwig', 'USA'),
('Damien Chazelle', 'France'),
('Todd Phillips', 'USA');

-- ======================
-- GENRES (20)
-- ======================
INSERT INTO genres (name)
VALUES
('Action'),
('Drama'),
('Sci-Fi'),
('Thriller'),
('Comedy'),
('Romance'),
('Fantasy'),
('Adventure'),
('Crime'),
('Mystery'),
('Animation'),
('Horror'),
('Biography'),
('Family'),
('War'),
('History'),
('Sport'),
('Music'),
('Documentary'),
('Martial Arts');


-- ======================
-- MOVIE_DIRECTORS
-- ======================

INSERT INTO movie_directors (movie_id, director_id) VALUES
(1,1),
(2,1),
(3,1),
(4,2),
(5,7),
(6,7),
(7,2),
(8,8),
(9,15),
(10,15),
(11,10),
(12,14),
(13,6),
(14,3),
(15,3),
(16,12),
(17,4),
(18,20),
(19,19),
(20,20);

-- ======================
-- MOVIE_ACTORS
-- ======================

INSERT INTO movie_actors (movie_id, actor_id, role_name) VALUES
(1,1,'Cobb'),
(1,2,'Arthur'),

(2,3,'Cooper'),
(2,9,'Professor Brand'),

(3,4,'Bruce Wayne'),

(4,10,'Kim Ki-taek'),

(5,16,'Taki'),
(5,17,'Mitsuha'),

(6,16,'Haku'),

(7,10,'Seok-woo'),

(8,11,'Oh Dae-su'),

(9,17,'Amelie'),

(10,9,'Philippe'),

(11,8,'Ze Pequeno'),

(12,20,'Li Mu Bai'),

(13,12,'Paul Atreides'),
(13,13,'Chani'),

(14,5,'Jake Sully'),

(15,1,'Jack Dawson'),

(16,8,'Tyler Durden'),

(17,15,'Neo'),

(18,18,'Arthur Fleck'),

(19,17,'Andrew Neiman'),

(20,7,'Bruce Wayne');

-- ======================
-- MOVIE_GENRES
-- ======================

INSERT INTO movie_genres (movie_id, genre_id) VALUES
(1,1),(1,3),(1,4),
(2,3),(2,2),
(3,1),(3,9),
(4,2),(4,4),
(5,6),(5,7),
(6,7),(6,11),
(7,1),(7,12),
(8,4),(8,9),
(9,5),(9,6),
(10,2),(10,5),
(11,9),(11,2),
(12,20),(12,1),
(13,3),(13,8),
(14,3),(14,8),
(15,2),(15,6),
(16,2),(16,4),
(17,3),(17,1),
(18,2),(18,9),
(19,2),(19,18),
(20,1),(20,4);


-- ======================
-- SAMPLE REVIEWS (20)
-- ======================

INSERT INTO reviews
(user_id,movie_id,rating,comment,created_at)
VALUES
(1,1,9.5,'Amazing movie',NOW()),
(2,1,9.0,'Mind blowing',NOW()),
(3,2,8.9,'Great visuals',NOW()),
(4,3,9.7,'Best Batman movie',NOW()),
(5,4,9.2,'Excellent story',NOW()),
(6,5,8.8,'Very emotional',NOW()),
(7,6,9.1,'Masterpiece',NOW()),
(8,7,8.5,'Great zombie movie',NOW()),
(9,8,9.3,'Outstanding acting',NOW()),
(10,9,8.4,'Charming film',NOW()),
(11,10,8.8,'Inspiring',NOW()),
(12,11,9.0,'Powerful drama',NOW()),
(13,12,8.7,'Beautiful action',NOW()),
(14,13,8.9,'Epic sci-fi',NOW()),
(15,14,8.6,'Visual masterpiece',NOW()),
(16,15,9.1,'Classic romance',NOW()),
(17,16,8.8,'Thought provoking',NOW()),
(18,17,9.0,'Revolutionary',NOW()),
(19,18,8.7,'Dark character study',NOW()),
(20,19,9.4,'Excellent performance',NOW());


-- ======================
-- SAMPLE FAVORITES (20)
-- ======================

INSERT INTO favorites
(user_id,movie_id,created_at)
VALUES
(1,1,NOW()),
(2,2,NOW()),
(3,3,NOW()),
(4,4,NOW()),
(5,5,NOW()),
(6,6,NOW()),
(7,7,NOW()),
(8,8,NOW()),
(9,9,NOW()),
(10,10,NOW()),
(11,11,NOW()),
(12,12,NOW()),
(13,13,NOW()),
(14,14,NOW()),
(15,15,NOW()),
(16,16,NOW()),
(17,17,NOW()),
(18,18,NOW()),
(19,19,NOW()),
(20,20,NOW());

UPDATE movies m
JOIN (
    SELECT movie_id, AVG(rating) avg_r
    FROM reviews
    GROUP BY movie_id
) r ON m.id = r.movie_id
SET m.avg_rating = r.avg_r;
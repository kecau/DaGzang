DROP TABLE IF EXISTS recommender;

CREATE TABLE recommender (
    id INT AUTO_INCREMENT PRIMARY KEY,
    generating_id VARCHAR (500),
    item_id VARCHAR (500),
    user_id VARCHAR (500),
    rating VARCHAR (500)
)

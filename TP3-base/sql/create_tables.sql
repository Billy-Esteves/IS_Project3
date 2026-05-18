CREATE TABLE items(
    item_id SERIAL PRIMARY KEY,
    item_name VARCHAR(40) NOT NULL
);
CREATE TABLE countries(
    country_id SERIAL PRIMARY KEY,
    country_name VARCHAR(40) NOT NULL
);
INSERT INTO items (item_name) VALUES ('coffee'), ('tea'), ('bread'), ('milk');
INSERT INTO countries (country_name) VALUES ('Portugal'), ('Spain'), ('France'), ('Germany');
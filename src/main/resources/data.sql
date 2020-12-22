DROP TABLE IF EXISTS BID;
DROP TABLE IF EXISTS IMAGE;
DROP TABLE IF EXISTS AUCTION;
DROP TABLE IF EXISTS ITEM;

CREATE TABLE ITEM (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  TITLE VARCHAR(100) NOT NULL,
  DESCRIPTION TEXT NOT NULL
);

CREATE TABLE AUCTION (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  ITEM_ID INT NOT NULL,
  EXPIRES_ON TIMESTAMP NOT NULL,
  CURRENT_PRICE DECIMAL(7,2) NOT NULL,
  CONSTRAINT `AUCTION_ITEM_FK` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IMAGE (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  ITEM_ID INT NOT NULL,
  TYPE VARCHAR(10) NOT NULL,
  URL TEXT NOT NULL,
  CONSTRAINT `IMAGE_ITEM_FK` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE BID (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  AUCTION_ID INT NOT NULL,
  USERNAME VARCHAR(20) NOT NULL,
  AMOUNT DECIMAL(7,2) NOT NULL,
  DATE_TIME TIMESTAMP NOT NULL,
  CONSTRAINT `BID_AUCTION_FK` FOREIGN KEY (`AUCTION_ID`) REFERENCES `AUCTION` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO ITEM(ID, TITLE, DESCRIPTION) VALUES
(1, 'HP Notebook', 'Un computer quasi nuovo, comprato d''occasione nel Klondyke'),
(2, 'Schermo', 'Uno schermo, non c''è molto altro da dire'),
(3, 'Chiavetta USB', 'Una capiente chiavetta USB, dada gift anno 2015');

INSERT INTO AUCTION(ID, ITEM_ID, EXPIRES_ON, CURRENT_PRICE) VALUES
(1, 1, '2021-03-01 14:00:00', 1000.0),
(2, 2, '2021-02-27 18:00:00', 200.0),
(3, 3, '2021-02-19 11:30:00', 8.75);

INSERT INTO IMAGE(ITEM_ID, TYPE, URL) VALUES
(1, 'THUMBNAIL', 'https://my.image.server/image/1_1_thumb.png'),
(1, 'THUMBNAIL', 'https://my.image.server/image/1_2_thumb.png'),
(1, 'THUMBNAIL', 'https://my.image.server/image/1_3_thumb.png'),
(1, 'FULLSIZE', 'https://my.image.server/image/1_1.png'),
(1, 'FULLSIZE', 'https://my.image.server/image/1_2.png'),
(1, 'FULLSIZE', 'https://my.image.server/image/1_3.png'),
(2, 'THUMBNAIL', 'https://my.image.server/image/2_1_thumb.png'),
(2, 'THUMBNAIL', 'https://my.image.server/image/2_2_thumb.png'),
(2, 'FULLSIZE', 'https://my.image.server/image/2_1.png'),
(2, 'FULLSIZE', 'https://my.image.server/image/2_2.png'),
(3, 'THUMBNAIL', 'https://my.image.server/image/3_1_thumb.png'),
(3, 'FULLSIZE', 'https://my.image.server/image/3_1.png');

INSERT INTO BID(AUCTION_ID, USERNAME, AMOUNT, DATE_TIME) VALUES
(1, 'tpessa', 1010.5, '2020-01-23 19:23:11'),
(1, 'tpessa', 1020.5, '2020-02-01 10:10:23');
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
  INITIAL_PRICE DECIMAL(7,2) NOT NULL,
  CONSTRAINT `AUCTION_ITEM_FK` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IMAGE (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  ITEM_ID INT NOT NULL,
  FORMAT VARCHAR(10) NOT NULL,
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

INSERT INTO AUCTION(ID, ITEM_ID, EXPIRES_ON, CURRENT_PRICE, INITIAL_PRICE) VALUES
(1, 1, '2021-03-01 14:00:00', 1020.5, 1000),
(2, 2, '2021-02-27 18:00:00', 200.0, 200.0),
(3, 3, '2021-02-19 11:30:00', 8.75, 8.75);


INSERT INTO BID(AUCTION_ID, USERNAME, AMOUNT, DATE_TIME) VALUES
(1, 'tpessa', 1010.5, '2020-01-23 19:23:11'),
(1, 'tpessa', 1020.5, '2020-02-01 10:10:23');


INSERT INTO IMAGE(ITEM_ID, FORMAT, URL) VALUES
(1, 'THUMBNAIL', 'https://images.unsplash.com/photo-1588702547923-7093a6c3ba33?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(1, 'FULLSIZE', 'https://images.unsplash.com/photo-1588702547923-7093a6c3ba33?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(1, 'THUMBNAIL', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(1, 'FULLSIZE', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(1, 'THUMBNAIL', 'https://images.unsplash.com/photo-1587614382346-4ec70e388b28?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(1, 'FULLSIZE', 'https://images.unsplash.com/photo-1587614382346-4ec70e388b28?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(1, 'THUMBNAIL', 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(1, 'FULLSIZE', 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHxsYXB0b3B8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(2, 'THUMBNAIL', 'https://images.unsplash.com/photo-1587251029040-f5a10e9d281b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=200'),
(2, 'FULLSIZE', 'https://images.unsplash.com/photo-1587251029040-f5a10e9d281b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=1080'),
(2, 'THUMBNAIL', 'https://images.unsplash.com/photo-1555940280-66bf87aa823d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=200'),
(2, 'FULLSIZE', 'https://images.unsplash.com/photo-1555940280-66bf87aa823d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=1080'),
(2, 'THUMBNAIL', 'https://images.unsplash.com/photo-1561886362-a2b38ce83470?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=200'),
(2, 'FULLSIZE', 'https://images.unsplash.com/photo-1561886362-a2b38ce83470?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=1080'),
(2, 'THUMBNAIL', 'https://images.unsplash.com/photo-1555963153-11ff60182d08?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=200'),
(2, 'FULLSIZE', 'https://images.unsplash.com/photo-1555963153-11ff60182d08?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHxtb25pdG9yLXBjfGVufDB8fHw&ixlib=rb-1.2.1&q=80&w=1080'),
(3, 'THUMBNAIL', 'https://images.unsplash.com/photo-1477949331575-2763034b5fb5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(3, 'FULLSIZE', 'https://images.unsplash.com/photo-1477949331575-2763034b5fb5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwxfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(3, 'THUMBNAIL', 'https://images.unsplash.com/photo-1551818014-7c8ace9c1b5c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(3, 'FULLSIZE', 'https://images.unsplash.com/photo-1551818014-7c8ace9c1b5c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwyfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(3, 'THUMBNAIL', 'https://images.unsplash.com/photo-1587145820098-23e484e69816?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(3, 'FULLSIZE', 'https://images.unsplash.com/photo-1587145820098-23e484e69816?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHwzfHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080'),
(3, 'THUMBNAIL', 'https://images.unsplash.com/photo-1521908613973-ff2eb881fcee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=200'),
(3, 'FULLSIZE', 'https://images.unsplash.com/photo-1521908613973-ff2eb881fcee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwxMTc0OTB8MHwxfHNlYXJjaHw0fHx1c2J8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=1080')
;

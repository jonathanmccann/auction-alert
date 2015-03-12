DROP TABLE IF EXISTS SearchQuery, SearchResult;

CREATE TABLE IF NOT EXISTS SearchQuery(
	searchQueryId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	searchQuery VARCHAR(100) null
);

CREATE TABLE IF NOT EXISTS SearchResult(
	searchResultId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	searchQueryId INT NOT NULL,
	itemId VARCHAR(25) NOT NULL,
	itemTitle VARCHAR(100),
	typeOfAuction VARCHAR(25),
	itemURL VARCHAR(50),
	galleryURL VARCHAR(100),
	endingTime LONG,
	auctionPrice DOUBLE,
	fixedPrice DOUBLE
);

CREATE TABLE IF NOT EXISTS SearchQueryPreviousResult(
	searchQueryId INT NOT NULL,
	searchResultItemId VARCHAR(25) NOT NULL PRIMARY KEY
);
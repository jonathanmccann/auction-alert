CREATE TABLE IF NOT EXISTS SearchQuery(
	searchQueryId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	searchQuery VARCHAR(100) null
);

CREATE TABLE IF NOT EXISTS SearchQueryResult(
	searchQueryResultId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	itemId INT NOT NULL,
	itemTitle VARCHAR(100),
	itemTypeOfAuction VARCHAR(25),
	itemURL VARCHAR(50),
	itemEndingTime DATE,
	itemAuctionPrice DOUBLE,
	itemFixedPrice DOUBLE
);
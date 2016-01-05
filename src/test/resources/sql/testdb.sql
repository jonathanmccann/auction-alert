DROP TABLE IF EXISTS SearchQuery, SearchResult, Category;

CREATE TABLE IF NOT EXISTS SearchQuery(
	searchQueryId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	keywords VARCHAR(100) null,
	categoryId VARCHAR(15) null,
	searchDescription BOOLEAN,
	freeShippingOnly BOOLEAN,
	newCondition BOOLEAN,
	usedCondition BOOLEAN,
	unspecifiedCondition BOOLEAN,
	auctionListing BOOLEAN,
	fixedPriceListing BOOLEAN,
	minPrice DOUBLE,
	maxPrice DOUBLE
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
	searchQueryPreviousResultId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	searchQueryId INT NOT NULL,
	searchResultItemId VARCHAR(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS Category(
	uuid INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	categoryId VARCHAR(10) NOT NULL,
	categoryName VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS User_(
	userId BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	emailAddress VARCHAR(100),
	phoneNumber VARCHAR(12),
	password VARCHAR(128),
	salt VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS Release_(
	uuid INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	releaseName VARCHAR(50) NOT NULL UNIQUE,
	version VARCHAR(10) NOT NULL
);
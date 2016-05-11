DROP TABLE IF EXISTS SearchQuery, SearchResult, SearchQueryPreviousResult, Category, User_, NotificationPreferences, Release_;

CREATE TABLE IF NOT EXISTS SearchQuery(
	searchQueryId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	userId INT NOT NULL,
	keywords VARCHAR(300) null,
	categoryId VARCHAR(15) null,
	searchDescription BOOLEAN,
	freeShippingOnly BOOLEAN,
	newCondition BOOLEAN,
	usedCondition BOOLEAN,
	unspecifiedCondition BOOLEAN,
	auctionListing BOOLEAN,
	fixedPriceListing BOOLEAN,
	minPrice DOUBLE,
	maxPrice DOUBLE,
	active BOOLEAN NOT NULL DEFAULT 1
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
	emailAddress VARCHAR(100) NOT NULL UNIQUE,
	phoneNumber VARCHAR(12),
	mobileOperatingSystem VARCHAR(30),
	mobileCarrierSuffix VARCHAR(50),
	password VARCHAR(128),
	salt VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS NotificationPreferences(
	userId INT NOT NULL PRIMARY KEY,
	emailNotification BOOLEAN,
	textNotification BOOLEAN,
	basedOnTime BOOLEAN,
	startOfDay INT,
	endOfDay INT,
	timeZone VARCHAR(30),
	weekdayDayEmailNotification BOOLEAN,
	weekdayDayTextNotification BOOLEAN,
	weekdayNightEmailNotification BOOLEAN,
	weekdayNightTextNotification BOOLEAN,
	weekendDayEmailNotification BOOLEAN,
	weekendDayTextNotification BOOLEAN,
	weekendNightEmailNotification BOOLEAN,
	weekendNightTextNotification BOOLEAN
);

CREATE TABLE IF NOT EXISTS Release_(
	uuid INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	releaseName VARCHAR(50) NOT NULL UNIQUE,
	version VARCHAR(10) NOT NULL
);

CREATE UNIQUE INDEX CATEGORY_ID ON Category(categoryId);
CREATE INDEX USER_ID ON SearchQuery(userId);
CREATE INDEX SEARCH_QUERY_ID ON SearchResult(searchQueryId);
CREATE UNIQUE INDEX EMAIL_ADDRESS ON User_(emailAddress);
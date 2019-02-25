DROP TABLE IF EXISTS SearchQuery, SearchResult, Category, User_, Release_;

CREATE TABLE IF NOT EXISTS SearchQuery(
	searchQueryId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	userId INT NOT NULL,
	keywords VARCHAR(300) null,
	categoryId VARCHAR(15) null,
	subcategoryId VARCHAR(15) null,
	searchDescription BOOLEAN,
	freeShippingOnly BOOLEAN,
	newCondition BOOLEAN,
	usedCondition BOOLEAN,
	unspecifiedCondition BOOLEAN,
	auctionListing BOOLEAN,
	fixedPriceListing BOOLEAN,
	minPrice DOUBLE,
	maxPrice DOUBLE,
	globalId VARCHAR(15),
	active BOOLEAN NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS SearchResult(
	searchResultId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	searchQueryId INT NOT NULL,
	itemId VARCHAR(25) NOT NULL,
	itemTitle VARCHAR(100),
	itemURL VARCHAR(250),
	galleryURL VARCHAR(100),
	auctionPrice VARCHAR(30),
	fixedPrice VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS Category(
	uuid INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	categoryId VARCHAR(10) NOT NULL,
	categoryName VARCHAR(50) NOT NULL,
	categoryParentId VARCHAR(50) NOT NULL,
	categoryLevel INT NOT NULL
);

CREATE TABLE IF NOT EXISTS User_(
	userId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	emailAddress VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(128),
	salt VARCHAR(128),
	preferredDomain VARCHAR(250) NOT NULL,
	emailNotification BOOLEAN DEFAULT TRUE,
	emailsSent INT DEFAULT 0,
	customerId VARCHAR(100),
	subscriptionId VARCHAR(100),
	active BOOLEAN DEFAULT FALSE,
	pendingCancellation BOOLEAN DEFAULT FALSE,
	lastLoginDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastLoginIpAddress VARCHAR(20),
	passwordResetToken VARCHAR(100),
	passwordResetExpiration TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
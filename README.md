eBay Webapp
========

This Java webapp will hook into eBay's API in order to serve custom search results via email or text message to recipients on a set interval.

Configuration
========

A "config.properties" file is required in the Catalina base directory. The following properties are expected:

* application.id -- You eBay Developer application ID
* jdbc.default.url -- The URL to your database
* jdbc.default.username -- The username for your database
* jdbc.default.password -- The password for your database
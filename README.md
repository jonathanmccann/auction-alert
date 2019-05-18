Auction Alert
========

This Java webapp will hook into eBay's API in order to serve custom search results via email to recipients on a set interval.

In Action
========

See this web application in action at http://auctionalert.io/

Configuration
========

A "config.properties" file is required in the Catalina base directory. Please see [config.properties](https://github.com/jonathanmccann/auction-alert/blob/master/src/main/resources/config.properties) for a full listing of properties.

Running Tests
========

In order to run the tests, the application ID, eBay token, and JDBC connection configuration properties need to be specified. The format to run all of the tests is the following, inserting valid information for each property:

```
gradle test jacocoTestReport -Dapplication.id="${application.id}" -Debay.token="${ebay.token}" -Djdbc.default.password="${jdbc.default.password}" -Djdbc.default.url="${jdbc.default.url}" -Djdbc.default.username="${jdbc.default.username}"
```

To test a single class, append this to the end of your test command:

```
-Dtest.single=${nameOfTestClass}
```

To debug the test, append the following to the end of your test command:

```
-Dtest.debug
```

License
========

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program.  If not, see <http://www.gnu.org/licenses/>.
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
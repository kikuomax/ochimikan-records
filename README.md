*OchiMikan Records* is a record server for [OchiMikan](https://github.com/kikuomax/ochimikan), which records high scores.
It provides a RESTful API to access and manipulate records.

Prerequisites
=============

You need the following software installed,
 - [Java](http://www.oracle.com/technetwork/java/javase/overview/index.html)
 - [sbt](http://www.scala-sbt.org)
 - [MongoDB](http://www.mongodb.org)

It is mainly developed with the following versions,
 - Java 7
 - sbt 0.13.7
 - MongoDB 2.6.5

Running a service
=================

*OchiMikan Records* is using [sbt-revolver](https://github.com/spray/sbt-revolver) to run a service.
And it is currently backed by MongoDB.

 1. Start a MongoDB service.

		mongod

 2. Start an `sbt` console.

		sbt

 3. Run `re-start` on the `sbt` console.

		sbt> re-start

You will find a service waiting for requests on the port 9090 of your localhost.
The service is hosted by [spray.io](http://spray.io).

API
===

You can produce a scaladoc documentation by running a `doc` command on `sbt`.

	sbt doc

The following description is the same with that in the documentation on `com.herokuapp.ochimikan.records.service.Service`.

GET /record?from=from&to=to
---------------------------

Returns scores in a specified range `[from, to)`.

### Parameters

 - `from` (Int >= 0, optional): The inclusive index from which scores are to be obtained. If omitted, it is equivalent to `0`.
 - `to` (Int >= 0, optional): The exclusive index until which scores are to be obtained. If omitted, it is equivalent to the total number of registered scores.

It is illegal if `from` > `to`.

### Response

A JSON object which represents a list of scores in the range `[from, to)`. It will be similar to the following,

	{
	  "scores": [
	    {
	      "value":  300,
	      "level":  2,
	      "player": "player",
	      "date":   1415549555
	    },
	    ...
	  ]
	}

`scores` is an array of `score objects` and sorted in descending order.

Each `score object` has the following fields,
 - `value` (number >= 0): The value of this score.
 - `level` (number >= 0): The level at which this score was achieved.
 - `player` (string): The name of the player who achieved this score.
 - `date` (number): The date when this score was achieved. The number of seconds since January 1, 1970, 00:00:00 GMT.

POST /record?from=from&to=to
----------------------------

Registers a specified score in the database and returns updated scores in a specified range `[from, to)`.
A client needs authentication before posting a score.

### Parameters

The same parameters as the `GET` variant.

### Header

A client needs to specify an `Authorization` header with an authorized token. It will be similar to the following,

	Authorization: Bearer <token string>

### Entity

A JSON object which represents a score to be registered. It is similar to the following,

	{
	  "value":  300,
	  "level":  2,
	  "player": "player",
	  "date":   1415549555
	}

It is the same object with a sigle `score object` returned by the `GET` variant.

### Response

The same as the `GET` variant.

GET /authenticate
-----------------

Performs a Basic authentication and returns an authorized token if succeeded.

### Response

A JSON object which represents an authorized token. It will be similar to the following,

	{
	  "token": "1234ABC..."
	}

 - `token` (string): A token string which can be specified when posting a score.

JSON Web Token
==============

Authorization is done through JSON Web Token (JWT). Please refer to a [draft](https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32) for more details.

*OchiMikan Records* is using [Nimbus JOSE + JWT](http://connect2id.com/products/nimbus-jose-jwt) for signing and verification of JWT.

Configuring a service
=====================

You can configure a service through [application.conf](application.conf). It is a [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) formatted configuration. Please refer to its comments for how to set it.

You should change the `secret-key` for your own project.

Running unittests
=================

Very basic tests are described using [specs2](http://etorreborre.github.io/specs2/) and you can run them through a `test` command on `sbt`.

	sbt test

License
=======

[MIT License](http://opensource.org/licenses/MIT).

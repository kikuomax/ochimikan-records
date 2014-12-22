package com.herokuapp.ochimikan.records.service

import com.typesafe.config.{
  ConfigException,
  ConfigFactory
}
import org.specs2.Specification

/** Specification for [[Settings]]. */
class SettingsSpec extends Specification { def is = s2"""

  The following Settings, ${valid1.config}
  Should  ${beExpected(valid1)}

  The following Settings, ${valid2.config}
  Should  ${beExpected(valid2)}

  The following Settings, ${valid3.config}
  Should  ${beExpected(valid3)}

  The following Settings, ${valid4.config}
  Should  ${beExpected(valid4)}

  The following Settings missing the root, ${missingRoot}
  Should throw ConfigException.Missing  ${expectMissing(missingRoot)}

  The following Settings missing the host.name, ${missingHostName}
  Should throw ConfigException.Missing  ${expectMissing(missingHostName)}

  The following Settings missing the host.port, ${missingHostPort}
  Should throw ConfigException.Missing  ${expectMissing(missingHostPort)}

  The following Settings missing the secretKey, ${missingSecretKey}
  Should throw ConfigException.Missing  ${expectMissing(missingSecretKey)}

  The following Settings missing the mongo-uri, ${missingMongoUri}
  Should throw ConfigException.Missing  ${expectMissing(missingMongoUri)}

  The following Settings missing the database name, ${missingDBName}
  Should throw ConfigException.Missing  ${expectMissing(missingDBName)}

  The following Settings with a wrong host.name, ${wrongHostName}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongHostName)}

  The following Settings with a wrong host.port, ${wrongHostPort}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongHostPort)}

  The following Settings with a wrong secret-key, ${wrongSecretKey}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongSecretKey)}

  The following Settings with a wrong allowed-origin, ${wrongAllowedOrigin}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongAllowedOrigin)}

  The following Settings with a wrong mongo-uri, ${wrongMongoUri}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongMongoUri)}

  The following Settings with a wrong db-name, ${wrongDBName}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongDBName)}

  The following Settings with an empty host.name, ${emptyHostName}
  Should throw ConfigException.BadValue  ${expectBadValue(emptyHostName)}

  The following Settings with host.port = 0, ${hostPort0}
  Should throw ConfigException.BadValue  ${expectBadValue(hostPort0)}

  The following Settings with host.port = 65536, ${hostPort65536}
  Should throw ConfigException.BadValue  ${expectBadValue(hostPort65536)}

  The following Settings with an empty secret-key, ${emptySecretKey}
  Should throw ConfigException.BadValue  ${expectBadValue(emptySecretKey)}

  The following Settings with a bad mongo-uri, ${badMongoUri}
  Should throw ConfigException.BadValue  ${expectBadValue(badMongoUri)}
"""

  // Tests specified settings have expected values.
  def beExpected(settings: ValidSettings) = s2"""
have hostName      = ${settings.hostName}          ${settings.expectHostName}
have hostPort      = ${settings.hostPort.toString} ${settings.expectHostPort}
have secretKey     = ${settings.secretKey}         ${settings.expectSecretKey}
have allowedOrigin = ${settings.allowedOrigin}     ${settings.expectAllowedOrigin}
have mongoUri      = ${settings.mongoUri}          ${settings.expectMongoUri}
have dbName        = ${settings.dbName}            ${settings.expectDBName}
"""

  // Expects a ConfigException.Missing thrown
  def expectMissing(config: String) =
    (new Settings(ConfigFactory.parseString(config))) must throwA[ConfigException.Missing]

  // Expects a ConfigException.WrongType thrown
  def expectWrongType(config: String) =
    (new Settings(ConfigFactory.parseString(config))) must throwA[ConfigException.WrongType]

  // Expects a ConfigException.BadValue thrown
  def expectBadValue(config: String) =
    (new Settings(ConfigFactory.parseString(config))) must throwA[ConfigException.BadValue]

  // Fixtures

  val valid1 = ValidSettings("""
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }""",
    hostName      = "127.0.0.1",
    hostPort      = 9090,
    secretKey     = "xyz123",
    allowedOrigin = "*",
    mongoUri      = "mongodb://127.0.0.1:27017/",
    dbName        = "mikan"
  )

  val valid2 = ValidSettings("""
    com.herokuapp.ochimikan.records.service {
      host.name      = records.ochimikan.com
      host.port      = 4649
      secret-key     = MikanSecret
      allowed-origin = "http://ochimikan.herokuapp.com/"
      mongo-uri      = "mongodb://user:password@mongo.ochimikan.com:11029/testdb"
    }""",
    hostName      = "records.ochimikan.com",
    hostPort      = 4649,
    secretKey     = "MikanSecret",
    allowedOrigin = "http://ochimikan.herokuapp.com/",
    mongoUri      = "mongodb://user:password@mongo.ochimikan.com:11029/testdb",
    dbName        = "testdb"
  )

  val valid3 = ValidSettings("""
    com.herokuapp.ochimikan.records.service {
      host.name     = 127.0.0.1
      host.port     = 9090
      secret-key    = xyz123
      mongo-uri     = "mongodb://127.0.0.1:27017/testdb"
      db-name       = "mikan"
    }""",
    hostName      = "127.0.0.1",
    hostPort      = 9090,
    secretKey     = "xyz123",
    allowedOrigin = "*",
    mongoUri      = "mongodb://127.0.0.1:27017/testdb",
    dbName        = "testdb"
  )

  val valid4 = ValidSettings("""
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = ""
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }""",
    hostName      = "127.0.0.1",
    hostPort      = 9090,
    secretKey     = "xyz123",
    allowedOrigin = "*",
    mongoUri      = "mongodb://127.0.0.1:27017/",
    dbName        = "mikan"
  )

  val missingRoot = """
    unknown.settings {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""
 
  val missingHostName = """
    com.herokuapp.ochimikan.records.service {
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""
 
  val missingHostPort = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""
 
  val missingSecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""
 
  val missingMongoUri = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      db-name        = "mikan"
    }"""

  val missingDBName = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
    }"""

  val wrongHostName = """
    com.herokuapp.ochimikan.records.service {
      host.name      = [ localhost ]
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val wrongHostPort = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = PORT
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val wrongSecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = { value = secret }
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val wrongAllowedOrigin = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = [ "http://ochimikan.herokuapp.com/" ]
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val wrongMongoUri = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = { uri = "mongodb://127.0.0.1:27017" }
      db-name        = "mikan"
    }"""

  val wrongDBName = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = [ "mikan" ]
    }"""

  val emptyHostName = """
    com.herokuapp.ochimikan.records.service {
      host.name      = ""
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val hostPort0 = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 0
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val hostPort65536 = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 65536
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val emptySecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = ""
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/"
      db-name        = "mikan"
    }"""

  val badMongoUri = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "127.0.0.1:27017"
      db-name        = "mikan"
    }"""

  val doubleDBNames = """
    com.herokuapp.ochimikan.records.service {
      host.name      = 127.0.0.1
      host.port      = 9090
      secret-key     = xyz123
      allowed-origin = "*"
      mongo-uri      = "mongodb://127.0.0.1:27017/testdb"
      db-name        = "mikan"
    }"""

  /** A valid settings. */
  case class ValidSettings(
    config: String,
    hostName: String,
    hostPort: Int,
    secretKey: String,
    allowedOrigin: String,
    mongoUri: String,
    dbName: String)
  {
    val settings = new Settings(ConfigFactory.parseString(config))
    // expectations
    def expectHostName      = settings.hostName must_== hostName
    def expectHostPort      = settings.hostPort must_== hostPort
    def expectSecretKey     = settings.secretKey must_== secretKey
    def expectAllowedOrigin = settings.allowedOrigin must_== allowedOrigin
    def expectMongoUri      = settings.mongoUri.getURI() must_== mongoUri
    def expectDBName        = settings.dbName must_== dbName
  }
}

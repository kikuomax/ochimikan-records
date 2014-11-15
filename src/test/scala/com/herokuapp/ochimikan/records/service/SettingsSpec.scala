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

  The following Settings missing the root, ${missingRoot}
  Should throw ConfigException.Missing  ${expectMissing(missingRoot)}

  The following Settings missing the host.name, ${missingHostName}
  Should throw ConfigException.Missing  ${expectMissing(missingHostName)}

  The following Settings missing the host.port, ${missingHostPort}
  Should throw ConfigException.Missing  ${expectMissing(missingHostPort)}

  The following Settings missing the secretKey, ${missingSecretKey}
  Should throw ConfigException.Missing  ${expectMissing(missingSecretKey)}

  The following Settings missing the mongo.name, ${missingMongoName}
  Should throw ConfigException.Missing  ${expectMissing(missingMongoName)}

  The following Settings missing the mongo.port, ${missingMongoPort}
  Should throw ConfigException.Missing  ${expectMissing(missingMongoPort)}

  The following Settings with a wrong host.name, ${wrongHostName}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongHostName)}

  The following Settings with a wrong host.port, ${wrongHostPort}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongHostPort)}

  The following Settings with a wrong secretKey, ${wrongSecretKey}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongSecretKey)}

  The following Settings with a wrong mongo.name, ${wrongMongoName}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongMongoName)}

  The following Settings with a wrong mongo.port, ${wrongMongoPort}
  Should throw ConfigException.WrongType  ${expectWrongType(wrongMongoPort)}

  The following Settings with an empty host.name, ${emptyHostName}
  Should throw ConfigException.BadValue  ${expectBadValue(emptyHostName)}

  The following Settings with host.port = 0, ${hostPort0}
  Should throw ConfigException.BadValue  ${expectBadValue(hostPort0)}

  The following Settings with host.port = 65536, ${hostPort65536}
  Should throw ConfigException.BadValue  ${expectBadValue(hostPort65536)}

  The following Settings with an empty secretKey, ${emptySecretKey}
  Should throw ConfigException.BadValue  ${expectBadValue(emptySecretKey)}

  The following Settings with an empty mongo.name, ${emptyMongoName}
  Should throw ConfigException.BadValue  ${expectBadValue(emptyMongoName)}

  The following Settings with mongo.port = 0, ${mongoPort0}
  Should throw ConfigException.BadValue  ${expectBadValue(mongoPort0)}

  The following Settings with mongo.port = 65536, ${mongoPort65536}
  Should throw ConfigException.BadValue  ${expectBadValue(mongoPort65536)}
"""

  // Tests specified settings have expected values.
  def beExpected(settings: ValidSettings) = s2"""
have hostName  = ${settings.hostName}            ${settings.expectHostName}
have hostPort  = ${settings.hostPort.toString}   ${settings.expectHostPort}
have secretKey = ${settings.secretKey}           ${settings.expectSecretKey}
have mongoName = ${settings.mongoName}           ${settings.expectMongoName}
have mongoPort = ${settings.mongoPort.toString}  ${settings.expectMongoPort}
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
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }""",
    hostName  = "127.0.0.1",
    hostPort  = 9090,
    secretKey = "xyz123",
    mongoName = "127.0.0.1",
    mongoPort = 27017
  )

  val valid2 = ValidSettings("""
    com.herokuapp.ochimikan.records.service {
      host.name  = records.ochimikan.com
      host.port  = 4649
      secretKey  = MikanSecret
      mongo.name = mongo.ochimikan.com
      mongo.port = 11029
    }""",
    hostName  = "records.ochimikan.com",
    hostPort  = 4649,
    secretKey = "MikanSecret",
    mongoName = "mongo.ochimikan.com",
    mongoPort = 11029
  )

  val missingRoot = """
    unknown.settings {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""
 
  val missingHostName = """
    com.herokuapp.ochimikan.records.service {
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""
 
  val missingHostPort = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""
 
  val missingSecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""
 
  val missingMongoName = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.port = 27017
    }"""

  val missingMongoPort = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
    }"""

  val wrongHostName = """
    com.herokuapp.ochimikan.records.service {
      host.name  = [ localhost ]
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val wrongHostPort = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = PORT
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val wrongSecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = { value = secret }
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val wrongMongoName = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = [ 127.0.0.1 ]
      mongo.port = 27017
    }"""

  val wrongMongoPort = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = [ 27017 ]
    }"""

  val emptyHostName = """
    com.herokuapp.ochimikan.records.service {
      host.name  = ""
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val hostPort0 = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 0
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val hostPort65536 = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 65536
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val emptySecretKey = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = ""
      mongo.name = 127.0.0.1
      mongo.port = 27017
    }"""

  val emptyMongoName = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = ""
      mongo.port = 27017
    }"""

  val mongoPort0 = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 0
    }"""

  val mongoPort65536 = """
    com.herokuapp.ochimikan.records.service {
      host.name  = 127.0.0.1
      host.port  = 9090
      secretKey  = xyz123
      mongo.name = 127.0.0.1
      mongo.port = 65536
    }"""

  /** A valid settings. */
  case class ValidSettings(
    config: String,
    hostName: String,
    hostPort: Int,
    secretKey: String,
    mongoName: String,
    mongoPort: Int)
  {
    val settings = new Settings(ConfigFactory.parseString(config))
    // expectations
    def expectHostName  = settings.hostName  must_== hostName
    def expectHostPort  = settings.hostPort  must_== hostPort
    def expectSecretKey = settings.secretKey must_== secretKey
    def expectMongoName = settings.mongoName must_== mongoName
    def expectMongoPort = settings.mongoPort must_== mongoPort
  }
}

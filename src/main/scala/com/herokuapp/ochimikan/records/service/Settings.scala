package com.herokuapp.ochimikan.records.service

import com.mongodb.MongoClientURI
import com.typesafe.config.{
  Config,
  ConfigException
}

/**
 * The settings for the OchiMikan Records.
 *
 * The following settings must be specified under the path
 * `com.herokuapp.ochimikan.records.service`,
 *  - `host.name`
 *  - `host.port`
 *  - `secret-key`
 *  - `allowed-origin` (optional)
 *  - `mongo-uri`
 *  - `db-name` (optional)
 *
 * If `allowed-origin` is omitted or an empty string, it will be interpreted as
 * a wildcard "*".
 *
 * If `db-name` is omitted, the database name must be included in `mongo-uri`.
 * If database names are specified in both `mongo-uri` and `db-name`, the name
 * in `mongo-uri` will be adopted.
 *
 * Please refer to `resources/application.conf` for how to describe
 * a configuration.
 *
 * @constructor
 * @param config
 *     The configuration to be loaded.
 * @throws ConfigException.Missing
 *     - If `com.herokuapp.ochimikan.records.service` does not exist,
 *     - or if one or more of the necessary keys do not exist in `config`,
 *     - or if the database name is in neither `mongo-uri` nor `db-name`.
 * @throws ConfigException.WrongType
 *     - If `host.name` is not a string,
 *     - or if `host.port` is not an integer,
 *     - or if `secret-key` cannot be a string,
 *     - or if `allowed-origin` is specified but not a string,
 *     - or if `mongo-uri` is not a string,
 *     - or if `db-name` is specified but not a string.
 * @throws ConfigException.BadValue
 *     - If `host.name` is empty,
 *     - or if `host.port` is <= 0,
 *     - or if `host.port` is >= 65536,
 *     - or if `secret-key` is empty,
 *     - or if `mongo-uri` is not an acceptable MongoDB URI.
 */
class Settings(config: Config) {
  private val subconfig =
    config.getConfig("com.herokuapp.ochimikan.records.service")

  /**
   * The host name or IP of the service.
   * Associated with the key `host.name`. Non-empty.
   */
  val hostName: String = getNonEmpty("host.name")

  /**
   * The port on which the service is waiting for connections.
   * Associated with the key `host.port`. [1..65535].
   */
  val hostPort: Int = getPort("host.port")

  /**
   * The secret key to sign authorization tokens.
   * Associated with the key `secret-key`. Non-empty.
   */
  val secretKey: String = getNonEmpty("secret-key")

  /**
   * The origin allowed to access the service.
   * Associated with the key `allowed-origin`. "*" or URI.
   * "*" if omitted or empty.
   */
  val allowedOrigin: String = getAllowedOrigin("allowed-origin")

  /**
   * The MongoDB URI of the MongoDB server.
   * Associated with the key `mongo-uri`.
   */
  val mongoUri: MongoClientURI = getMongoUri("mongo-uri")

  /**
   * The name of the database to connect to.
   * Associated with the key `db-name`.
   */
  val dbName: String = getDBName()

  /**
   * Obtains a non-empty string value.
   *
   * @throws ConfigException.Missing
   *     If `key` does not exist.
   * @throws ConfigException.WrongType
   *     If the value associated with `key` is not a string.
   * @throws ConfigException.BadValue
   *     If the string associated with `key` is empty.
   */
  private def getNonEmpty(key: String): String = {
    val value = subconfig.getString(key)
    if (value.isEmpty)
      throw new ConfigException.BadValue(subconfig.origin(), key,
        s"$key must not be empty")
    value
  }

  /**
   * Obtains a port number.
   *
   * @throws ConfigException.Missing
   *     If `key` does not exist.
   * @throws ConfigException.WrongType
   *     If the value associated with `key` is not an integer.
   * @throws ConfigException.BadValue
   *     - If the value associated with `key` is <= 0,
   *     - or if the value associated with `key` is >= 65536.
   */
  private def getPort(key: String): Int = {
    val port = subconfig.getInt(key)
    if (port <= 0 || 65536 <= port)
      throw new ConfigException.BadValue(subconfig.origin(), key,
        s"$key must be in [1, 65535] but $port")
    port
  }

  /**
   * Obtains an allowed origin.
   *
   * "*" if no value is associated with `key` or the value associated with `key`
   * is an empty string.
   *
   * @throws ConfigException.WrongType
   *     If the value associated with `key` is not a string.
   */
  private def getAllowedOrigin(key: String): String =
    if (!subconfig.hasPath(key))
      "*"
    else {
      val allowedOrigin = subconfig.getString(key)
      if (!allowedOrigin.isEmpty) allowedOrigin else "*"
   }

  /**
   * Obtains a MongoDB URI.
   *
   * @throws ConfigException.Missing
   *     If `key` does not exist.
   * @throws ConfigException.WrongType
   *     If the value associated with `key` is not a string.
   * @throws ConfigException.BadValue
   *     If the string associated with `key` is not an acceptable MongoDB URI.
   */
  private def getMongoUri(key: String): MongoClientURI =
    try {
      new MongoClientURI(subconfig.getString(key))
    } catch {
      case e: IllegalArgumentException =>
        throw new ConfigException.BadValue(subconfig.origin(), key, e.getMessage())
    }

  /**
   * Obtains the database name.
   *
   * This method must be called after `mongoUri` is loaded.
   *
   * @throws ConfigException.Missing
   *     If the database name is in neither `mongoUri` nor the `db-name` field.
   * @throws ConfigException.WrongType
   *     If the value associated with `db-name` is not a string.
   */
  private def getDBName(): String =
    Option(mongoUri.getDatabase()).getOrElse(subconfig.getString("db-name"))
}

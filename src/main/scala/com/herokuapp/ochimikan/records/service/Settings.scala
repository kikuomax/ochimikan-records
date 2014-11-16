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
 *  - `secretKey`
 *  - `mongo-uri`
 *
 * Please refer to `resources/application.conf` for how to describe
 * a configuration.
 *
 * @constructor
 * @param config
 *     The configuration to be loaded.
 * @throws ConfigException.Missing
 *     - If `com.herokuapp.ochimikan.records.service` does not exist,
 *     - or if one or more of the keys do not exist in `config`.
 * @throws ConfigException.WrongType
 *     - If `host.name` is not a string,
 *     - or if `host.port` is not an integer,
 *     - or if `secretKey` cannot be a string,
 *     - or if `mongo-uri` is not a string.
 * @throws ConfigException.BadValue
 *     - If `host.name` is empty,
 *     - or if `host.port` is <= 0,
 *     - or if `host.port` is >= 65536,
 *     - or if `secretKey` is empty,
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
   * Associated with the key `secretKey`. Non-empty.
   */
  val secretKey: String = getNonEmpty("secretKey")

  /**
   * The MongoDB URI of the MongoDB server.
   * Associated with the key `mongo-uri`.
   */
  val mongoUri: MongoClientURI = getMongoUri("mongo-uri")

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
   * Obtains a MongoDB URI.
   *
   * @throws ConfigException.Missing
   *     If `key` does not exist.
   * @throws ConfigException.WrongType
   *     If the value associated with `key` is not a string.
   * @throws ConfigException.BadValue
   *     If the string associated with `key` is not an acceptable MongoDB URI.
   */
  private def getMongoUri(key: String): MongoClientURI = {
    try {
      new MongoClientURI(subconfig.getString(key))
    } catch {
      case e: IllegalArgumentException =>
        throw new ConfigException.BadValue(subconfig.origin(), key, e.getMessage())
    }
  }
}

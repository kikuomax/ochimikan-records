package com.herokuapp.ochimikan.records.service

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
 *  - `mongo.name`
 *  - `mongo.port`
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
 *     - or if `mongo.name` is not a string,
 *     - or if `mongo.port` is not an integer.
 * @throws ConfigException.BadValue
 *     - If `host.name` is empty,
 *     - or if `host.port` is <= 0,
 *     - or if `host.port` is >= 65536,
 *     - or if `secretKey` is empty,
 *     - or if `mongo.name` is empty,
 *     - or if `mongo.port` is <= 0,
 *     - or if `mongo.port` is >= 65536.
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
   * The host name or IP of the MongoDB.
   * Associated with the key `mongo.name`. Non-empty.
   */
  val mongoName: String = getNonEmpty("mongo.name")

  /**
   * The port on which the MongoDB is waiting for connections.
   * Associated with the key `mongo.port`. [1..65535].
   */
  val mongoPort: Int = getPort("mongo.port")

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
}

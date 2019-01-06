package com.example

import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.stringType

val config = EnvironmentVariables()

object Config {
    val DB_HOST = config[Key("db.host", stringType)]
    val DB_PORT = config[Key("db.port", intType)]
    val DB_NAME = config[Key("db.name", stringType)]
    val DB_USER = config[Key("db.user", stringType)]
    val DB_PASSWORD = config[Key("db.password", stringType)]
}

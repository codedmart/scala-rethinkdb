package com.scalaRethinkdb

sealed abstract class RethinkError(message: String) extends Exception(message)
case class RethinkDbError(message: String) extends RethinkError(message)
case class RethinkDbConnectionError(message: String) extends RethinkError(message)
case class RethinkDbUnexpectedResponseError(message: String) extends RethinkError(message)


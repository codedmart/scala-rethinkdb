package com.scalaRethinkdb

import annotation.tailrec
import scala.util.{Try, Success}
import java.io.{InputStream, OutputStream, IOException}
import java.net.{InetSocketAddress, Socket}
import java.util.concurrent.atomic.AtomicInteger
import argonaut._
import Argonaut._

import com.scalaRethinkdb.utils.{pack, unpack, getOption}

trait Rql {
  // TODO: this is just for quick test of examples. Need to rewrite to be able to chain like the supported drivers
  def get(conn: Connection, db:String, table: String): String = {
    // Table example
    val query = Json.array(
      jNumber(1), Json.array(
        jNumber(15), Json.array(
          jString(table)
        )
      ),
      Json.obj(
        "db" -> Json.array(
          jNumber(14), Json.array(
            jString(db)
          )
        )
      )
    )

    conn.run(query)
  }

    // Order by asc example -- desc would be the same just with 74 rather then 73
    //val query = Json.array(
      //jNumber(1), Json.array(
        //jNumber(41), Json.array(
          //Json.array(
            //jNumber(15), Json.array(
              //jString("designs")
            //)
          //),
          //Json.array(
            //jNumber(73), Json.array(
              //jString("createdAt")
            //)
          //)
        //)
      //),
      //Json.obj(
        //"db" -> Json.array(
          //jNumber(14), Json.array(
            //jString("lumi")
          //)
        //)
      //)
    //)

    // Order by desc, limit, skip example
    //val query = Json.array(
      //jNumber(1), Json.array(
        //jNumber(71), Json.array(
          //Json.array(
            //jNumber(70), Json.array(
              //Json.array(
                //jNumber(41), Json.array(
                  //Json.array(
                    //jNumber(15), Json.array(
                      //jString("designs")
                    //)
                  //),
                  //Json.array(
                    //jNumber(74), Json.array(
                      //jString("createdAt")
                    //)
                  //)
                //)
              //),
              //jNumber(1)
            //)
          //),
          //jNumber(1)
        //)
      //),
      //Json.obj(
        //"db" -> Json.array(
          //jNumber(14), Json.array(
            //jString("lumi")
          //)
        //)
      //)
    //)

    // Get example
    //val query = Json.array(
      //jNumber(1), Json.array(
        //jNumber(16), Json.array(
          //Json.array(
            //jNumber(15), Json.array(
              //jString("designs")
            //)
          //),
          //jString("07e1aeeb43")
        //)
      //), Json.obj(
        //"db" -> Json.array(
          //jNumber(14), Json.array(
            //jString("lumi")
          //)
        //)
      //)
    //)

    // Count example
    //val query = Json.array(
      //jNumber(1), Json.array(
        //jNumber(43), Json.array(
          //Json.array(
            //jNumber(15), Json.array(
              //jString("designs")
            //)
          //)
        //)
      //), Json.obj(
        //"db" -> Json.array(
          //jNumber(14), Json.array(
            //jString("lumi")
          //)
        //)
      //)
    //)

    // Limit example
    //val query = Json.array(
      //jNumber(1), Json.array(
        //jNumber(71), Json.array(
          //Json.array(
            //jNumber(15), Json.array(
              //jString("designs")
            //)
          //),
          //jNumber(1)
        //)
      //),
      //Json.obj(
        //"db" -> Json.array(
          //jNumber(14), Json.array(
            //jString("lumi")
          //)
        //)
      //)
    //)

    //val queryLength = query.toString.length
    //val message = pack(token, 8) ++ pack(queryLength) ++ query.toString.getBytes

    //out.write(message)
    //out.flush()

    //val resToken = recvAll(8)
    //val resLength = recvAll(4)
    //val resMessage = recvAll(unpack(resLength))

    //new String(resMessage, "US-ASCII")
}

// TODO: Needs modified
object R extends Rql {
    val DEFAULT_HOST = "localhost"
    val DEFAULT_PORT_DRIVER = 28015

    def connect(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT_DRIVER, db: String = null, auth: String = null): Connection = {
        val address = new InetSocketAddress(host, port)
        val dbName = getOption[String](db)
        val authKey = getOption[String](auth)

        Connection(address, dbName, authKey)
    }
}

case class Connection(address: InetSocketAddress, dbName: Option[String], authKey: Option[String]) {
  private var socket: Socket = _
  private var in: InputStream = _
  private var out: OutputStream = _

  private val token: AtomicInteger = new AtomicInteger()
  def nextToken = token.incrementAndGet

  reconnect()

  def reconnect(): this.type = {
    if (isOpen) close()

    Try(new Socket(address.getAddress, address.getPort)) match {
      case Success(sock) => {
        socket = new Socket(address.getAddress, address.getPort)
        socket.setKeepAlive(true)
        socket.setTcpNoDelay(true)

        in  = socket.getInputStream()
        out = socket.getOutputStream()

        handshake()
      }
      case _ => {
        val message = "Could not connect to %s.".format(address)
        throw new RethinkDbConnectionError(message)
      }
    }

    def handshake() {
      val version = Wire.to(VersionDummy.V0_3)
      val protocol = Wire.to(VersionDummy.JSON)
      val auth = authKey match {
        case None => ""
        case Some(str) => str
      }
      val authLength = authKey match {
        case None => 0
        case Some(str) => str.length
      }
      val message = pack(version) ++ pack(authLength) ++ auth.getBytes ++ pack(protocol)

      out.write(message)
      out.flush()

      val response = recvNullTerminatedString()

      if ("SUCCESS" != new String(response, "US-ASCII")) {
        throw new RethinkDbConnectionError("Error establishing driver handshake")
      }
    }

    this
  }

  //def use(name: String): Connection = {
    //this.defaultDb = name
    //this
  //}

  def close(): Unit = {
    if (!isOpen) println("Connection is closed.")

    in.close()
    out.close()
    socket.close()
  }

  // TODO: This is not how this should actually be, needs rewritten, just using for testing purposes
  def run(query: argonaut.Json): String = {
    val token = nextToken
    val queryLength = query.toString.length
    val message = pack(token, 8) ++ pack(queryLength) ++ query.toString.getBytes

    out.write(message)
    out.flush()

    val resToken = recvAll(8)
    val resLength = recvAll(4)
    val resMessage = recvAll(unpack(resLength))

    new String(resMessage, "US-ASCII")
  }

  def isOpen: Boolean = {
    socket != null && !socket.isClosed && socket.isConnected
  }

  private def recvAll(length: Int): Array[Byte] = {
    Stream.continually(in.read())
      .take(length)
      .map(_.toByte)
      .toArray
  }

  private def recvNullTerminatedString(): Array[Byte] = {
    Stream.continually(in.read())
      .takeWhile(_ != 0)
      .map(_.toByte)
      .toArray
  }
}


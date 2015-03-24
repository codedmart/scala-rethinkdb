package com.scalaRethinkdb

import annotation.tailrec
import scala.util.{Try, Success}
import java.io.{InputStream, OutputStream, IOException}
import java.net.{InetSocketAddress, Socket}
import java.util.concurrent.atomic.AtomicInteger

import com.scalaRethinkdb.utils.{pack, unpack}

object R {
    val DEFAULT_HOST = "localhost"
    val DEFAULT_PORT_DRIVER = 28015

    // TODO: handle auth
    def connect(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT_DRIVER, db: String = null): Connection = {
        val address = new InetSocketAddress(host, port)

        db match {
          case null => new Connection(address)
          case _ => new Connection(address) // TODO: handle db here
        }
    }
}

class Connection(private val address: InetSocketAddress) extends AutoCloseable {
  private var socket: Socket = _
  private var in: InputStream = _
  private var out: OutputStream = _

  //private val token: AtomicInteger = new AtomicInteger()
  //def nextToken = token.incrementAndGet

  connect()

  def connect(): Connection = {
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

    this
  }

  def handshake() {
    val version = Wire.to(VersionDummy.V0_3)
    val protocol = Wire.to(VersionDummy.JSON)
    val message = pack(version) ++ pack(0) ++ "".getBytes() ++ pack(protocol)

    out.write(message)
    out.flush()

    if ("SUCCESS" != new String(read(), "US-ASCII")) {
      throw new RethinkDbConnectionError("Error establishing driver handshake")
    }
  }

  def close(): Unit = {
    if (!isOpen) println("Connection is closed.")

    in.close()
    out.close()
    socket.close()
  }

  def isOpen: Boolean = {
    socket != null && !socket.isClosed && socket.isConnected
  }

  private def read(): Array[Byte] = {
    @tailrec def readBuffer(response: Array[Byte]): Array[Byte] = {
      val character = in.read()
      character match {
        case 0 => response
        case _ => readBuffer(response :+ character.toByte)
      }
    }

    readBuffer(Array[Byte]())
  }
}


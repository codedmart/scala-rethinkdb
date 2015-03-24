package com.scalaRethinkdb

import org.scalatest.{WordSpec, Matchers}
import com.scalaRethinkdb.{R => r}

class NetworkSpec extends WordSpec with Matchers {
  "R" should {
    val conn = r.connect("localhost", 28015)
    "connect with correct host or port" in {
      conn.isOpen should be (true)
    }

    "close the connection when close() is called" in {
      conn.close()
      conn.isOpen should be (false)
    }

    "throw error with wrong host or port" in {
      an [RethinkDbConnectionError] should be thrownBy r.connect("localhost", 28014)
    }
  }
}


package com.scalaRethinkdb

import org.scalatest.{WordSpec, Matchers}

class UtilsSpec extends WordSpec with Matchers {
  "Utils" should {
    "pack Int to Array[Byte]" in {
      utils.pack(83) should be (Array(83, 0, 0, 0))
    }

    "unpack Array[Byte] to Int" in {
      utils.unpack(Array(83, 0, 0, 0)) should be (83)
    }
  }
}


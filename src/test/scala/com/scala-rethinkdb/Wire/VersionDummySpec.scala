package com.scalaRethinkdb

import org.scalatest.{WordSpec, Matchers}

class VersionDummySpec extends WordSpec with Matchers {
  "Version" should {
    "toWire properly" in {
      VersionDummy.V0_3.toWire should be (1601562686)
      VersionDummy.V0_4.toWire should be (1074539808)
    }
  }

  "JSON" should {
    "toWire properly" in {
      VersionDummy.JSON.toWire should be (2120839367)
    }
  }
}

package com.scalaRethinkdb

import org.scalatest.{WordSpec, Matchers}

class VersionDummySpec extends WordSpec with Matchers {
  "Version" should {
    "toWire properly" in {
      Wire.to(VersionDummy.V0_3) should be (1601562686)
      Wire.to(VersionDummy.V0_4) should be (1074539808)
    }

    "fromWire properly" in {
      Wire.from[VersionDummy.Version](0x5f75e83e) should be (VersionDummy.V0_3)
      Wire.from[VersionDummy.Version](0x400c2d20) should be (VersionDummy.V0_4)
    }
  }

  "Protocol" should {
    "toWire properly" in {
      Wire.to(VersionDummy.JSON) should be (2120839367)
    }

    "fromWire properly" in {
      Wire.from[VersionDummy.Protocol](0x7e6970c7) should be (VersionDummy.JSON)
    }
  }
}


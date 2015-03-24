package com.scalaRethinkdb

object VersionDummy {
  trait Version extends Wire

  case object V0_3 extends Version {
    val toWire = 0x5f75e83e
    val fromWire = "V0_3" // TODO
  }

  case object V0_4 extends Version {
    val toWire = 0x400c2d20
    val fromWire = "V0_4" // TODO
  }

  trait Protocol extends Wire

  case object JSON extends Protocol {
    val toWire = 0x7e6970c7
    val fromWire = "JSON" // TODO
  }
}


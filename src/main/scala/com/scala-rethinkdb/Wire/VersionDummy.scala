package com.scalaRethinkdb

object VersionDummy {
  // Version
  sealed abstract class Version
  val V0_3: Version = _V0_3
  val V0_4: Version = _V0_4
  object _V0_3 extends Version
  object _V0_4 extends Version

  object Version {
    implicit object instances extends Wire[Version] {
      def to(a: Version): Int = a match {
        case V0_3 => 0x5f75e83e
        case V0_4 => 0x400c2d20
        case _ => throw new RethinkDbError("Version not found.")
      }

      def from(a: Int): Version = a match {
        case 0x5f75e83e => V0_3
        case 0x400c2d20 => V0_4
        case _ => throw new RethinkDbError("Version not found.")
      }
    }
  }

  // Protocol
  sealed abstract class Protocol
  val JSON: Protocol = _JSON
  object _JSON extends Protocol

  object Protocol {
    implicit object instances extends Wire[Protocol] {
      def to(a: Protocol): Int = a match {
        case JSON => 0x7e6970c7
        case _ => throw new RethinkDbError("Protocol not found.")
      }

      def from(a: Int): Protocol = a match {
        case 0x7e6970c7 => JSON
        case _ => throw new RethinkDbError("Protocol not found.")
      }
    }
  }
}


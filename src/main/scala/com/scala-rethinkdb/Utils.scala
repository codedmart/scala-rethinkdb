package com.scalaRethinkdb

import java.nio.{ByteOrder, ByteBuffer}

object utils {
  /*
   * Packs given integer value into a little-endian byte array.
   */
  def pack(value: Int, byte: Int = 4): Array[Byte] = {
    ByteBuffer.allocate(byte)
      .order(ByteOrder.LITTLE_ENDIAN)
      .putInt(value)
      .array()
  }

  /*
   * Unpacks given little-endian byte array into an integer value.
   */
  def unpack(value: Array[Byte]): Int = {
    ByteBuffer.wrap(value)
      .order(ByteOrder.LITTLE_ENDIAN)
      .getInt()
  }

  def getOption[T](db: T): Option[T] = db match {
    case null => None
    case opt @ _ => Some(opt)
  }
}


package com.scalaRethinkdb

import java.nio.{ByteOrder, ByteBuffer}

object utils {
  /*
   * Packs given integer value into a little-endian byte array.
   */
  def pack(value: Int): Array[Byte] = {
    ByteBuffer.allocate(4)
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
}


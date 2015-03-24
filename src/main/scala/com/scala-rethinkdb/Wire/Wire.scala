package com.scalaRethinkdb

trait Wire[T] {
  def to(a: T): Int
  def from(a: Int): T
}

object Wire {
  def to[A](a: A)(implicit ev: Wire[A]) = ev.to(a)
  def from[A](a: Int)(implicit ev: Wire[A]) = ev.from(a)
}


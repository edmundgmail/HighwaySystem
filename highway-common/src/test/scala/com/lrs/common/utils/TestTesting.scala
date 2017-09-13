package com.lrs.common.utils

/**
  * Created by vagrant on 9/11/17.
  */

trait Companion[T] {
  type C
  def apply() : C
}

object Companion {
  implicit def companion[T](implicit comp : Companion[T]) = comp()
}

object TestCompanion {
  trait Foo

  object Foo {
    def bar = "wibble"

    // Per-companion boilerplate for access via implicit resolution
    implicit def companion = new Companion[Foo] {
      type C = Foo.type
      def apply() = Foo
    }
  }

  import Companion._

  val fc = companion[Foo]  // Type is Foo.type
  val s = fc.bar           // bar is accessible
}

class TestTesting extends Testing{

  it("temp"){
    val x = new Exception("abc")
    val y = new Exception("xyz", x)
    y.getMessage shouldBe "xyz"
  }

}

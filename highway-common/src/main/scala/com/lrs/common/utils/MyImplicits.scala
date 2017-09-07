package com.lrs.common.utils

/**
  * Created by eguo on 9/4/17.
  */
object MyImplicits {
  case class CompareSettings(epsilon:Double = 0.1) extends AnyVal

  // add an operator =~= to double to do a fuzzy comparions
  implicit class DoubleCompareExtensions(val value:Double) extends AnyVal {
    def =~=(that:Double)(implicit settings:CompareSettings) : Boolean = {
      // this is not a good way to do a fuzzy comparison. You should have both relative
      // and absolute precision. But for an example like this it should suffice.
      (value - that).abs < settings.epsilon
    }
  }

  implicit val compareSettings = CompareSettings(0.0001)

}

package com.lrs.common.utils

/**
  * Created by vagrant on 10/5/17.
  */
object AssertException {
  def apply(condition: Boolean): Unit = apply(condition, "Required conditions not met")
  def apply(condition: Boolean, msg: String ) : Unit = {
    if(!condition) {
      throw new Exception(msg)
    }
  }
}

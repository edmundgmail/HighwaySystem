package com.lrs.common.utils

/**
  * Created by vagrant on 10/5/17.
  */
object AssertException {
  def apply(condition: Boolean): Unit = {
    if(!condition) {
      throw new Exception("Required conditions not met")
    }
  }
}

package com.lrs.common.processor

import org.apache.spark.SparkContext

/**
  * Created by vagrant on 8/18/17.
  */
trait Processor[T, R] {

  def process(sc: SparkContext, t: T, r : R) : T
}

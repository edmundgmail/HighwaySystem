package com.lrs.streaming.processor

import org.apache.spark.SparkContext

/**
  * Created by vagrant on 8/18/17.
  */
trait Processor[T, R] {

  def process(t: T, r : R) : T
}

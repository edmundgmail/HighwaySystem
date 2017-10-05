package com.lrs.common.utils

import java.io.StringWriter
import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Created by vagrant on 10/5/17.
  */
trait JsonWritable {

  def toJson: String = {
    val stringWriter = new StringWriter()
    JsonWritable.mapper.writeValue(stringWriter, this)
    stringWriter.toString
  }

}

object JsonWritable {

  @transient lazy val mapper =
    new ObjectMapper().registerModule(DefaultScalaModule).setDateFormat(new SimpleDateFormat("yyyyMMdd HH:mm:ss"))

}

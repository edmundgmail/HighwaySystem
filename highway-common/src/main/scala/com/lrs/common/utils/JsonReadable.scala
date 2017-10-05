package com.lrs.common.utils

import java.io.StringWriter
import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.DeserializationFeature

/**
  * Created by vagrant on 10/5/17.
  */
object JsonReadable {
  @transient lazy val mapper =
    new ObjectMapper().registerModule(DefaultScalaModule)
                      .setDateFormat(new SimpleDateFormat("yyyyMMdd HH:mm:ss"))
                      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def parseJson[T](json: String, value: Class[T]) : T = {
    JsonReadable.mapper.readValue(json,value)
  }
}

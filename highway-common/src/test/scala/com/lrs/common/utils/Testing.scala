package com.lrs.common.utils

import java.io.File

import org.scalatest._
import org.scalatest.mock.MockitoSugar

import scala.collection.JavaConverters._
/**
  * Created by vagrant on 8/29/17.
  */
trait Testing extends FunSpec with Matchers with MockitoSugar with BeforeAndAfterEach with BeforeAndAfterAll {

  def removeFileExtension(path: String): String = {
    val filename = new File(path).getName
    if (filename.contains('.')) filename.split('.')(0)
    else filename
  }

}
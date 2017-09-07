package com.lrs.common.logging

import java.lang.management.ManagementFactory

import org.apache.log4j.Logger

/**
  * Created by vagrant on 8/29/17.
  */
trait Logging extends Serializable {

  System.setProperty("PID", ManagementFactory.getRuntimeMXBean.getName.split('@')(0))

  @transient lazy val logger: Logger = Logger.getLogger(getClass.getName)

  /**
    * Print exception stack into log
    */
  def logError(e: Throwable) = logger.error(e.getMessage, e)
}

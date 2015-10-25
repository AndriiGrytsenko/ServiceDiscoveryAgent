package com.example.serviceDiscoveryAgent

import com.example.serviceDiscoveryAgent.config.Config
import com.example.serviceDiscoveryAgent.healthcheck.{HttpCheck, ProcessCheck}
import com.twitter.logging.Logger
import com.twitter.util.Future

trait HealthCheck {
  def check(): Future[Boolean]

  def verifyConfig(): Boolean
}

object HealthCheck {
  private[this] val log = Logger.get(getClass.getName)

  def apply(config: Config): HealthCheck = {
    val checkerMap = config.target.name match {
      case "ProcessCheck" => new ProcessCheck(config.target)
      case "HttpCheck" => new HttpCheck(config.target)
      case x => throw new CheckerIsNotSupported("Checker %s is not supported".format("x"))
    }

    // throw exception if checker is poorly configured
    checkerMap.verifyConfig()
    checkerMap
  }

  def checkParameter(targetConfig: Map[String, String], parameterName: String, processCheck: String, optional: Boolean = false): Unit = {
    if (!targetConfig.contains(parameterName))
      if (!optional)
        throw new CheckerConfigException("Checker %s should have parameter %s".format(parameterName, processCheck))
      else
        log.warning("Optional parameter %s is not set".format(parameterName))
  }
}

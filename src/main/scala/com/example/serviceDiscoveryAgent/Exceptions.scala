package com.example.serviceDiscoveryAgent

import com.twitter.logging.Logger
import com.twitter.server.handler.ShutdownHandler

import scala.reflect.internal.FatalError

trait serviceDiscoveryAgentException {
  val msg: String
  val log = Logger.get(getClass.getName)
  log.fatal(msg)
}

class CheckerConfigException(override val msg: String) extends InterruptedException(msg) with serviceDiscoveryAgentException

class CheckerIsNotSupported(override val msg: String) extends InterruptedException(msg) with serviceDiscoveryAgentException

class CheckerIsNotSpecified(override val msg: String) extends InterruptedException(msg) with serviceDiscoveryAgentException

class HealthCheckFailed(val msg: String) extends Exception() with serviceDiscoveryAgentException

class MacrosException(override val msg: String) extends InterruptedException(msg) with serviceDiscoveryAgentException

class NotImplementedException(override val msg: String) extends InterruptedException(msg) with serviceDiscoveryAgentException

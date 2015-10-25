package com.example.serviceDiscoveryAgent.healthcheck

import java.nio.file.{Paths, Files}

import com.example.serviceDiscoveryAgent.config.Target
import com.example.serviceDiscoveryAgent.HealthCheck
import com.example.serviceDiscoveryAgent.utils.RunCommand
import com.twitter.logging.Logger
import com.twitter.util.Future

import scala.io.Source

class ProcessCheck(config: Target) extends HealthCheck {
  private[this] lazy val log = Logger.get(getClass.getName)
  private[this] lazy val os = System.getProperty("os.name")
  private[this] lazy val pidFile = config.params.get("processPidFile").get
  private[this] val name = "ProcessCheck"

  private def getPid: Option[String] = {
    val isFileExists = Files.exists(Paths.get(pidFile))
    if (isFileExists) Some(Source.fromFile(pidFile).getLines().mkString)
    else None
  }

  private def checkProcess(f: (String) => Future[Boolean]): Future[Boolean] = {
    getPid match {
      case Some(pid) =>
        log.info("Checking if process id %s is running...".format(pid))
        f(pid)
      case None =>
        log.warning("Pid file is not exists")
        Future(false)
    }
  }

  private def checkLinux(pid: String): Future[Boolean] = Future{Files.exists(Paths.get("/proc/%s".format(pid)))}

  private def checkMacOS(pid: String): Future[Boolean] = {
    RunCommand.run(List("ps", "-p", s"$pid", "-o", "pid=")) map { _ =>
      true
    } handle {
      case _ => false
    }
  }

  def check(): Future[Boolean] = {
    os match {
      case "Linux" => checkProcess(checkLinux)
      case "Mac OS X" => checkProcess(checkMacOS)
      case _ => throw new Exception("current OS(%s) is not supported".format(os))
    }
  }

  def verifyConfig(): Boolean = {
    HealthCheck.checkParameter(config.params, "processPidFile", name)
    true
  }
}

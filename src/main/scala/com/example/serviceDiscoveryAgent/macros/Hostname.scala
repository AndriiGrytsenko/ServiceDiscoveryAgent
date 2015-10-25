package com.example.serviceDiscoveryAgent.macros

import com.example.serviceDiscoveryAgent.utils.RunCommand

object Hostname {
  private[this] val os = System.getProperty("os.name")
  private def getHostname = {
    RunCommand.run(List("hostname"))
  }

  def getValue = {
    os match {
      case "Linux" => getHostname
      case "Mac OS X" => getHostname
      case _ => throw new Exception("current OS(%s) is not supported".format(os))
    }
  }
}

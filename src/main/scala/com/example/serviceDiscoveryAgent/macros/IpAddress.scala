package com.example.serviceDiscoveryAgent.macros

import com.example.serviceDiscoveryAgent.{NotImplementedException, Macro}
import com.example.serviceDiscoveryAgent.utils.RunCommand
import com.twitter.util.Future

// TODO: those spaghetti should be simplified
object IpAddress extends Macro{
  private[this] val os = System.getProperty("os.name")
  private def getIpAddressMacOs = {
    RunCommand.run(List("ipconfig", "getifaddr", "en0"))
  }

  private def getIpAddrLinux = {
    throw new NotImplementedException("Ip addr for linux not implemented yet")
//    RunCommand.run(
//      List("ip", "a"),
//      List("grep", "'inet '"),
//      List("grep", "-v", "127.0.0.1"),
//      List("awk", "'{print", "$2}'"),
//      List("awk", "-F/", "'{print", "$1}'"))
  }
  def getValue: Future[String] = {
    os match {
      case "Linux" => getIpAddrLinux
      case "Mac OS X" => getIpAddressMacOs
      case _ => throw new Exception("current OS(%s) is not supported".format(os))
    }
  }
}

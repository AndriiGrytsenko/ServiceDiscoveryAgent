package com.example.serviceDiscoveryAgent

import com.example.serviceDiscoveryAgent.macros.IpAddress
import com.example.serviceDiscoveryAgent.macros.Hostname
import com.twitter.logging.Logger
import com.twitter.util.{Await, Future}

import scala.reflect.internal.FatalError
import scala.util.Failure
import scala.util.matching.Regex

trait Macro {
  def getValue: Future[String]
}

object Macros {

  private[this] val log = Logger.get(getClass.getName)

  def getValue(macroName: String): Future[String] = {
    macroName match {
      case "hostname" => Hostname.getValue
      case "ip_addr" => IpAddress.getValue
      case _ => throw new MacrosException("macros %s is not supported".format(macroName))
    }
  }

  def parseData(data: String): String = {
    val separator = "%%.+%%".r

    def replacer(x: Regex.Match) = {
      val name = x.source.subSequence(x.start, x.end)
      // TODO: fix it to return future
      Await.result(Macros.getValue(name.toString.replace("%%", "")))
    }

    val res = separator.replaceAllIn(data, replacer(_))
    log.debug("changing data %s to %s".format(data, res))
    res
  }
}
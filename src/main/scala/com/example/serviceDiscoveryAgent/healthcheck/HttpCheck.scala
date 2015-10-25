package com.example.serviceDiscoveryAgent.healthcheck

import com.example.serviceDiscoveryAgent.HealthCheck
import com.example.serviceDiscoveryAgent.config.Target
import com.twitter.finagle.Service

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Request, Response, Http}
import com.twitter.logging.Logger
import com.twitter.util.Future
import com.twitter.util.TimeConversions._

class HttpCheck(config: Target) extends HealthCheck  {

  private[this] val name = "HttpCheck"

  private[this] val target = config.params.get("target").get
  private[this] val statusCode = config.params.getOrElse("responseCode", "200").toInt
  private[this] val url = target.split("/")(0)
  private[this] val endpoint = target.replace(url, "")
  private[this] val requestTimeout = config.params.getOrElse("requestTimeout", "1").toInt

  private[this] lazy val log = Logger.get(getClass.getName)

  private[this] lazy val client: Service[Request, Response] = ClientBuilder()
    .codec(Http())
    .hosts(url)
    .hostConnectionLimit(1)
    .failFast(false)
    .requestTimeout(requestTimeout.seconds)
    .build()

  private[this] lazy val req = Request(endpoint)

  def check(): Future[Boolean] = {
    log.debug("checking http: %s".format(target))

    client(req) map { resp =>
      if (resp.getStatusCode() == statusCode) true
      else false
    } handle {
      case _ => false
    }
  }

  def verifyConfig(): Boolean = {
    Map("target" -> false, "responseCode" -> true) map { item =>
      HealthCheck.checkParameter(config.params, item._1, name, item._2)
    }
    true
  }
}

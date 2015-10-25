package com.example.serviceDiscoveryAgent

import com.example.serviceDiscoveryAgent.config.{Config, JsonHandler}
import com.twitter.server.TwitterServer
import com.twitter.util.Await


object Main extends TwitterServer {

  private val configFile = flag(
    "config",
    "/etc/serviceDiscovery/serviceDiscovery.json",
    "Main configuration file")

  // agent's metrics
  val stats = statsReceiver.scope("serviceDiscoveryAgent")
  private val jsonParser = new JsonHandler()

  def main() {
    val config = jsonParser.parseFromFile[Config](configFile())
    val zookeeperHandler = new ZookeeperHandler(config.zookeeper, jsonParser)
    val checker = HealthCheck(config)
    val interval = config.interval

    val poller = new Poller(config, zookeeperHandler, checker, stats)

    // kick off main process
    poller.poll(interval)
    //start twitter admin server
    Await.ready(adminHttpServer)
  }
}
package com.example.serviceDiscoveryAgent

import com.example.serviceDiscoveryAgent.config.Config
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.finagle.util.DefaultTimer
import com.twitter.logging.Logger
import com.twitter.util._
import com.twitter.zk.ZNode


class Poller(
    config: Config,
    zookeeperHandler: ZookeeperHandler,
    checker: HealthCheck,
    stats: StatsReceiver) {

  private implicit val timer: Timer = DefaultTimer.twitter

  private[this] val upCounter = stats.counter("up")
  private[this] val downCounter = stats.counter("down")
  private[this] val nodeRemoveCounter = stats.counter("nodeRemoved")
  private[this] val nodeAddedCounter = stats.counter("nodeAdded")

  private[this] val log = Logger.get(getClass.getName)

  private def poll(interval: Int, zNode: Option[ZNode]): Future[Unit] = {

    log.debug("sleeping for %s".format(interval))
    Thread.sleep(interval * 1000)
    log.debug("waking up...")

    checker.check() map { ir =>
      log.debug("check status %b".format(ir))
      zNode match {
        case Some(zN) if !ir =>
          downCounter.incr()
          nodeRemoveCounter.incr()
          log.info("health check is down. Removing zk node")
          zN.delete()
          poll(interval, None)
        case None if ir =>
          upCounter.incr()
          nodeAddedCounter.incr()
          log.info("health check is up. Creating zk node")
          zookeeperHandler.create(Macros.parseData(config.zookeeper.zkData)) map { x =>
            poll(interval, x)
          }
        case _ =>
          log.debug("State hasn't changed. zNode: %s".format(zNode))
          if (ir) upCounter.incr()
          else downCounter.incr()
          poll(interval, zNode)
      }
    }
  }

  def poll(interval: Int): Future[Unit] = {

    val zNode: Future[Future[Option[ZNode]]] = checker.check() map { x =>
      if (x) {
        upCounter.incr()
        log.debug("health check succeed")
        zookeeperHandler.create(Macros.parseData(config.zookeeper.zkData))
      } else {
        downCounter.incr()
        log.debug("health check failed")
        Future(None)
      }
    }

    zNode.flatten map { x => poll(interval, x)}

    Future.Unit
  }
}

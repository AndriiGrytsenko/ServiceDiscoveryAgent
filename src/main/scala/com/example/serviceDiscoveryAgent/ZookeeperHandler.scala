package com.example.serviceDiscoveryAgent

import com.example.serviceDiscoveryAgent.config.{ZookeepConfig, Config, JsonHandler}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.logging.Logger
import com.twitter.util.TimeConversions._
import com.twitter.util.{Future, Timer}
import com.twitter.zk.{ZkClient, ZNode}
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException.{NodeExistsException, NoNodeException}
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import scala.collection.JavaConverters._

class ZookeeperHandler(zkConfig: ZookeepConfig, jsonParser: JsonHandler) {
  private val log = Logger.get(getClass.getName)
  private implicit val timer: Timer = DefaultTimer.twitter
  private val zkClient = ZkClient(
    zkConfig.connectString,
    zkConfig.connectTimeout.seconds,
    zkConfig.sessionTimeout.seconds)

  private val zNode = ZNode(zkClient, zkConfig.base)

  private def getCidJson(value: Int): Array[Byte] = jsonParser.toJson(Map("cid" -> value))

  def ensureParentExists(zkClient: ZkClient, zNode: ZNode): Future[ZNode] = {
    log.debug("checking parent %s".format(zNode.toString()))
    zNode.exists() rescue {
      case e: NoNodeException => {
        val parentNode = zNode.parent

        ensureParentExists(zkClient, parentNode) flatMap { _ =>
          log.info("Creating parent node %s".format(zNode.toString()))
          zNode.create(getCidJson(0), OPEN_ACL_UNSAFE.asScala) handle { case e1: NodeExistsException =>
            zNode
          }
        }
      }
    }
  }

  def create(nodeData: String): Future[Option[ZNode]] = {
    log.debug("Creating zk node in %s".format(zkConfig.base))
    ensureParentExists(zkClient, zNode) flatMap { _ =>
      val baseData = zNode.getData()
      baseData flatMap { data =>
        val jsonData = jsonParser.parse[Map[String, String]](new String(data.bytes))
        val cid = jsonData.get("cid") match {
          case Some(x) => x.toInt
          case _ => 0
        }
        log.info("current child id is %d with data %s".format(cid, nodeData))
        val newZNode = zNode.create(nodeData.getBytes, OPEN_ACL_UNSAFE.asScala,
          CreateMode.EPHEMERAL, child = Some(cid.toString))
        newZNode map { _ =>
          zNode.setData(getCidJson(cid + 1), cid)
        }
        newZNode map { x =>
          Some(x)
        } handle {
          case _ => None
        }
      }
    }
  }
}

package com.example.serviceDiscoveryAgent.config

case class ZookeepConfig(
   base: String,
   connectString: String,
   connectTimeout: Int,
   sessionTimeout: Int,
   zkData: String)

case class Target(
    name: String,
    params: Map[String, String])

case class Config(
  zookeeper: ZookeepConfig,
  target: Target,
  interval: Int)

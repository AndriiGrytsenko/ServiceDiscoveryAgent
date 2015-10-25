package com.example.serviceDiscoveryAgent.healthcheck

import com.example.serviceDiscoveryAgent.HealthCheck
import com.twitter.util.Future

class DummyCheck extends HealthCheck{
  def check(): Future[Boolean] = Future(true)

  def verifyConfig(): Boolean = true
}

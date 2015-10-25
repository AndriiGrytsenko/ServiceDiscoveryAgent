package com.example.serviceDiscoveryAgent.utils

import com.twitter.util.Future

import sys.process._

object RunCommand {
  def run(command: List[String]) = Future {
    try {
      (command !!).trim
    } catch {
      case _: Throwable => throw new Exception("Command %s failed".format(command))
    }
  }
}

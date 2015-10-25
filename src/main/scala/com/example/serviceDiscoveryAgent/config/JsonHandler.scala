package com.example.serviceDiscoveryAgent.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.io.Source

class JsonHandler() {
  private[this] val jsonMapper = new ObjectMapper() with ScalaObjectMapper
  jsonMapper.registerModule(DefaultScalaModule)
  jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
  jsonMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)

  def parseFromFile[T](filename: String)(implicit m : Manifest[T]): T = {
    val content = Source.fromFile(filename).getLines().mkString
    parse[T](content)
  }

  def parse[T](content: String)(implicit m : Manifest[T]): T = {
    jsonMapper.readValue[T](content)
  }

  def toJson[T](content: T): Array[Byte] = {
    jsonMapper.writeValueAsBytes(content)
  }
}

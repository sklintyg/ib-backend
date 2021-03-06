package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object Conf {

  val baseUrl = System.getProperty("baseUrl")
  println(baseUrl)
  val httpConf = http
    .baseURL(baseUrl)
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .connectionHeader("keep-alive")

}
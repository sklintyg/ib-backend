package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import scala.io.Source._
import collection.mutable.{ HashMap, MultiMap, Set }
import scala.collection.mutable.MutableList
import scala.util.parsing.json._

import java.util.UUID

import scalaj.http._

object Utils {

  val baseUrl = System.getProperty("baseUrl", "http://localhost:8990" )

  // Save added IDs so we can clean them
  var utredningsIdn = MutableList[String]()

  def cleanUtredningarForLandstingsId(hsaId: String) = {
    var url = baseUrl + "/api/test/vardgivare/" + hsaId
    Http(url)
      .method("delete")
      .header("content-type", "application/json")
      .asString
  }

  def cleanAll() : Unit = {
    utredningsIdn.foreach(id => {
      var url = baseUrl + "/api/test/utredningar/" + id
      Http(url)
        .method("delete")
        .header("content-type", "application/json")
        .asString
    })
    utredningsIdn.clear()
  }

  def injectUtredningar(file: String): List[String] = {
    val bufferedSource = fromFile("src/test/resources/data/vardenheter.csv")
    var counter = 0
    for (line <- bufferedSource.getLines().drop(1)) {
      var i = 0
      for (i <- 1 to 10) {
        val cols = line.split(",").map(_.trim)
        utredningsIdn += createUtredning(file, cols(0), cols(1))
        counter = counter + 1
      }
    }
    bufferedSource.close()
    println(s"Added ${counter} utredningar")
    return utredningsIdn.toList
  }

  def createUtredning(file: String, vardenhetHsaId: String, vardgivareHsaId: String): String = {
    var url = baseUrl + "/api/test/utredningar"

    var json = fromFile(s"src/test/resources/request/${file}").mkString
    json = json.replaceAll("\\$\\{vardenhetHsaId\\}", vardenhetHsaId)
    json = json.replaceAll("\\$\\{vardgivareHsaId\\}", vardgivareHsaId)

    val response: HttpResponse[String] = Http(url)
      .postData(json)
      .method("post")
      .option(HttpOptions.allowUnsafeSSL)
      .option(HttpOptions.connTimeout(5000))
      .option(HttpOptions.readTimeout(5000))
      .header("Content-type", "application/json").asString

    val result = JSON.parseFull(response.body).getOrElse(0).asInstanceOf[Map[String, Any]]
    val innerMap = result("entity").asInstanceOf[Map[String, Double]]

    return innerMap("utredningId").toInt.toString
  }
}

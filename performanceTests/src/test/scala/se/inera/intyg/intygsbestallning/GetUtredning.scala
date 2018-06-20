package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.session._
import scala.concurrent.duration._
import collection.mutable.{ HashMap, MultiMap, Set, MutableList }
import scala.util.Random

class GetUtredning extends Simulation {
  val random = new Random
  val numberOfUsers = 10
  var utredningar = List[String]()
  val testpersonnummer = csv("data/testpersonnummerSkatteverket.csv").circular

  val feeder = Iterator.continually(
    Map(("id", utredningar(random.nextInt(utredningar.size))))
  )

  before {
    println("Injecting utredningar")
    utredningar = Utils.injectUtredningar("utredning.json", 10)
  }

  val scn = scenario("GetUtredning")
    .exec(Login.loginAs("Simona"))
    .feed(feeder)
    .exec(http("Getting utredning ${id}")
      .get("/api/utredningar/${id}"))

  after {
    println("Cleanup")
    Utils.cleanAll()
  }
  setUp(scn.inject(rampUsers(numberOfUsers) over (100 seconds)).protocols(Conf.httpConf))

}


package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import scala.concurrent.duration._

class GetBestallningarForVardenhet extends Simulation {
  before {
    println("Setup")
    Utils.injectUtredningar("utredning-bestalld.json")
  }
  val numberOfUsers = 10
  val vardenheter = jsonFile("data/vardenheter.json").circular

  val scn = scenario("GetBestallningarForVardenhet")
    .exec(Login.loginAs("Ingbritt"))
    .feed(vardenheter)
    .exec(http("GetBestallningarForVardenhet ${vardenhetHsaId}")
      .post("/api/vardadmin/bestallningar")
      .headers(Headers.json)
      .body(ElFileBody("request/getBestallningarForVardenhet.json")).asJSON
      .check(
        status.is(200),
        jsonPath("$.totalCount").saveAs("count")
      ))
    .exec( session => {
      println( "Count was: " + session("count").as[String] )
      session
    })

  after {
    println("Cleanup")
    Utils.cleanAll()
  }
  setUp(scn.inject(rampUsers(numberOfUsers) over (10 seconds)).protocols(Conf.httpConf))

}


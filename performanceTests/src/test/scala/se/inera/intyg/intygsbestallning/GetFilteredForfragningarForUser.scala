package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import scala.concurrent.duration._

class GetFilteredForfragningarForUser extends Simulation {
  before {
    println("Setup")
    Utils.injectUtredningar("utredning.json", 10)
  }
  val numberOfUsers = 100
  val vardenheter = csv("data/vardenheter.csv").circular
  var user = ""
  val scn = scenario("GetFilteredForfragningarForUser")
    .feed(vardenheter)
    .exec(http("Login as ${user}")
      .post("/fake")
      .headers(Headers.form_urlencoded)
      .formParam("userJsonDisplay", """{
                                      | "hsaId": "${user}",
                                      | "forNamn": "Ingbritt",
                                      | "efterNamn": "Filt",
                                      | "enhetId": "${vardenhetHsaId}",
                                      | "systemRoles": [
                                      |  "INTYG;FMU-VARDADMIN-${vardenhetHsaId}"
                                      | ],
                                      | "relayState": "FMU"
                                      |}""".stripMargin.replaceAll("\n", " "))
    )
    .exec(http("GetFilteredForfragningarForUser for ${user} at ${vardenhetHsaId}")
      .post("/api/vardadmin/internforfragningar")
      .headers(Headers.json)
      .body(StringBody("""{ "vardgivareHsaId" : "${vardgivareHsaId}" }""")).asJSON
      .check(
        status.is(200)
      ))

  after {
    println("Cleanup")
    Utils.cleanAll()
  }
  setUp(scn.inject(rampUsers(numberOfUsers) over (120 seconds)).protocols(Conf.httpConf))

}


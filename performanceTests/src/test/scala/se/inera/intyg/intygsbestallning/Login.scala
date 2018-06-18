package se.inera.intyg.intygsbestallning
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object Login {

  val logins = Map(
    "Ingbritt" -> s"""{
        | "hsaId": "ib-user-2",
        | "forNamn": "Ingbritt",
        | "efterNamn": "Filt",
        | "enhetId": "IFV1239877878-1042",
        | "systemRoles": [
        |  "INTYG;FMU-VARDADMIN-IFV1239877878-1042"
        | ],
        | "relayState": "FMU"
        |}""".stripMargin.replaceAll("\n", " "),
    "Simona" -> s"""
        |{
        | "hsaId": "ib-user-1",
        | "forNamn": "Simona",
        | "efterNamn": "Samordnare",
        | "enhetId": "IFV1239877878-1042",
        | "systemRoles": [
        |  "INTYG;FMU-SAMORDNARE-IFV1239877878-1041"
        | ],
        | "relayState": "FMU"
        |}""".stripMargin.replaceAll("\n", " ")
  )

  def loginAs(login: String) = {
    http("Login")
      .post("/fake")
      .headers(Headers.form_urlencoded)
      .formParam("userJsonDisplay", logins(login))
  }
}
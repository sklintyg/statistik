package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadAldersgruppReport {
  def exec(user: Login.User) = RestCall.post(
    s"getAgeGroupsStatistics: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getAgeGroupsStatistics")
}

class InloggadAldersgrupp extends Simulation {
  val user = Login.processledare
  val scn = scenario("Aldersgrupp")
    .exec(Login.login(Login.processledare))
    .pause(1)
    .exec(InloggadAldersgruppReport.exec(user))
    .pause(1)

  setUp(scn.inject(rampUsers(2) over(2))).protocols(Conf.httpProtocol)
}
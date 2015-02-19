package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadLakare {
  def exec(user: Login.User) = RestCall.post(
    s"getNumberOfCasesPerLakare: ${user}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getNumberOfCasesPerLakare")
}
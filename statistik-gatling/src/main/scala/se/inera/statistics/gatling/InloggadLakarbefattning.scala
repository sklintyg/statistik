package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadLakarbefattning {
  def exec(user: Login.User) = RestCall.post(
    s"getNumberOfCasesPerLakarbefattning: ${user}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getNumberOfCasesPerLakarbefattning")
}
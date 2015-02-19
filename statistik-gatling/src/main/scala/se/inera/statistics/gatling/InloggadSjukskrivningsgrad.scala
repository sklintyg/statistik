package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadSjukskrivningsgrad {
  def exec(user: Login.User) = RestCall.post(
    s"getDegreeOfSickLeaveStatistics: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getDegreeOfSickLeaveStatistics")
}
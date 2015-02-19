package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadAldersgruppPagaende {
  def exec(user: Login.User) = RestCall.post(
    s"getAgeGroupsCurrentStatistics: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getAgeGroupsCurrentStatistics")
}
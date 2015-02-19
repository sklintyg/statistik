package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadOversikt {
  def exec(user: Login.User) = RestCall.post(
    s"getOverview: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getOverview")
}
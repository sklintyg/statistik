package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadSjukskrivningslangdPagaende {
  def exec(user: Login.User) = RestCall.post(
    s"getSickLeaveLengthCurrentData: ${user}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getSickLeaveLengthCurrentData")
}
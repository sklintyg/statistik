package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadSjukskrivningslangd {
  def exec(user: Login.User) = RestCall.post(
    s"getSickLeaveLengthData: ${user}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getSickLeaveLengthData")
}
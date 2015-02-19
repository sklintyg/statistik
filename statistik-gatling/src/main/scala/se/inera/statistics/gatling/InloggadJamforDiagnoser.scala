package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadJamforDiagnoser {
  def exec(user: Login.User, diagnoshash: String, filterhash: String) = RestCall.post(
    s"getDiagnosavsnittstatistik: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}" +
      s"/getJamforDiagnoserStatistik/$diagnoshash?filter=$filterhash")
}
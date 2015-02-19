package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadDiagnosKapitel {
  def exec(user: Login.User, kapitel: String) = RestCall.post(
    s"getDiagnosavsnittstatistik: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getDiagnosavsnittstatistik/${kapitel}")
}
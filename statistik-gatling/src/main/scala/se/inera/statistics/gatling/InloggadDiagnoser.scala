package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadDiagnoser {
  def exec(user: Login.User) = RestCall.post(
    s"getDiagnoskapitelstatistik: ${user.vardgivare}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getDiagnoskapitelstatistik")
}
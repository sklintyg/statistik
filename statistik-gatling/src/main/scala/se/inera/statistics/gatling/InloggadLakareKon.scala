package se.inera.statistics.gatling

import io.gatling.core.Predef._

object InloggadLakareKon {
  def exec(user: Login.User) = RestCall.post(
    s"getCasesPerDoctorAgeAndGenderStatistics: ${user}",
    s"${Conf.uri}/api/verksamhet/${user.vardgivare}/getCasesPerDoctorAgeAndGenderStatistics")
}
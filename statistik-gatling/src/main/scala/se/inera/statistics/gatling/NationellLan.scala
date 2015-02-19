package se.inera.statistics.gatling

import io.gatling.core.Predef._

object NationellLan {
  def exec = RestCall.get(
    s"getCountyStatistics",
    s"${Conf.uri}/api/getCountyStatistics")
}
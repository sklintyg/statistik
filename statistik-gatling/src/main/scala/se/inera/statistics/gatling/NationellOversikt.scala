package se.inera.statistics.gatling

import io.gatling.core.Predef._

object NationellOversikt {
  def exec = RestCall.get(
    s"getOverview",
    s"${Conf.uri}/api/getOverview")
}
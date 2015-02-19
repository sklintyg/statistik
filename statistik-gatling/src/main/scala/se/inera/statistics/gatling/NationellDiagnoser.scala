package se.inera.statistics.gatling

import io.gatling.core.Predef._

object NationellDiagnoser {
  def exec = RestCall.get(
    s"getDiagnoskapitel",
    s"${Conf.uri}/api/getDiagnoskapitel")
}
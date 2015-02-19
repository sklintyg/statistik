package se.inera.statistics.gatling

import io.gatling.core.Predef._
import io.gatling.core.session
import io.gatling.http.Predef._

object RestCall {
  val rest_headers = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Pragma" -> "no-cache",
    "Content-Type" -> "application/json;charset=utf-8")

  def post(name: String, url: session.Expression[String]) =
    exec(http(name)
      .post(url)
      .headers(rest_headers)
      .body(StringBody("{}"))
      .check(status.is(200)))

  def get(name: String, url: session.Expression[String]) =
    exec(http(name)
      .get(url)
      .headers(rest_headers)
      .check(status.is(200)))

}

package se.inera.statistics.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Conf {
  val uri = "http://localhost:8080"
  val httpProtocol = http
    .baseURL("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .contentTypeHeader("application/ocsp-request")
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:35.0) Gecko/20100101 Firefox/35.0")


}

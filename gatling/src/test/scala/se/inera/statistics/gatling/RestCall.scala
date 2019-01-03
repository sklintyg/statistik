/**
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

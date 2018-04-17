/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import io.gatling.http.Predef._

object Login {
  case class User(json: String, vardgivare: String)

  val login_headers = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val processledare = User("""{
    "fornamn":"Anna",
    "efternamn":"Urmodig",
    "hsaId":"HSA-AM1",
    "enhetId":"SE162321000255-O19466",
    "vardgivarId":"SE162321000255-O00001",
    "vardgivarniva":"true"
}""", "SE162321000255-O00001")

  val delprocessledare = User(s"""{
    "fornamn":"Anna",
    "efternamn":"Urmodig",
    "hsaId":"HSA-AM1",
    "enhetId":"SE162321000255-O19466",
    "vardgivarId":"SE162321000255-O00001",
    "vardgivarniva":"false"
}""", "SE162321000255-O00001")

  val verksamhetschef = User(s"""{
    "fornamn":"Anna",
    "efternamn":"Vemodig",
    "hsaId":"HSA-AM2",
    "enhetId":"SE162321000255-O11635",
    "vardgivarId":"SE162321000255-O00001",
    "vardgivarniva":"false"
}""", "SE162321000255-O00001")

  def login(user: User) =
    exec(http(s"login: ${user.json}")
      .get("/views/fakelogin.html"))
      .pause(1)
      .exec(http("fakelogin_login")
      .post("/fake")
      .headers(login_headers)
      .formParam("userJsonDisplay", user.json)
      .formParam("Login", "")
      .check(status.is(200)))
}

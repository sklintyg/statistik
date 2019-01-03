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
import scala.concurrent.duration._
import scala.language.postfixOps

import scala.util.Random

class StandardScenario extends Simulation {
  val pl = Login.processledare
  val dpl = Login.delprocessledare
  val vc = Login.verksamhetschef

  def userWait = { Random.nextDouble() * 10L seconds }

  val processledare = scenario("Standard, processledare")
    .exec(Login.login(pl))
    .pause(userWait)
    .exec(InloggadOversikt.exec(pl))
    .pause(userWait)
    .exec(InloggadSjukfall.exec(pl))
    .pause(userWait)
    .exec(InloggadSjukskrivningsgrad.exec(pl))
    .pause(userWait)
    .exec(InloggadDiagnoser.exec(pl))
    .pause(userWait)
    .exec(InloggadSjukfall.exec(pl))
    .pause(userWait)

  val delprocessledare = scenario("Standard, delprocessledare")
    .exec(Login.login(dpl))
    .pause(userWait)
    .exec(InloggadOversikt.exec(dpl))
    .pause(userWait)
    .exec(InloggadEnheter.exec(dpl))
    .pause(userWait)
    .exec(InloggadDiagnoser.exec(dpl))
    .pause(userWait)
    .exec(InloggadDiagnosKapitel.exec(dpl, "M00-M99"))
    .pause(userWait)
    .exec(InloggadDiagnosKapitel.exec(dpl, "F00-F99"))
    .pause(userWait)
    .exec(InloggadAldersgruppReport.exec(dpl))
    .pause(userWait)
    .exec(InloggadSjukskrivningsgrad.exec(dpl))
    .pause(userWait)
    .exec(InloggadSjukskrivningslangd.exec(dpl))
    .pause(userWait)

  val verksamhetschef = scenario("Standard, verksamhetschef")
    .exec(Login.login(vc))
    .pause(userWait)
    .exec(InloggadOversikt.exec(vc))
    .pause(userWait)
    .exec(InloggadSjukfall.exec(vc))
    .pause(userWait)
    .exec(InloggadDiagnoser.exec(vc))
    .pause(userWait)
    .exec(InloggadDiagnosKapitel.exec(vc, "M00-M99"))
    .pause(userWait)
    .exec(InloggadDiagnosKapitel.exec(vc, "F00-F99"))
    .pause(userWait)
    .exec(InloggadAldersgruppReport.exec(vc))
    .pause(userWait)
    .exec(InloggadSjukskrivningsgrad.exec(vc))
    .pause(userWait)
    .exec(InloggadSjukskrivningslangd.exec(vc))
    .pause(userWait)

  val medborgare = scenario("Standard, medborgare")
    .exec(NationellOversikt.exec)
    .pause(userWait)
    .exec(NationellAldersgrupp.exec)
    .pause(userWait)
    .exec(NationellDiagnoser.exec)
    .pause(userWait)
    .exec(NationellDiagnosavsnitt.exec("A00-B99"))
    .pause(userWait)
    .exec(NationellLan.exec)
    .pause(userWait)
    .exec(NationellSjukfall.exec)
    .pause(userWait)
    .exec(NationellSjukskrivningPerKonPerLan.exec)
    .pause(userWait)
    .exec(NationellSjukskrivningsgrad.exec)
    .pause(userWait)
    .exec(NationellSjukskrivningslangd.exec)
    .pause(userWait)

  setUp(
    processledare.inject(rampUsers(2) over 60),
    delprocessledare.inject(rampUsers(6) over 60),
    medborgare.inject(rampUsers(50) over 60 ),
    verksamhetschef.inject(rampUsers(25) over 60)
  ).protocols(Conf.httpProtocol)

}

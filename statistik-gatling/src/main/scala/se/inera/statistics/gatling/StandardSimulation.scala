package se.inera.statistics.gatling

import io.gatling.core.Predef._

class StandardSimulation extends Simulation {
  val pl = Login.processledare
  val dpl = Login.delprocessledare
  val vc = Login.verksamhetschef

  val userWait = 1

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

  setUp(
    processledare.inject(rampUsers(5) over(300)),
    delprocessledare.inject(rampUsers(10) over(300)),
    medborgare.inject(rampUsers(100) over(300)),
    verksamhetschef.inject(rampUsers(50) over(300))
  ).protocols(Conf.httpProtocol)

}

package se.inera.statistics.spec

import se.inera.statistics.service.countypopulation.CountyPopulation
import se.inera.statistics.web.reports.ReportsUtil

import java.time.LocalDate

class AngivnaInvanarantal {

    private final ReportsUtil reportsUtil = new ReportsUtil()

    String länkod
    Integer invånarantal
    String datum

    def values = [:]

    public void beginTable() {
        reportsUtil.clearCountyPopulation()
    }

    public void reset() {
        länkod = null
        invånarantal = null
    }

    public void setKommentar(String kommentar) {}

    public void execute() {
        def valuesPerDate = values.get(datum, [:])
        valuesPerDate[länkod] = invånarantal
    }

    public void endTable() {
        values.each { k, v ->
            reportsUtil.insertCountyPopulation(v, k)
        }
    }

}

package se.inera.statistics.warehouse

import se.inera.statistics.context.StartUp
import se.inera.statistics.service.helper.ConversionHelper
import se.inera.statistics.service.warehouse.Aisle
import se.inera.statistics.service.warehouse.Sjukfall

import se.inera.statistics.service.warehouse.Warehouse
import se.inera.statistics.service.warehouse.Fact

class Info {

    String vardgivareId
    String personId
    int sjukskrivningsgrad

    String getPersonLines() {
        int person = ConversionHelper.patientIdToInt(personId)
        Iterator<Fact> lines = extractPersonLines(person).iterator()
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            Fact line = lines.next()
            if (line.patient == person) {
                sb.append(line.toString()).append('\n')
            }
        }
        sb.toString()
    }

    String getSjukfall() {
        int person = ConversionHelper.patientIdToInt(personId)
        List<Fact> lineList = extractPersonLines(person)
        Iterator<Fact> lines = sortByDate(lineList)

        Sjukfall sjukfall = null
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            Fact line = lines.next()
            if (sjukfall == null) {
                sjukfall = new Sjukfall(line)
            } else {
                Sjukfall newSjukfall = sjukfall.join(line)
                if (newSjukfall != sjukfall) {
                    sb.append(sjukfall).append('\n')
                    sjukfall = newSjukfall
                }
            }
        }
        if (sjukfall != null) {
            sb.append(sjukfall).append('\n')
        }
        sb.toString()
    }

    String getSjukfall(Closure describe) {
        int person = ConversionHelper.patientIdToInt(personId)
        List<Fact> lineList = extractPersonLines(person)
        Iterator<Fact> lines = sortByDate(lineList)

        Sjukfall sjukfall = null
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            Fact line = lines.next()
            if (sjukfall == null) {
                sjukfall = new Sjukfall(line)
            } else {
                Sjukfall newSjukfall = sjukfall.join(line)
                if (!newSjukfall.isExtended()) {
                    sb.append(describe(sjukfall)).append('\n')
                    sjukfall = newSjukfall
                }
            }
        }
        if (sjukfall != null) {
            sb.append(describe(sjukfall)).append('\n')
        }
        sb.toString()
    }

    String getSjukfallWithDiagnos() {
        getSjukfall { "SjukfallWithDiagnos{${it.diagnoskapitel}}" + it }
    }

    String getSjukfallWithSjukskrivningsgrad() {
        getSjukfall {"SjukfallWithDiagnos{${it.diagnoskapitel}}" + it}
    }

    private Iterator<Fact> sortByDate(List<Fact> lineList) {
        lineList.sort({ o1, o2 -> o1.startdatum - o2.startdatum}).iterator()
    }

    private List extractPersonLines(int person) {
        Warehouse bean = (Warehouse) StartUp.context.getBean(Warehouse.class)
        Aisle aisle = bean.get(vardgivareId)
        Iterator lines = aisle.iterator()
        List<Fact> patientLines = new ArrayList<>()
        while (lines.hasNext()) {
            Fact line = lines.next()
            if (line.patient == person) {
                patientLines.add(line)
            }
        }
        patientLines
    }

    private List extractPersonLinesForSjukskrivningsgrad(int person, int grad) {
        Warehouse bean = (Warehouse) StartUp.context.getBean(Warehouse.class)
        Aisle aisle = bean.get(vardgivareId)
        Iterator lines = aisle.iterator()
        List<Fact> patientLines = new ArrayList<>()
        while (lines.hasNext()) {
            Fact line = lines.next()
            if (line.patient == person && line.sjukskrivningsgrad == sjukskrivningsgrad) {
                patientLines.add(line)
            }
        }
        patientLines
    }

}

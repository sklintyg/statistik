package se.inera.statistics.warehouse

import se.inera.statistics.context.StartUp
import se.inera.statistics.service.helper.DocumentHelper
import se.inera.statistics.service.warehouse.Aisle
import se.inera.statistics.service.warehouse.Sjukfall
import se.inera.statistics.service.warehouse.SjukfallWithDiagnos
import se.inera.statistics.service.warehouse.SjukfallWithSjukskrivningsgrad
import se.inera.statistics.service.warehouse.Warehouse
import se.inera.statistics.service.warehouse.WideLine

class Info {

    String vardgivareId
    String personId
    int sjukskrivningsgrad

    String getPersonLines() {
        int person = DocumentHelper.patientIdToInt(personId)
        Iterator<WideLine> lines = extractPersonLines(person).iterator()
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            WideLine line = lines.next()
            if (line.patient == person) {
                sb.append(line.toString()).append('\n')
            }
        }
        sb.toString()
    }

    String getSjukfall() {
        int person = DocumentHelper.patientIdToInt(personId)
        List<WideLine> lineList = extractPersonLines(person)
        Iterator<WideLine> lines = sortByDate(lineList)

        Sjukfall sjukfall = null
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            WideLine line = lines.next()
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

    String getSjukfallWithDiagnos() {
        int person = DocumentHelper.patientIdToInt(personId)
        List<WideLine> lineList = extractPersonLines(person)
        Iterator<WideLine> lines = sortByDate(lineList)

        SjukfallWithDiagnos sjukfall = null
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            WideLine line = lines.next()
            if (sjukfall == null) {
                sjukfall = new SjukfallWithDiagnos(line)
            } else {
                SjukfallWithDiagnos newSjukfall = sjukfall.join(line)
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

    String getSjukfallWithSjukskrivningsgrad() {
        int person = DocumentHelper.patientIdToInt(personId)
        List<WideLine> lineList = extractPersonLinesForSjukskrivningsgrad(person, sjukskrivningsgrad)
        Iterator<WideLine> lines = sortByDate(lineList)

        SjukfallWithSjukskrivningsgrad sjukfall = null
        StringBuilder sb = new StringBuilder()
        while (lines.hasNext()) {
            WideLine line = lines.next()
            if (sjukfall == null) {
                sjukfall = new SjukfallWithSjukskrivningsgrad(line)
            } else {
                SjukfallWithSjukskrivningsgrad newSjukfall = sjukfall.join(line)
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

    private Iterator<WideLine> sortByDate(List<WideLine> lineList) {
        lineList.sort(new Comparator<WideLine>() {
            int compare(WideLine o1, WideLine o2) {
                return o1.kalenderperiod - o2.kalenderperiod
            }
        }).iterator()
    }

    private List extractPersonLines(int person) {
        Warehouse bean = (Warehouse) StartUp.context.getBean(Warehouse.class)
        Aisle aisle = bean.get(vardgivareId)
        Iterator lines = aisle.iterator()
        List<WideLine> patientLines = new ArrayList<>()
        while (lines.hasNext()) {
            WideLine line = lines.next()
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
        List<WideLine> patientLines = new ArrayList<>()
        while (lines.hasNext()) {
            WideLine line = lines.next()
            if (line.patient == person && line.sjukskrivningsgrad == sjukskrivningsgrad) {
                patientLines.add(line)
            }
        }
        patientLines
    }

}

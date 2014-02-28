package se.inera.statistics.hsa

/**
 * Created by roger on 28/02/14.
 */
class HsaStruktur {

    public boolean skapaLakare(String id) {
        System.err.println("Skapade läkare " + id)
        return true
    }

    public boolean skapaEnhet(String vgId, String eId) {
        System.err.println("Skapade enhet " + vgId + " " + eId)
        return true
    }

    public boolean skapaUppdrag(String lId, String eId, String uppdrag) {
        System.err.println("Skapade uppdrag " + lId + " " + eId + " " + uppdrag)
        return true
    }
}

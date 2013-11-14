package se.inera.statistics.hsa.stub;

import java.util.List;

/**
 * @author andreaskaltenbach
 */
public class Medarbetaruppdrag {

    public static final String STATISTIK = "Statistik";

    private String hsaId;
    private List<String> enhetIds;

    private String andamal = STATISTIK;

    public Medarbetaruppdrag() {
    }

    public Medarbetaruppdrag(String hsaId, List<String> enhetIds) {
        this(hsaId, enhetIds, STATISTIK);
    }

    public Medarbetaruppdrag(String hsaId, List<String> enhetIds, String andamal) {
        this.hsaId = hsaId;
        this.enhetIds = enhetIds;
        this.andamal = andamal;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public List<String> getEnhetIds() {
        return enhetIds;
    }

    public void setEnhetIds(List<String> enhetIds) {
        this.enhetIds = enhetIds;
    }

    public String getAndamal() {
        return andamal;
    }

    public void setAndamal(String andamal) {
        this.andamal = andamal;
    }
}

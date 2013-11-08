package se.inera.statistics.hsa.stub;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.hsa.model.Vardenhet;

/**
 * @author rlindsjo
 */
public class HsaServiceStub {

    // Data cache

    private List<Vardenhet> vardenheter = new ArrayList<>();
    private List<Medarbetaruppdrag> medarbetaruppdrag = new ArrayList<>();

    public Vardenhet getVardenhet(String hsaIdentity) {

        for (Vardenhet vardenhet : vardenheter) {
            if (vardenhet.getId().equals(hsaIdentity)) {
                return vardenhet;
            }
        }
        return null;
    }

    public List<Vardenhet> getVardenhets() {
        return vardenheter;
    }

    public void deleteEnhet(String id) {
        for (Vardenhet enhet: vardenheter) {
            if (enhet.getId().equals(id)) {
                vardenheter.remove(enhet);
            }
        }
    }

    public List<Medarbetaruppdrag> getMedarbetaruppdrag() {
        return medarbetaruppdrag;
    }
}

package se.inera.auth;

import java.util.List;

import se.inera.auth.model.VerksamhetMapperObject;

/**
 * @author andreaskaltenbach
 */
public class FakeCredentials {

    private String hsaId;
    private String fornamn;
    private String efternamn;
    private boolean lakare;
    private List<VerksamhetMapperObject> vardenhets;

    public FakeCredentials() {
    }

    public FakeCredentials(String hsaId, String fornamn, String efternamn, boolean lakare, List<VerksamhetMapperObject> vardenhets) {
        this.hsaId = hsaId;
        this.fornamn = fornamn;
        this.efternamn = efternamn;
        this.lakare = lakare;
        this.vardenhets = vardenhets;
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getFornamn() {
        return fornamn;
    }

    public String getEfternamn() {
        return efternamn;
    }

    public boolean isLakare() {
        return lakare;
    }

    public List<VerksamhetMapperObject> getVardenhets() {
        return vardenhets;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public void setFornamn(String fornamn) {
        this.fornamn = fornamn;
    }

    public void setEfternamn(String efternamn) {
        this.efternamn = efternamn;
    }

    public void setLakare(boolean lakare) {
        this.lakare = lakare;
    }

    public void setVardenhets(List<VerksamhetMapperObject> vardenhets) {
        this.vardenhets = vardenhets;
    }

    @Override
    public String toString() {
        return "FakeCredentials [hsaId=" + hsaId + ", fornamn=" + fornamn + ", efternamn=" + efternamn + ", lakare=" + lakare + ", vardenhets=" + vardenhets
                + "]";
    }

}

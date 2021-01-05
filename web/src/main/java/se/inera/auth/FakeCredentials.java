/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Per INTYG-2782, the fake login works like this:
 *
 * hsaId is the identifier for the person.
 * vardgivarIdSomProcessLedare is an array of vårdgivare hsaId's that the user shall have role "processledare" for. However,
 * the boolean 'vardgivarniva' can be used to override this, e.g: If vardgivarniva is false that effectively stops the user
 * from becoming processledare on any of the hsaId's specified in vardgivarIdSomProcessLedare.
 *
 * The reason for the boolean override is to make it possible in the fake login page to list vgIds in the user objects
 * for easy editing and "flipping" the role assignments by changing the boolean rather than having to add/remove vg hsaId's
 * manually.
 *
 * @author eriklupander
 */
@SuppressWarnings("serial")
public class FakeCredentials implements Serializable {

    private String hsaId;
    private String fornamn;
    private String efternamn;
    private List<String> vardgivarIdSomProcessLedare = new ArrayList<>();
    private boolean vardgivarniva;

    FakeCredentials() {
        //Do nothing but is needed by json mapper
    }

    public FakeCredentials(String hsaId, String fornamn, String efternamn) {
        this.hsaId = hsaId;
        this.fornamn = fornamn;
        this.efternamn = efternamn;
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

    public List<String> getVardgivarIdSomProcessLedare() {
        return vardgivarIdSomProcessLedare;
    }

    public boolean isVardgivarniva() {
        return vardgivarniva;
    }

    void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    void setFornamn(String fornamn) {
        this.fornamn = fornamn;
    }

    void setEfternamn(String efternamn) {
        this.efternamn = efternamn;
    }

    void setVardgivarIdSomProcessLedare(List<String> vardgivarIdSomProcessLedare) {
        this.vardgivarIdSomProcessLedare = vardgivarIdSomProcessLedare;
    }

    void setVardgivarniva(boolean vardgivarniva) {
        this.vardgivarniva = vardgivarniva;
    }


    @Override
    public String toString() {
        return "FakeCredentials{"
            + "hsaId='" + hsaId + '\''
            + ", fornamn='" + fornamn + '\''
            + ", efternamn='" + efternamn + '\''
            + ", vardgivarIdSomProcessLedare='" + vardgivarIdSomProcessLedare.stream().collect(Collectors.joining()) + '\''
            + ", vardgivarniva='" + vardgivarniva + '\''
            + '}';
    }

}

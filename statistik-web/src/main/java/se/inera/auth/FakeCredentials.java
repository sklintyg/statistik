/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author andreaskaltenbach
 */
@SuppressWarnings("serial")
public class FakeCredentials implements Serializable {

    private String hsaId;
    private String fornamn;
    private String efternamn;
    private List<String> vardgivarId = new ArrayList<>();

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

    public List<String> getVardgivarId() {
        return vardgivarId;
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

    void setVardgivarId(List<String> vardgivarId) {
        this.vardgivarId = vardgivarId;
    }


    @Override
    public String toString() {
        return "FakeCredentials{"
                + "hsaId='" + hsaId + '\''
                + ", fornamn='" + fornamn + '\''
                + ", efternamn='" + efternamn + '\''
                + ", vardgivarId='" + vardgivarId.stream().collect(Collectors.joining()) + '\''
                + '}';
    }

}

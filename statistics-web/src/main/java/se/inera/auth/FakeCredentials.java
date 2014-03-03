/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.auth;

import java.io.Serializable;

/**
 * @author andreaskaltenbach
 */
@SuppressWarnings("serial")
public class FakeCredentials implements Serializable {

    private String hsaId;
    private String fornamn;
    private String efternamn;
    private boolean vardgivarniva;
    private String enhetId;
    private String vardgivarId;

    public FakeCredentials() {
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

    public String getEnhetId() {
        return enhetId;
    }

    public String getVardgivarId() {
        return vardgivarId;
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

    public void setEnhetId(String enhetId) {
        this.enhetId = enhetId;
    }

    public void setVardgivarId(String vardgivarId) {
        this.vardgivarId = vardgivarId;
    }

    public void setVardgivarniva(boolean vardgivarniva) {
        this.vardgivarniva = vardgivarniva;
    }

    public boolean isVardgivarniva() {
        return vardgivarniva;
    }

    @Override
    public String toString() {
        return "FakeCredentials{hsaId='" + hsaId + "', fornamn='" + fornamn + "', efternamn='" + efternamn + "', vardgivarniva=" + vardgivarniva + '}';
    }
}

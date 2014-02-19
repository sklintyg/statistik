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

    private final String hsaId;
    private final String fornamn;
    private final String efternamn;
    private final boolean lakare;

    public FakeCredentials(String hsaId, String fornamn, String efternamn, boolean lakare) {
        this.hsaId = hsaId;
        this.fornamn = fornamn;
        this.efternamn = efternamn;
        this.lakare = lakare;
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

    @Override
    public String toString() {
        return "FakeCredentials{hsaId='" + hsaId + "', fornamn='" + fornamn + "', efternamn='" + efternamn + "', lakare=" + lakare + '}';
    }
}

/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.stub;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Locale;

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
        return hsaId == null ? null : hsaId.toUpperCase(Locale.ENGLISH);
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public List<String> getEnhetIds() {
        return enhetIds == null ? null : Lists.transform(enhetIds, new Function<String, String>() {
            @Override
            public String apply(String enhetId) {
                return enhetId.toUpperCase(Locale.ENGLISH);
            }
        });
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

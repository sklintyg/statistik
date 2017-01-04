/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import se.inera.statistics.hsa.model.HsaIdEnhet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FilterDataResponse {

    private String filterhash;
    private List<String> diagnoser;
    private List<String> enheter;
    private List<String> sjukskrivningslangd;
    private List<String> aldersgrupp;

    //To be used by json converter
    private FilterDataResponse() {
    }

    FilterDataResponse(String filterhash, Collection<String> diagnoser, Collection<HsaIdEnhet> enheter, Collection<String> sjukskrivningslangd, Collection<String> aldersgrupp) {
        this.filterhash = filterhash;
        this.diagnoser = diagnoser == null ? null : Collections.unmodifiableList(new ArrayList<>(diagnoser));
        this.sjukskrivningslangd = sjukskrivningslangd == null ? null : Collections.unmodifiableList(new ArrayList<>(sjukskrivningslangd));
        this.aldersgrupp = aldersgrupp == null ? null : Collections.unmodifiableList(new ArrayList<>(aldersgrupp));
        this.enheter = enheter == null ? null : Lists.transform(new ArrayList<>(enheter), new Function<HsaIdEnhet, String>() {
            @Override
            public String apply(HsaIdEnhet hsaId) {
                return hsaId.getId();
            }
        });
    }

    public FilterDataResponse(Filter filter) {
        this(filter.getFilterHash(), filter.getDiagnoser(), filter.getEnheter(), filter.getSjukskrivningslangd(), filter.getAldersgrupp());
    }

    public static FilterDataResponse empty() {
        return new FilterDataResponse(null, null, null, null, null);
    }

    public List<String> getDiagnoser() {
        return diagnoser;
    }

    public List<String> getEnheter() {
        return enheter;
    }

    public List<String> getSjukskrivningslangd() {
        return sjukskrivningslangd;
    }

    public List<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public String getFilterhash() {
        return filterhash;
    }

}

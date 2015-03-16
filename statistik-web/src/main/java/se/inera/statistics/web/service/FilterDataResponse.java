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
package se.inera.statistics.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FilterDataResponse {
    private List<String> diagnoser;
    private List<String> enheter;

    //To be used by json converter
    FilterDataResponse() {
    }

    public FilterDataResponse(Collection<String> diagnoser, Collection<String> enheter) {
        this.diagnoser = diagnoser == null ? null : Collections.unmodifiableList(new ArrayList<>(diagnoser));
        this.enheter = enheter == null ? null : Collections.unmodifiableList(new ArrayList<>(enheter));
    }

    public List<String> getDiagnoser() {
        return diagnoser;
    }

    public List<String> getEnheter() {
        return enheter;
    }

}

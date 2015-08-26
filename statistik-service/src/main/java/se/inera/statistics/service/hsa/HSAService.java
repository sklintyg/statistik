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
package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface HSAService {

    String ENHETS_TYP = "enhetsTyp";
    String HSA_INFO_ENHET = "enhet";
    String HSA_INFO_HUVUDENHET = "huvudenhet";
    String HSA_INFO_VARDGIVARE = "vardgivare";
    String HSA_INFO_PERSONAL = "personal";

    ObjectNode getHSAInfo(HSAKey key);

}

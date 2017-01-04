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
package se.inera.statistics.service.landsting;

import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

import java.util.Collections;
import java.util.List;

public class LandstingEnhetFileData {

    private HsaIdVardgivare vgId;
    private List<LandstingEnhetFileDataRow> rows;
    private String userName;
    private HsaIdUser userId;
    private String fileName;

    public LandstingEnhetFileData(HsaIdVardgivare vgId, List<LandstingEnhetFileDataRow> rows, String userName, HsaIdUser userId, String fileName) {
        this.vgId = vgId == null ? new HsaIdVardgivare("") : vgId;
        this.rows = rows == null ? Collections.<LandstingEnhetFileDataRow>emptyList() : Collections.unmodifiableList(rows);
        this.userName = userName;
        this.userId = userId;
        this.fileName = fileName;
    }

    public HsaIdVardgivare getVgId() {
        return vgId;
    }

    public List<LandstingEnhetFileDataRow> getRows() {
        return rows;
    }

    public String getUserName() {
        return userName;
    }

    public HsaIdUser getUserId() {
        return userId;
    }

    public String getFileName() {
        return fileName;
    }
}

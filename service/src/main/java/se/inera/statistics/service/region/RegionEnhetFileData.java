/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.region;

import java.util.Collections;
import java.util.List;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

public class RegionEnhetFileData {

    private HsaIdVardgivare vgId;
    private List<RegionEnhetFileDataRow> rows;
    private String userName;
    private HsaIdUser userId;
    private String fileName;

    public RegionEnhetFileData(HsaIdVardgivare vgId, List<RegionEnhetFileDataRow> rows, String userName, HsaIdUser userId,
        String fileName) {
        this.vgId = vgId == null ? new HsaIdVardgivare("") : vgId;
        this.rows = rows == null ? Collections.<RegionEnhetFileDataRow>emptyList() : Collections.unmodifiableList(rows);
        this.userName = userName;
        this.userId = userId;
        this.fileName = fileName;
    }

    public HsaIdVardgivare getVgId() {
        return vgId;
    }

    public List<RegionEnhetFileDataRow> getRows() {
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

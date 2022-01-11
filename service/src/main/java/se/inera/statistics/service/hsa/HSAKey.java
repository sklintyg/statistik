/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.hsa;

import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

public class HSAKey {

    private final HsaIdVardgivare vardgivareId;
    private final HsaIdEnhet enhetId;
    private final HsaIdLakare lakareId;

    public HSAKey(HsaIdVardgivare vardgivareId, HsaIdEnhet enhetId, HsaIdLakare lakareId) {
        this.vardgivareId = vardgivareId;
        this.enhetId = enhetId;
        this.lakareId = lakareId;
    }

    public HSAKey(String vardgivareId, String enhetId, String lakareId) {
        this(vardgivareId != null ? new HsaIdVardgivare(vardgivareId) : null,
            enhetId != null ? new HsaIdEnhet(enhetId) : null,
            lakareId != null ? new HsaIdLakare(lakareId) : null);
    }

    public HsaIdLakare getLakareId() {
        return lakareId;
    }

    public HsaIdVardgivare getVardgivareId() {
        return vardgivareId;
    }

    public HsaIdEnhet getEnhetId() {
        return enhetId;
    }

    @Override
    public String toString() {
        return "HSAKey{" + "vardgivareId='" + vardgivareId + '\'' + ", enhetId='" + enhetId + '\'' + ", lakareId='" + lakareId + '\'' + '}';
    }
}

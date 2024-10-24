/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.fileservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "FileGetHsaUnitsResponse",
    namespace = "urn:riv:hsa:HsaWsResponder:3"
)
public class FileGetHsaUnitsResponse {

    @XmlElement(
        namespace = "urn:riv:hsa:HsaWsResponder:3"
    )
    private String startDate;
    @XmlElement(
        namespace = "urn:riv:hsa:HsaWsResponder:3"
    )
    private String endDate;
    @XmlElement(
        namespace = "urn:riv:hsa:HsaWsResponder:3"
    )
    private HsaUnits hsaUnits;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public HsaUnits getHsaUnits() {
        return hsaUnits;
    }

    public void setHsaUnits(HsaUnits hsaUnits) {
        this.hsaUnits = hsaUnits;
    }

}
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
package se.inera.statistics.fileservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "HsaUnits",
    namespace = "urn:riv:hsa:HsaWsResponder:3"
)
public class HsaUnits {

    @XmlElement(
        namespace = "urn:riv:hsa:HsaWsResponder:3"
    )
    private ArrayList<HsaUnit> hsaUnit = new ArrayList<>();

    public HsaUnits() {
    }

    public HsaUnits(ArrayList<HsaUnit> hsaUnit) {
        this.hsaUnit = hsaUnit;
    }

    public ArrayList<HsaUnit> getHsaUnit() {
        return hsaUnit;
    }

    public void setHsaUnit(ArrayList<HsaUnit> hsaUnit) {
        this.hsaUnit = hsaUnit;
    }

}

/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class StatisticsHsaUnitDto {

    private String hsaIdentity;
    private String careGiverHsaIdentity;
    private String location;
    private String countyCode;
    private String municipalityCode;
    private String municipalitySectionCode;
    private String municipalitySectionName;
    private GeoCoordDto geographicalCoordinatesRt90;
    private List<String> businessTypes = Collections.emptyList();
    private List<String> managements = Collections.emptyList();
    private List<String> businessClassificationCodes = Collections.emptyList();
    private List<String> careTypes = Collections.emptyList();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isArchived;

    public String getHsaIdentity() {
        return hsaIdentity;
    }

    public void setHsaIdentity(String hsaIdentity) {
        this.hsaIdentity = hsaIdentity;
    }

    public String getCareGiverHsaIdentity() {
        return careGiverHsaIdentity;
    }

    public void setCareGiverHsaIdentity(String careGiverHsaIdentity) {
        this.careGiverHsaIdentity = careGiverHsaIdentity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getMunicipalityCode() {
        return municipalityCode;
    }

    public void setMunicipalityCode(String municipalityCode) {
        this.municipalityCode = municipalityCode;
    }

    public String getMunicipalitySectionCode() {
        return municipalitySectionCode;
    }

    public void setMunicipalitySectionCode(String municipalitySectionCode) {
        this.municipalitySectionCode = municipalitySectionCode;
    }

    public String getMunicipalitySectionName() {
        return municipalitySectionName;
    }

    public void setMunicipalitySectionName(String municipalitySectionName) {
        this.municipalitySectionName = municipalitySectionName;
    }

    public GeoCoordDto getGeographicalCoordinatesRt90() {
        return geographicalCoordinatesRt90;
    }

    public void setGeographicalCoordinatesRt90(GeoCoordDto geographicalCoordinatesRt90) {
        this.geographicalCoordinatesRt90 = geographicalCoordinatesRt90;
    }

    public List<String> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<String> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public List<String> getManagements() {
        return managements;
    }

    public void setManagements(List<String> managements) {
        this.managements = managements;
    }

    public List<String> getBusinessClassificationCodes() {
        return businessClassificationCodes;
    }

    public void setBusinessClassificationCodes(List<String> businessClassificationCodes) {
        this.businessClassificationCodes = businessClassificationCodes;
    }

    public List<String> getCareTypes() {
        return careTypes;
    }

    public void setCareTypes(List<String> careTypes) {
        this.careTypes = careTypes;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean isArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }
}

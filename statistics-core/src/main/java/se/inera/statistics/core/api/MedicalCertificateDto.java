/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.core.api;


public class MedicalCertificateDto {
	private int viewRange;
	private Integer age;
	private Integer workDisability;
	private String careUnit;
	private String careGiver;
	private String issuer;
	private Integer issuerAge;
	private String issuerGender;

	private Boolean female;
	private Boolean diagnose;
	
	private String icd10;
	
	private String startDate;
	private String endDate;
	
	private Boolean basedOnExamination;
	private Boolean basedOnTelephoneContact;
		
	public MedicalCertificateDto() {	
	}

	public int getViewRange() {
		return viewRange;
	}

	public void setViewRange(int viewRange) {
		this.viewRange = viewRange;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getWorkDisability() {
		return workDisability;
	}

	public void setWorkDisability(Integer workDisability) {
		this.workDisability = workDisability;
	}

	public String getCareUnit() {
		return careUnit;
	}

	public void setCareUnit(String careUnit) {
		this.careUnit = careUnit;
	}

	public Boolean getFemale() {
		return female;
	}

	public void setFemale(Boolean female) {
		this.female = female;
	}

	public Boolean getDiagnose() {
		return diagnose;
	}

	public void setDiagnose(Boolean diagnose) {
		this.diagnose = diagnose;
	}

	public String getIcd10() {
		return icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}

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

	public Boolean getBasedOnExamination() {
		return basedOnExamination;
	}

	public void setBasedOnExamination(Boolean basedOnExamination) {
		this.basedOnExamination = basedOnExamination;
	}

	public Boolean getBasedOnTelephoneContact() {
		return basedOnTelephoneContact;
	}

	public void setBasedOnTelephoneContact(Boolean basedOnTelephoneContact) {
		this.basedOnTelephoneContact = basedOnTelephoneContact;
	}
	
	public void setCareGiver(String careGiver) {
		this.careGiver = careGiver;
	}
	
	public String getCareGiver() {
		return careGiver;
	}
	
	public String getIssuer() {
		return issuer;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public Integer getIssuerAge() {
		return issuerAge;
	}
	
	public void setIssuerAge(Integer issuerAge) {
		this.issuerAge = issuerAge;
	}
	
	public String getIssuerGender() {
		return issuerGender;
	}
	
	public void setIssuerGender(String issuerGender) {
		this.issuerGender = issuerGender;
	}
}

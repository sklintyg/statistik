/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class MedicalCertificateEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(nullable=false)
	private Long personId;
	
	@Column(nullable=false)
	private Long diagnosisId;
	
	@Column(nullable=false)
	private Long careUnitId;

	@Column(nullable=false)
	private Long startDate;
	
	@Column(nullable=false)
	private Long endDate;
	
	@Column(nullable=false)
	private Boolean basedOnExamination;
	
	@Column(nullable=false)
	private Boolean basedOnTelephoneContact;

	@Column(nullable=false)
	private String careGiverId;

	@Column(nullable=false)
	private String issuerId;

	@Column(nullable=false)
	private Integer issuerAge;

	@Column(nullable=false)
	private String issuerGender;

	@Column(nullable=false)
	private Integer workDisability;
	
	MedicalCertificateEntity() {}
	
	public static MedicalCertificateEntity newEntity(final Long startDate, final Long endDate) {
		final MedicalCertificateEntity ent = new MedicalCertificateEntity();

		ent.setStartDate(startDate);
		ent.setEndDate(endDate);
		
		return ent;
	}

    public static MedicalCertificateEntity newEntity(final DateEntity startDate, final DateEntity endDate) {
        return MedicalCertificateEntity.newEntity(startDate.getId(), endDate.getId());
    }

	public Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}
	
	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	
	public Boolean getBasedOnExamination() {
		return basedOnExamination;
	}

	public void setBasedOnExamination(Boolean basedOnExamination) {
		this.basedOnExamination = basedOnExamination;
	}

	public boolean getBasedOnTelephoneContact() {
		return basedOnTelephoneContact;
	}

	public void setBasedOnTelephoneContact(Boolean basedOnTelephoneContact) {
		this.basedOnTelephoneContact = basedOnTelephoneContact;
	}

	public Long getDiagnosisId() {
		return diagnosisId;
	}

	public void setDiagnosisId(Long diagnosisId) {
		this.diagnosisId = diagnosisId;
	}
	
	public Long getCareUnitId() {
		return careUnitId;
	}

	public void setCareUnitId(Long careUnitId) {
		this.careUnitId = careUnitId;
	}

	public String getCareGiverId() {
		return careGiverId;
	}

	public void setCareGiverId(String careGiverId) {
		this.careGiverId = careGiverId;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public void setIssuerAge(Integer issuerAge) {
		this.issuerAge = issuerAge;
	}
	
	public Integer getIssuerAge() {
		return issuerAge;
	}
	
	public void setIssuerGender(String issuerGender) {
		this.issuerGender = issuerGender;
	}
	
	public String getIssuerGender() {
		return issuerGender;
	}

	public void setWorkDisability(Integer workDisability) {
		this.workDisability = workDisability;
	}
	
	public Integer getWorkDisability() {
		return workDisability;
	}
}

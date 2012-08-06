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

//	@Column
//	private boolean actualSicknessProcess;
	
//	@Column
//	private boolean examinationResults;
	
//	@Column
//	private boolean limitedActivity;

	@Column(nullable=false)
	private Long startDate;
	
	@Column(nullable=false)
	private Long endDate;
	
	@Column(nullable=false)
	private Boolean basedOnExamination;
	
	@Column(nullable=false)
	private Boolean basedOnTelephoneContact;
	
//	@Column
//	private boolean basedOnJournal;
	
//	@Column
//	private boolean basedOnOther;
	
	MedicalCertificateEntity() {}
	
	public static MedicalCertificateEntity newEntity(final Long startDate, final Long endDate) {
		final MedicalCertificateEntity ent = new MedicalCertificateEntity();

		ent.setStartDate(startDate);
		ent.setEndDate(endDate);
		
		return ent;
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

	
//	public boolean isActualSicknessProcess() {
//		return actualSicknessProcess;
//	}
//
//	public void setActualSicknessProcess(boolean actualSicknessProcess) {
//		this.actualSicknessProcess = actualSicknessProcess;
//	}
//
//	public boolean isExaminationResults() {
//		return examinationResults;
//	}
//
//	public void setExaminationResults(boolean examinationResults) {
//		this.examinationResults = examinationResults;
//	}
//
//	public boolean isLimitedActivity() {
//		return limitedActivity;
//	}
//
//	public void setLimitedActivity(boolean limitedActivity) {
//		this.limitedActivity = limitedActivity;
//	}

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

//	public boolean isBasedOnJournal() {
//		return basedOnJournal;
//	}
//
//	public void setBasedOnJournal(boolean basedOnJournal) {
//		this.basedOnJournal = basedOnJournal;
//	}
//
//	public boolean isBasedOnOther() {
//		return basedOnOther;
//	}
//
//	public void setBasedOnOther(boolean basedOnOther) {
//		this.basedOnOther = basedOnOther;
//	}

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
}

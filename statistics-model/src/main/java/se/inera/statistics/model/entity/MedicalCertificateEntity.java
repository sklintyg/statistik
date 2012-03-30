package se.inera.statistics.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table
public class MedicalCertificateEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private int age;
	
	@Column(nullable=false)
	private boolean female;
	
	@Column
	private boolean diagnose;
	
	@Column
	private String icd10;
	
	@Column
	private boolean actualSicknessProcess;
	
	@Column
	private boolean examinationResults;
	
	@Column
	private boolean limitedActivity;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column
	private CertificateOption businessRehab;
	
	@Column
	private CertificateOption abilityToReturnToPresentWork;
	
	@Column
	private WorkCapability workCapability;
	
	@Column
	private boolean basedOnExamination;
	
	@Column
	private boolean basedOnTelephoneContact;
	
	@Column
	private boolean basedOnJournal;
	
	@Column
	private boolean basedOnOther;
	
	MedicalCertificateEntity() {}
	
	public static MedicalCertificateEntity newEntity(final int age, final boolean female, final Date startDate, final Date endDate) {
		final MedicalCertificateEntity ent = new MedicalCertificateEntity();
		ent.setAge(age);
		ent.setFemale(female);
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

	public int getAge() {
		return age;
	}

	void setAge(int age) {
		this.age = age;
	}

	public boolean isFemale() {
		return female;
	}

	void setFemale(boolean female) {
		this.female = female;
	}

	public Date getEndDate() {
		return endDate;
	}

	void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean getDiagnose() {
		return diagnose;
	}

	public void setDiagnose(boolean diagnose) {
		this.diagnose = diagnose;
	}

	public Date getStartDate() {
		return startDate;
	}

	void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public CertificateOption getBusinessRehab() {
		return businessRehab;
	}

	public void setBusinessRehab(CertificateOption businessRehab) {
		this.businessRehab = businessRehab;
	}

	public CertificateOption getAbilityToReturnToPresentWork() {
		return abilityToReturnToPresentWork;
	}

	public void setAbilityToReturnToPresentWork(
			CertificateOption abilityToReturnToPresentWork) {
		this.abilityToReturnToPresentWork = abilityToReturnToPresentWork;
	}

	public WorkCapability getWorkCapability() {
		return workCapability;
	}

	public void setWorkCapability(WorkCapability workCapability) {
		this.workCapability = workCapability;
	}
	
	public boolean isActualSicknessProcess() {
		return actualSicknessProcess;
	}

	public void setActualSicknessProcess(boolean actualSicknessProcess) {
		this.actualSicknessProcess = actualSicknessProcess;
	}

	public boolean isExaminationResults() {
		return examinationResults;
	}

	public void setExaminationResults(boolean examinationResults) {
		this.examinationResults = examinationResults;
	}

	public boolean isLimitedActivity() {
		return limitedActivity;
	}

	public void setLimitedActivity(boolean limitedActivity) {
		this.limitedActivity = limitedActivity;
	}

	public String getIcd10() {
		return icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}

	public boolean isBasedOnExamination() {
		return basedOnExamination;
	}

	public void setBasedOnExamination(boolean basedOnExamination) {
		this.basedOnExamination = basedOnExamination;
	}

	public boolean isBasedOnTelephoneContact() {
		return basedOnTelephoneContact;
	}

	public void setBasedOnTelephoneContact(boolean basedOnTelephoneContact) {
		this.basedOnTelephoneContact = basedOnTelephoneContact;
	}

	public boolean isBasedOnJournal() {
		return basedOnJournal;
	}

	public void setBasedOnJournal(boolean basedOnJournal) {
		this.basedOnJournal = basedOnJournal;
	}

	public boolean isBasedOnOther() {
		return basedOnOther;
	}

	public void setBasedOnOther(boolean basedOnOther) {
		this.basedOnOther = basedOnOther;
	}
}

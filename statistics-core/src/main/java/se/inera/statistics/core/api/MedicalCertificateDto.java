package se.inera.statistics.core.api;


public class MedicalCertificateDto {
	private int viewRange;
	private Integer age;
	private Integer workDisability;
	private String careUnit;

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

	public Integer getworkDisability() {
		return workDisability;
	}

	public void setworkDisability(Integer workDisability) {
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
}

package se.inera.statistics.core.api;


public class MedicalCertificate {

	private int age;
	private boolean female;
	
	private String startDate;
	private String endDate;
	
	private String icdDiagnose;
	private boolean diagnose;
	
	private boolean actualSicknessProcess;
	private boolean examinationResults;
	private boolean limitedActivity;
	
	private int businessRehab;
	private int abilityToReturnToPresentWork;
	
	private int workCapability;
	private int basedOn;
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public boolean isFemale() {
		return female;
	}
	
	public void setFemale(boolean female) {
		this.female = female;
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
	
	public boolean getDiagnose() {
		return diagnose;
	}
	
	public void setDiagnose(boolean diagnose) {
		this.diagnose = diagnose;
	}
	
	public int getBusinessRehab() {
		return businessRehab;
	}
	
	public void setBusinessRehab(int businessRehab) {
		this.businessRehab = businessRehab;
	}
	
	public int getAbilityToReturnToPresentWork() {
		return abilityToReturnToPresentWork;
	}
	
	public void setAbilityToReturnToPresentWork(int abilityToReturnToPresentWork) {
		this.abilityToReturnToPresentWork = abilityToReturnToPresentWork;
	}
	
	public int getWorkCapability() {
		return workCapability;
	}
	
	public void setWorkCapability(int workCapability) {
		this.workCapability = workCapability;
	}
	
	public int getBasedOn() {
		return basedOn;
	}
	
	public void setBasedOn(int basedOn) {
		this.basedOn = basedOn;
	}

	public String getIcdDiagnose() {
		return icdDiagnose;
	}

	public void setIcdDiagnose(String icdDiagnose) {
		this.icdDiagnose = icdDiagnose;
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
}

package se.inera.statistics.model.entity;

public enum MedicalCertificateBase {

	BASED_ON_EXAMINATION(0),
	BASED_ON_TELEPHONE_CONTACT(1),
	BASED_ON_PATIENT_JOURNAL(2),
	BASED_ON_OTHER(3);
	
	private final int code;
	
	private MedicalCertificateBase(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
}

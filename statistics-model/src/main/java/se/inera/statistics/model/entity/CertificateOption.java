package se.inera.statistics.model.entity;

public enum CertificateOption {
	YES(0),
	YES_PARTLY(1),
	NO(2),
	NOT_APPLICABLE(3);
	
	private int code;
	
	private CertificateOption(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
}

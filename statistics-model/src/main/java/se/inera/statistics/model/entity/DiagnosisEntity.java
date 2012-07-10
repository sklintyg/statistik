package se.inera.statistics.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"icd10"}))
public class DiagnosisEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String icd10;
	
	@Column(nullable=false)
	private boolean diagnose;
	
	@Column(nullable=false)
	private WorkCapability workCapability;
	//TODO: May be default to full working capability?
	
	DiagnosisEntity(){}
	
	public static DiagnosisEntity newEntity(final String Icd10, final boolean diagnose, final WorkCapability workCapability) {
		final DiagnosisEntity diagnosis = new DiagnosisEntity();
		diagnosis.setIcd10(Icd10);
		diagnosis.setDiagnose(diagnose);
		diagnosis.setWorkCapability(workCapability);
		
		return diagnosis;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIcd10() {
		return icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}

	public boolean isDiagnose() {
		return diagnose;
	}

	public void setDiagnose(boolean diagnose) {
		this.diagnose = diagnose;
	}
	
	public WorkCapability getWorkCapability() {
		return workCapability;
	}

	public void setWorkCapability(WorkCapability workCapability) {
		this.workCapability = workCapability;
	}
}
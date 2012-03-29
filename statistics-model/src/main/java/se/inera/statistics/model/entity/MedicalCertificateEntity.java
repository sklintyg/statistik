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
	
	public static final int NO_WORKING_CAPABILITY = 0;
	public static final int ONE_QUARTER_WORKING_CAPABILITY = 1;
	public static final int HALF_WORKING_CAPABILITY = 2;
	public static final int THREE_QUARTER_WORKING_CAPABILITY = 3;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private int age;
	
	@Column(nullable=false)
	private boolean female;
	
	@Column
	private String diagnose;
	
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
	private MedicalCertificateBase basedOn;
	
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

	public String getDiagnose() {
		return diagnose;
	}

	public void setDiagnose(String diagnose) {
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

	public MedicalCertificateBase getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(MedicalCertificateBase basedOn) {
		this.basedOn = basedOn;
	}
}

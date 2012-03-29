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
	private int gender;
	
	@Column(nullable=false)
	private String diagnose;
	
	@Column(nullable=false)
	private int workCapability;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column
	private boolean doctorExamined;
	
	@Column
	private boolean selfExamined;
	
	@Column
	private boolean personalContact;
	
	@Column
	private boolean phoneContact;
	
	@Column
	private boolean businessRehab;
	
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

	public int getGender() {
		return gender;
	}

	void setGender(int gender) {
		this.gender = gender;
	}

	public String getDiagnose() {
		return diagnose;
	}

	void setDiagnos(String diagnose) {
		this.diagnose = diagnose;
	}

	public int getWorkCapability() {
		return workCapability;
	}

	void setWorkCapability(int workCapability) {
		this.workCapability = workCapability;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isDoctorExamined() {
		return doctorExamined;
	}

	public void setDoctorExamined(boolean doctorExamined) {
		this.doctorExamined = doctorExamined;
	}

	public boolean isSelfExamined() {
		return selfExamined;
	}

	public void setSelfExamined(boolean selfExamined) {
		this.selfExamined = selfExamined;
	}

	public boolean isPersonalContact() {
		return personalContact;
	}

	public void setPersonalContact(boolean personalContact) {
		this.personalContact = personalContact;
	}

	public boolean isPhoneContact() {
		return phoneContact;
	}

	public void setPhoneContact(boolean phoneContact) {
		this.phoneContact = phoneContact;
	}

	public boolean isBusinessRehab() {
		return businessRehab;
	}

	public void setBusinessRehab(boolean businessRehab) {
		this.businessRehab = businessRehab;
	}

	public void setDiagnose(String diagnose) {
		this.diagnose = diagnose;
	}
}

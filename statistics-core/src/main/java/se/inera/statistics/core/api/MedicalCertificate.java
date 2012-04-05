package se.inera.statistics.core.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.model.entity.MedicalCertificateEntity;


public class MedicalCertificate {

	private int age;
	private boolean female;
	private boolean diagnose;
	
	private String icd10;
	
	private String startDate;
	private String endDate;
	
	private boolean basedOnExamination;
	private boolean basedOnTelephoneContact;
	
	public static List<MedicalCertificate> newFromEntities(final List<MedicalCertificateEntity> ents) {
		final List<MedicalCertificate> dtos = new ArrayList<MedicalCertificate>();
		for (final MedicalCertificateEntity ent : ents) {
			dtos.add(newFromEntity(ent));
		}
		
		return dtos;
	}
	
	public static MedicalCertificate newFromEntity(final MedicalCertificateEntity ent) {
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		final MedicalCertificate dto = new MedicalCertificate();
		dto.setAge(ent.getAge());
		
		dto.setFemale(ent.isFemale());
		dto.setIcd10(ent.getIcd10());
		
		dto.setBasedOnExamination(ent.isBasedOnExamination());
		dto.setBasedOnTelephoneContact(ent.isBasedOnTelephoneContact());
		
		dto.setStartDate(sdf.format(ent.getStartDate()));
		dto.setEndDate(sdf.format(ent.getEndDate()));
		
		return dto;
	}
	
	public MedicalCertificate() {
	
	}
	
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
	
	public boolean isDiagnose() {
		return diagnose;
	}
	
	public void setDiagnose(boolean diagnose) {
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
}

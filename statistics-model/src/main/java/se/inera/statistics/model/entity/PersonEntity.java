package se.inera.statistics.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"age", "gender"}))
public class PersonEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(nullable=false)
	private int age;
	
	@Column(nullable=false)
	private String gender;
	//TODO: make it enum may be 
	
//	@OneToMany(mappedBy="person", cascade=CascadeType.PERSIST)
//	private Collection<MedicalCertificateEntity> certificates = new ArrayList<MedicalCertificateEntity>(); 
	
	PersonEntity(){
	}
	
	public static PersonEntity newEntity(final int age, final String gender) {
		final PersonEntity person = new PersonEntity();
		person.setAge(age);
		person.setGender(gender);
		
		return person;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setFemale(boolean female) {
		if (female){
			this.gender = "Female";
		}else{
			this.gender = "Male";
		}	
	}
	public boolean isFemale() {
		return gender == "Female";
	}

	public Long getId() {
		return id;
	}

//	public Collection<MedicalCertificateEntity> getCertificates() {
//		return certificates;
//	}
//
//	public void setCertificates(Collection<MedicalCertificateEntity> certificates) {
//		this.certificates = certificates;
//	}

	public void setId(Long id) {
		this.id = id;
	}
}

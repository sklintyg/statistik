package se.inera.statistics.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name"}))
public class CareUnitEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String name;

	public CareUnitEntity() {}
	
	public static CareUnitEntity newEntity(final String name) {
		CareUnitEntity careUnitEntity = new CareUnitEntity();
		careUnitEntity.setName(name);
		
		return careUnitEntity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

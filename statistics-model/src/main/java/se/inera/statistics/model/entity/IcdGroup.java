package se.inera.statistics.model.entity;

public class IcdGroup {

	private String icd10RangeStart;
	
	private String icd10RangeEnd;
	
	private String chapter;
	
	private String Description;
	
	IcdGroup(){
	}

	public String getIcd10RangeStart() {
		return icd10RangeStart;
	}

	public void setIcd10RangeStart(String icd10RangeStart) {
		this.icd10RangeStart = icd10RangeStart;
	}

	public String getIcd10RangeEnd() {
		return icd10RangeEnd;
	}

	public void setIcd10RangeEnd(String icd10RangeEnd) {
		this.icd10RangeEnd = icd10RangeEnd;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
}

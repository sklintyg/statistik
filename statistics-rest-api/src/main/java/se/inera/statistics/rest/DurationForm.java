package se.inera.statistics.rest;

public class DurationForm {

	private String from;
	String to;
	String disability;
	String group;
	
	public DurationForm() {		
	}
	
	public String getFromDate() {
		return from;
	}
	
	public void setFromDate(String from) {
		this.from = from;
	}
	
	public String getToDate() {
		return to;
	}
	
	public void setToDate(String to) {
		this.to = to;
	}
	
	public String getDisability() {
		return disability;
	}
	
	public void setDisability(String disability) {
		this.disability = disability;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	
}

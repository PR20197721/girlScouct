package org.girlscouts.vtk.models;

import java.io.Serializable;

public class ActivitySearch implements Serializable {

	private String find, region;
	private int month, year;
	private java.util.List<String> programLevel, categories;

	public String getFind() {
		return find;
	}

	public void setFind(String find) {
		this.find = find;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public java.util.List<String> getProgramLevel() {
		return programLevel;
	}

	public void setProgramLevel(java.util.List<String> programLevel) {
		this.programLevel = programLevel;
	}

	public java.util.List<String> getCategories() {
		return categories;
	}

	public void setCategories(java.util.List<String> categories) {
		this.categories = categories;
	}

}

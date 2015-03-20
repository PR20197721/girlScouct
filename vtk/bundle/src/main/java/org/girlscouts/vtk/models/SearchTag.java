package org.girlscouts.vtk.models;

import java.io.Serializable;

public class SearchTag implements Serializable {

	private java.util.Map<String, String> categories, levels, region;
	private String searchKeyword;

	public java.util.Map<String, String> getRegion() {
		return region;
	}

	public void setRegion(java.util.Map<String, String> region) {
		this.region = region;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	public java.util.Map<String, String> getCategories() {
		return categories;
	}

	public void setCategories(java.util.Map<String, String> categories) {
		this.categories = categories;
	}

	public java.util.Map<String, String> getLevels() {
		return levels;
	}

	public void setLevels(java.util.Map<String, String> levels) {
		this.levels = levels;
	}

}

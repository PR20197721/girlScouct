package org.girlscouts.vtk.models;

public class Document {
	
	private String title;
	private String path;
	
	public Document(String title, String path){
		this.title = title;
		this.path = path;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getPath() {
		return path;
	}
	
}

package org.girlscouts.vtk.models;

public class bean_resource {

	
	private int itemCount;
	private String path, title, nodeUri, category, categoryDisplay;

	
	
	public void incrementCount(){itemCount++;}
	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNodeUri() {
		return nodeUri;
	}

	public void setNodeUri(String nodeUri) {
		this.nodeUri = nodeUri;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategoryDisplay() {
		return categoryDisplay;
	}
	public void setCategoryDisplay(String categoryDisplay) {
		this.categoryDisplay = categoryDisplay;
	}
	
	
	
}

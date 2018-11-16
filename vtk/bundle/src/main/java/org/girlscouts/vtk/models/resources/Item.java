package org.girlscouts.vtk.models.resources;

public class Item {
	public String type;
	public String uri;
	public String title;
	public String id;
	
	public Item(String title, String type, String uri, String id) {
		if (uri != null && uri.startsWith("/") && "link".equals(type)) {
			uri += ".html";
		}

		this.title = title;
		this.type = type;
		this.uri = uri;
		this.id = id;
	}

	public Item(String title, String type, String uri) {
		this(title, type, uri, "");
	}
}

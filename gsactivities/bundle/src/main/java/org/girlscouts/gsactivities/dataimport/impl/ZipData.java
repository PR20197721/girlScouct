package org.girlscouts.gsactivities.dataimport.impl;

import java.util.List;
import com.google.gson.JsonObject;

public class ZipData {
	private String zipName;
	private List<JsonObject> contents;

	public ZipData(String zip, List<JsonObject> list) {
		this.zipName = zip;
		this.contents = list;
	}
	public String getZipName() {
		return zipName;
	}
	public List<JsonObject> getContents() {
		return contents;
	}
}

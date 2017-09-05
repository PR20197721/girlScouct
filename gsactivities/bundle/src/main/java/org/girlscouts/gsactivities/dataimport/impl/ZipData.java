package org.girlscouts.gsactivities.dataimport.impl;

import java.util.List;
import org.apache.sling.commons.json.JSONObject;

public class ZipData {
	
	private String zipName;
	private List<JSONObject> contents;
	
	public ZipData(String zip, List<JSONObject> list){
		this.zipName = zip;
		this.contents = list;
	}

	public String getZipName() {
		return zipName;
	}

	public List<JSONObject> getContents() {
		return contents;
	}


}

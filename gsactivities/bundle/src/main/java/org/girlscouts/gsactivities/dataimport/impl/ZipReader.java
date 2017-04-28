package org.girlscouts.gsactivities.dataimport.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.girlscouts.web.exception.GirlScoutsException;

public class ZipReader implements Callable<List<JSONObject>>{
	
	FTPSClient ftp;
	String zipName;
	
	public ZipReader(FTPSClient ftp, String zip){
		this.ftp = ftp;
		this.zipName = zip;
	}

	public synchronized List<JSONObject> call() throws Exception {
		InputStream input = null;
		
		ArrayList<JSONObject> results = new ArrayList<JSONObject>();
		synchronized(this){
			input = ftp.retrieveFileStream(zipName);
		
		if(input == null){
			return null;
		} else{
			try {
				ZipInputStream inputStream = new ZipInputStream(input);
				ZipEntry entry = inputStream.getNextEntry();
				while (entry != null) {
					String fileName = entry.getName();
					if(fileName.startsWith("__macosx")) continue;
					//read instructions.json
					if(fileName.endsWith("instructions.json")){
						String jsonString = IOUtils.toString(inputStream, "UTF-8").trim();
						JSONArray jsonArray = new JSONArray(jsonString);
						for (int i = 0; i < jsonArray.length(); i++) {
							
								final JSONObject jsonObject = jsonArray.getJSONObject(i);
								results.add(jsonObject);
								
							
						}
						
						//TODO: if we only need to read one instruction.json", break here
						break;
					}
					entry = inputStream.getNextEntry();
				}
				inputStream.close();
			} catch (Exception e) {
				throw new GirlScoutsException(e, "Error reading file:"+zipName);
			} finally {
				if (!ftp.completePendingCommand()) {
					throw new GirlScoutsException(null,	"FTP CompletePendingCommand returns false." + " File " + zipName + " transfer failed.");
				}
				
				input.close();
				
				
				
			}
		}

		return results;
		}
	}
	
	

}

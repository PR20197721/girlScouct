package org.girlscouts.vtk.models;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.commons.RangeIterator;

public class DocumentCategory {

	private List<Document> allDocs;
	private String name;
	
	public DocumentCategory(String name, RangeIterator<Resource> iterator) throws RepositoryException{
		this.allDocs = new LinkedList<Document>();
		this.name = name;
		
		while(iterator.hasNext()){
			Resource temp = iterator.next();
			//return only documents
			if(temp.getName().equals("metadata")){
			ValueMap properties = temp.adaptTo(ValueMap.class);
			String docTitle = properties.get("dc:title",String.class);
			if (docTitle==null || docTitle.isEmpty()) docTitle =  properties.get("pdf:Title",String.class);
			if (docTitle==null || docTitle.isEmpty()) docTitle =  properties.get("jcr:title",String.class);

			temp = temp.getParent().getParent();
			String docPath = temp.getPath();
			if (docTitle==null || docTitle.isEmpty()) docTitle = temp.getName();
			
			Document tempDoc = new Document(docTitle, docPath);
			this.allDocs.add(tempDoc);
			}
		}
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public Document getNextDocument() {
		if(this.allDocs.isEmpty()){
			return null;
		} else{
			return this.allDocs.remove(0);
		}
	}
	
}

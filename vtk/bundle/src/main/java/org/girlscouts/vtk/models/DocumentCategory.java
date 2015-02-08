package org.girlscouts.vtk.models;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;

import com.day.cq.commons.RangeIterator;

public class DocumentCategory {

	private List<Document> allDocs;
	private String name;
	
	public DocumentCategory(String name, RangeIterator<Resource> iterator) throws RepositoryException{
		this.allDocs = new LinkedList<Document>();
		this.name = name;
		
		while(iterator.hasNext()){
			Resource temp = iterator.next();
			temp = temp.getParent();
			temp = temp.getParent();
			
			String docPath = temp.getPath();
			String docTitle = temp.getName();
			
			Document tempDoc = new Document(docTitle, docPath);
			this.allDocs.add(tempDoc);
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

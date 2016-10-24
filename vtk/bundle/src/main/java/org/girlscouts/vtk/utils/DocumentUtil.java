package org.girlscouts.vtk.utils;



import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.vtk.models.DocumentCategory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.JcrTagManagerFactory;
import com.day.cq.tagging.TagManager;
 

public class DocumentUtil {
	
	
	
	public static final String DOCUMENT_DIRECTORY = "/content/dam/%s/documents";
	public static final String TAGS_DIRECTORY = "/etc/tags/%s/forms_documents";
	public static final String TAG_ID_PREFIX_TEMPLATE = "%s:forms_documents/";
	

	private TagManager tagManager;
	private Resource docDirectory;
	private Resource tagsDirectory;
	private String tagIdPrefix;
	private List<TagHolder> allTags;
	private int tagIndex = 0;
	
	
	public DocumentUtil(ResourceResolver resolver, JcrTagManagerFactory tagManagerFactory, String councilName) throws PathNotFoundException, RepositoryException{	
		//Setup basic access tools
		
		this.tagManager = tagManagerFactory.getTagManager(resolver.adaptTo(Session.class));
		
		
		String tagsPath = String.format(TAGS_DIRECTORY, councilName);
		this.tagsDirectory = resolver.getResource(tagsPath);
		
		this.tagIdPrefix = String.format(TAG_ID_PREFIX_TEMPLATE, councilName);
		
		
	
		
		//Get all the tags
		this.allTags = new LinkedList<TagHolder>();
		if(tagsDirectory!=null){
			Iterator<Resource> tagIterator = this.tagsDirectory.getChildren().iterator();
			while(tagIterator.hasNext()){
				Resource tagResource = tagIterator.next();
				Node tempNode = tagResource.adaptTo(Node.class);
				this.allTags.add(new TagHolder(tempNode.getProperty("jcr:title").getString(), this.tagIdPrefix + tempNode.getName()));
			}
		}
		
		
		
	}
	
	
	public DocumentCategory getNextCategory() throws RepositoryException{
		if(this.allTags.size() > this.tagIndex ){
			TagHolder nextTag = this.allTags.remove(0);
			String currentTagId = nextTag.getTagId();
			String currentTagTitle = nextTag.getTagTitle();
			
			RangeIterator<Resource> tempIterator = this.tagManager.find(currentTagId);
			
			return new DocumentCategory(currentTagTitle, tempIterator);
		}else{
			return null;
		}
		
	}
	
	
	private class TagHolder{
		
		private String tagTitle;
		private String tagId;
		public TagHolder(String tagTitle, String tagId){
			this.tagTitle = tagTitle;
			this.tagId = tagId;
		}
		
		public String getTagTitle(){
			return this.tagTitle;
		}
		public String getTagId(){
			return this.tagId;
		}
	}
	
	
}

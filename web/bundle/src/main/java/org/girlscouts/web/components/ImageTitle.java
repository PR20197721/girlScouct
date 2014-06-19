package org.girlscouts.web.components;

import com.day.cq.wcm.foundation.Image;

import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;

import com.day.cq.commons.ImageResource;

import javax.jcr.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import com.day.text.Text;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import java.io.PrintWriter;


public class ImageTitle extends Image {

	public ImageTitle(Resource resource){
		super(resource);
	}
		
	public String getTitle(){
		  String title = get(getItemName("jcr:title"));
		return title;

	}
	protected Map<String, String> getImageTagAttributes()
	{
		Map attributes = new HashMap();
		if (get(getItemName("htmlWidth")).length() > 0) {
			attributes.put("width", get(getItemName("htmlWidth")));
		}
		if (get(getItemName("htmlHeight")).length() > 0) {
			attributes.put("height", get(getItemName("htmlHeight")));
		}
		String src = getSrc();
		if (src != null) {
			String q = getQuery();
			if (q == null) {
				q = "";
			}
			attributes.put("src", Text.escape(src, '%', true) + q);
		}
		attributes.put("alt", getAlt());
		String title = getTitle();
		if(title!=null && !title.isEmpty()){
			attributes.put("title", title);
		}

		if (this.attrs != null) {
			attributes.putAll(this.attrs);
		}
		return attributes;
	}

	
}

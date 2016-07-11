package org.girlscouts.web.councilrollout.impl;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.SEOSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

@Component
@Service(value = SEOSetter.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.seosetter", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts SEO Setter service", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })

public class SEOSetterImpl implements SEOSetter {
	private static Logger LOG = LoggerFactory.getLogger(SEOSetterImpl.class);

	/**
	 * Checks if an SEO Title property has been set on a page. If yes, return. Else, set the property to fit the default format
	 * 
	 * @param p The page whose SEO Title property will be set
	 * @return true if SEO Title was set, else false
	 */
	public String setSEO(Session session, Page p, String councilName) {
		String output = "";
		Node n = p.adaptTo(Node.class);
		try {
			Node jcrContent = n.getNode("jcr:content");
			if(jcrContent.hasProperty("seoTitle")){
				output = output + "SEO ALREADY SET FOR " + p.getPath() + "<br>";
			}else{
				String seo = "";
				if(p.getDepth() > 4){
					try{
						Page parent = p.getAbsoluteParent(3);
						String parentName = parent.getTitle() + " | ";
						seo = parentName + p.getTitle() + " | " + councilName; 
					}catch(Exception e){
						e.printStackTrace();
						return "Error 1 on " + p.getPath() + "<br>";
					}
				} else if(p.getDepth() == 4){
					try{
						seo = p.getTitle() + " | " + councilName;
					}catch(Exception e){
						e.printStackTrace();
						return "Error 5 on " + p.getPath() + "<br>";
					}
				} else if(p.getDepth() == 3){
					try{
						seo = councilName + " | " + p.getAbsoluteParent(1).getName().toUpperCase();
					}catch(Exception e){
						e.printStackTrace();
						return "Error 2 on " + p.getPath() + "<br>";
					}
				}
				
				jcrContent.setProperty("seoTitle", seo);
				output = output + p.getPath() + " SET TO " + seo + "<br>";
				Iterator<Page> pIterator = p.listChildren();
				while(pIterator.hasNext()){
					output = output + setSEO(session, pIterator.next(), councilName);
				}
			}
		} catch (PathNotFoundException e) {
			e.printStackTrace();
			return "Error 3 on " + p.getPath() + "<br>";
		} catch (RepositoryException e) {
			e.printStackTrace();
			return "Error 4 on " + p.getPath() + "<br>";
		}
		
		return output;
	}

}

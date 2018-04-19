package org.girlscouts.common.taglib.base;

import javax.jcr.Node;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

/*
 * Adds functionality for page attributes usually provided by Adobe's "global.jsp"
 */
@SuppressWarnings("serial")
public class BaseTag extends TagSupport{
	
	public SlingHttpServletRequest getRequest() {
		return (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
	}

	public SlingHttpServletResponse getResponse() {
		return (SlingHttpServletResponse) pageContext.getAttribute("slingResponse");
	}
	
	public Resource getResource() {
		return (Resource) pageContext.getAttribute("resource");
	}

	public ResourceResolver getResourceResolver() {
		return (ResourceResolver) pageContext.getAttribute("resourceResolver");
	}

	public Node getCurrentNode() {
		return (Node) pageContext.getAttribute("currentNode");
	}
	
	public ValueMap getProperties(){
		return (ValueMap) pageContext.getAttribute("properties");
	}
	
	public ValueMap getPageProperties() {
		return (ValueMap) pageContext.getAttribute("pageProperties");
	}
	
	public PageManager getPageManager() {
		return (PageManager) pageContext.getAttribute("pageManager");
	}
	
	public Component getComponent() {
		return (Component)  pageContext.getAttribute("component");
	}
	
	public Designer getDesigner() {
		return (Designer) pageContext.getAttribute("designer");
	}
	
	public Design getCurrentDesign() {
		return (Design) pageContext.getAttribute("currentDesign");
	}
	
	public Style getCurrentStyle() {
		return (Style) pageContext.getAttribute("currentStyle");
	}
	
	public boolean isGSUSA() {
		return false;
	}
	
}
package org.girlscouts.vtk.impl.servlets;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingServlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import java.rmi.ServerException;
import org.apache.commons.net.util.Base64;
import org.apache.sling.api.resource.ResourceResolverFactory;
import com.day.cq.commons.jcr.JcrUtil;
/*
//@Component(immediate = true, metatype = true)
//@Service(javax.servlet.Filter.class)
@SlingServlet(selectors = "vtk", resourceTypes="sling/servlet/default")

@Properties({
    @Property(name="service.pid", value="org.girlscouts.vtk.impl.servlets.Auth",propertyPrivate=false),
    @Property(name="service.description",value="Authentication Filter", propertyPrivate=false),
    @Property(name="service.vendor",value="Zensar Tech", propertyPrivate=false),
    @Property(name="pattern",value="vtk", propertyPrivate=false)//,  
    
   // @Property(name = "filter.scope", value = "request")
    //@Property(name = "sling.filter.scope", value = "request"),
    //@Property(name = "service.ranking", intValue = 100001)
})
*/


@Component(immediate = true, metatype = true, label = "Filter Service")
@Service(value = javax.servlet.Filter.class)

@Properties({ @Property(name = "filter.scope", label = "scope", value = "REQUEST"),
        @Property(name = "filter.order", label = "order", value = "1")
})
public class AlexAuth implements javax.servlet.Filter{

    
    public void destroy() {
        

    }

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		System.err.println("tata filter");
		arg2.doFilter(arg0, arg1);
	}
	
	//@Activate
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
		System.err.println("tata filter init..");
		
	}
}
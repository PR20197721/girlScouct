package org.girlscouts.vtk.impl.filters;




import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
  
    label = "Girl Scouts VTK Cache Filter",
    description = "Filter iml caching page", 
    metatype = true
)
@Service
@Properties({ 
    @Property(name = "sling.filter.scope", value = "REQUEST", propertyPrivate = true),
    @Property(name = "service.ranking", intValue = -1000, propertyPrivate = true)
})
public class VtkCaching implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory.getLogger(VtkCaching.class);
    

    public void destroy() {
        // Nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        SlingHttpServletRequest req = (SlingHttpServletRequest)request;
        SlingHttpServletResponse res = (SlingHttpServletResponse)response;
        
        String uri = req.getRequestURI();
        if (uri.startsWith("/content/girlscouts-vtk/en/myvtk/") ) {
        	
	         // The following URL are two r/w and read only versions of the same content for resource, year plan, meeting/activity
	         // /content/girlscouts-vtk/en/[targetPage]
	         // /content/girlscouts-vtk/en/myvtk/${councilCode}/[targetPage]
	        	
	         HttpSession httpSession = req.getSession();
	         if( httpSession==null ){
	        	res.setStatus(HttpServletResponse.SC_FORBIDDEN);
	         }
        
	     	 User user = ((org.girlscouts.vtk.models.User) session
	    			.getAttribute(org.girlscouts.vtk.models.User.class
	    					.getName()));
	    	 user.setSid(session.getId());
	    	 Troop troop = (Troop) session.getValue("VTK_troop");
        	
	            
	         res.sendRedirect("/content/girlscouts-vtk/en/vtk.resource.html");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void init(FilterConfig config) throws ServletException {
    }
    
    @Activate
	@Modified
	private void updateConfig(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
	
	}
}
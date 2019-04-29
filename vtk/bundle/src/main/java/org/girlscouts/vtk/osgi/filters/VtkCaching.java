package org.girlscouts.vtk.osgi.filters;


import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Dictionary;
import java.util.StringTokenizer;

@Component(metatype = true, immediate = true)
@Properties({ 
	@Property(name = "sling.filter.scope", value = "REQUEST", propertyPrivate = true),
	@Property(name = "service.ranking", intValue = -1000, propertyPrivate = true),
	@Property(name="label", value="Girl Scouts VTK Cache Filter"),
	@Property(name="description", value="Girl Scouts VTK Image Caching Page")
})
@Service
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
        if (false){//uri.startsWith("/content/girlscouts-vtk/en/myvtk/") ) {
        	
	         // The following URL are two r/w and read only versions of the same content for resource, year plan, meeting/activity
	         // /content/girlscouts-vtk/en/[targetPage]
	         // /content/girlscouts-vtk/en/myvtk/${councilCode}/${troopCode || '0'}/[targetPage]
	        	
	         HttpSession httpSession = req.getSession();
	         if( httpSession==null ){
	        	res.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        	return;
	         }
        
	     	 User user = VtkUtil.getUser(httpSession);
	    	 Troop troop = VtkUtil.getTroop(httpSession);
        	 if( user==null || troop==null ){
        		 res.setStatus(HttpServletResponse.SC_FORBIDDEN);
 	        	 return;
        	 }
        	 
        	 
         	String str =uri.substring(uri.indexOf("/myvtk/")+7);
        	StringTokenizer t = new StringTokenizer( str,"/");
        	String cid= t.nextToken();
        	String tid= t.nextToken();
        	String landingUrl = t.nextToken();
        	 if( !isValidUrl(user, troop, cid, tid) ){
        		 res.setStatus(HttpServletResponse.SC_FORBIDDEN);
  	        	 return;
        	 }
        	 
        	 /*
     		 PrintWriter out = response.getWriter();
	         out.println("User: "+ user.getApiConfig().getUser().getName() +" : "+ user.getApiConfig().getUser().getSfUserId() +" Troop: "+ troop.getSfTroopId());
	         */
	         res.sendRedirect("/content/girlscouts-vtk/en/"+ landingUrl);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isValidUrl(User user, Troop troop, String cid, String tid) {
    	/*
    	String str =uri.substring(uri.indexOf("/myvtk/")+7);
    	StringTokenizer t = new StringTokenizer( str,"/");
    	String cid= t.nextToken();
    	String tid= t.nextToken();
    	*/
    			
    	if( cid.trim().toLowerCase().equals(troop.getSfCouncil().trim().toLowerCase()) && 
    			( tid.equals("0") || tid.trim().toLowerCase().equals(troop.getSfTroopId().trim().toLowerCase()) ) ){
    		return true;
    	}
    return false;	
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

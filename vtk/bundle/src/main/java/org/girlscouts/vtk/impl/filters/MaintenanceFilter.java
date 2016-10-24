package org.girlscouts.vtk.impl.filters;

import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

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
    enabled = false, 
    label = "Girl Scouts VTK Maintenance Filter",
    description = "Filter that redirects the user to the maintenance page", 
    metatype = true
)
@Service
@Properties({ 
    @Property(name = "sling.filter.scope", value = "REQUEST", propertyPrivate = true),
    @Property(name = "service.ranking", intValue = -1000, propertyPrivate = true),
    @Property(name = "enabled", boolValue = false, label = "Enabled")
})
public class MaintenanceFilter implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceFilter.class);
    private boolean enabled;

    public void destroy() {
        // Nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        SlingHttpServletRequest req = (SlingHttpServletRequest)request;
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("vtk-skip-maintenance") &&
                        cookie.getValue().equals("true")) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        
        String uri = req.getRequestURI();
        if (uri.startsWith("/content/girlscouts-vtk") &&
            !uri.startsWith("/content/girlscouts-vtk/controllers/auth.sfauth.html") &&
            !uri.startsWith("/content/girlscouts-vtk/controllers/hello.hello.js") &&
            !uri.startsWith("/content/girlscouts-vtk/en/vtk.home")) {
            SlingHttpServletResponse res = (SlingHttpServletResponse) response;
            res.sendRedirect("/content/vtk-maintenance.html");
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
		enabled = (Boolean)configs.get("enabled");
	}
}
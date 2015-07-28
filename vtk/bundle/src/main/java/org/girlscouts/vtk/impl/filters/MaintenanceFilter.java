package org.girlscouts.vtk.impl.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(enabled = false)
@Service
@Properties({ 
    @Property(name = "sling.filter.scope", value = "REQUEST"),
    @Property(name = "service.ranking", intValue = -1000) 
})
/**
 * 
 * @author mike
 * This is the filter that reads the "additionalCss" properties of a node and
 * add CSS when the node is being included.
 *
 */
public class MaintenanceFilter implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory
            .getLogger(MaintenanceFilter.class);

    public void destroy() {
        // Nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        SlingHttpServletRequest req = (SlingHttpServletRequest)request;
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
        
        if (req.getRequestURI().startsWith("/content/girlscouts-vtk")) {
            SlingHttpServletResponse res = (SlingHttpServletResponse) response;
            res.sendRedirect("/content/vtk-maintenance.html");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do
    }
}
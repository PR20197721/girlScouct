package org.girlscouts.vtk.impl.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
@Properties({ @Property(name = "sling.filter.scope", value = "REQUEST"),
        @Property(name = "service.ranking", intValue = -1000) })
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
        process(request, response);
        filterChain.doFilter(request, response);
    }

    private void process(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        SlingHttpServletRequest req = (SlingHttpServletRequest) request;

    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do
    }
}
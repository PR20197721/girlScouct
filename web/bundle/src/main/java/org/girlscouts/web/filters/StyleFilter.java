package org.girlscouts.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.components.IncludeOptions;

@Component
@Service
@Properties({ @Property(name = "sling.filter.scope", value = "COMPONENT"),
        @Property(name = "service.ranking", intValue = 300) })
/**
 * 
 * @author mike
 * This is the filter that reads the "additionalCss" properties of a node and
 * add CSS when the node is being included.
 *
 */
public class StyleFilter implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory
            .getLogger(StyleFilter.class);

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

        Resource resource = req.getResource();
        if (resource == null) {
            return;
        }

        ValueMap properties = resource.adaptTo(ValueMap.class);
        if (properties == null) {
            return;
        }

        String additionalCss = properties.get("additionalCss", "");
        if (!additionalCss.isEmpty()) {
            IncludeOptions opt = IncludeOptions.getOptions(req, true);
            Set<String> css = opt.getCssClassNames();
            css.addAll(Arrays.asList(additionalCss.split(" ")));
            log.debug("Add additional CSS: " + additionalCss);
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
        // Nothing to do
    }
}
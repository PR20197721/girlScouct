package org.girlscouts.vtk.scaffolding;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false)
@Service({ Servlet.class })
@Property(name = "sling.servlet.paths", value = { "/bin/vtk-scaffolding-post" })
public class ScaffoldingPostServlet extends SlingAllMethodsServlet {
    private final Logger logger;
    public ScaffoldingPostServlet() {
        this.logger = LoggerFactory.getLogger(getClass());
    }
    
    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
    }
}
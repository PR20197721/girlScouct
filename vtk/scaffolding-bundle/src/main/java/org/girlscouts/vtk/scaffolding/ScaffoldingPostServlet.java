package org.girlscouts.vtk.scaffolding;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

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
    private static final Logger log = LoggerFactory.getLogger(ScaffoldingPostServlet.class);
    
    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        PrintWriter out = response.getWriter();
        out.println("hello world");
        Map<String, String[]> paramMap = request.getParameterMap();
        
        for (String key : paramMap.keySet()) {
        }
        request.getRequestDispatcher(request.getParameter("originalUrl")).forward(request, response);
    }
}
package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpSession;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

@SlingServlet( paths = {"/bin/checkup/vtk"} )
public class BuildInfoServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 5981389970977916595L;

	private static final Logger log = LoggerFactory
			.getLogger(BuildInfoServlet.class);


	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {

		HttpSession session = request.getSession(false);
        try{
            if (session == null || session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) == null) {
                    response.getWriter().write("forbidden"); 
                    response.setStatus(401);
                    return;
            } else {
                    StringBuilder builder = new StringBuilder();
                    
                    Properties properties = new Properties();
                    properties.load(getClass().getClassLoader().getResourceAsStream("girlscoutsbuild.properties"));
                      
                    builder.append("built from commit ").append(properties.getProperty("git.commit.id")).append("\n");
                    builder.append("version ").append(properties.getProperty("git.build.version")).append("\n");
                    builder.append("branch ").append(properties.getProperty("git.branch")).append("\n");
                    builder.append("built on ").append(properties.getProperty("git.build.time")).append("\n");
                    
                    response.getWriter().write(builder.toString()); 
            }
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

}

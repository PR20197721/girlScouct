package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "hello", extensions = "js", methods = "GET")
public class HelloServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 5981389970977916595L;
    private static final Logger log = LoggerFactory.getLogger(HelloServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            response.setContentType("text/javascript");
            PrintWriter out = response.getWriter();
            HttpSession session = request.getSession(false);
            if (session == null) {
                sayPleaseSignIn(out);
            } else {
                // TODO: Users may be lazy loaded. May refactor later.
                User user = null;
                try {
                    user = VtkUtil.getUser(session);
                } catch (Exception e) {
                    log.error("Error occurred:", e);
                }
                if (user == null && session.getAttribute("fatalError") == null) {
                    sayPleaseSignIn(out);
                } else {
                    String name = "";
                    if (user != null) {
                        name = user.getName();
                    }
                    sayHello(out, name);
                }
            }
        } catch (IOException e) {
            log.error("Cannot get PrintWriter \"out\" from response.");
        }
    }

    private void sayPleaseSignIn(PrintWriter out) {
        out.println("girlscouts.components.login.sayHello('signedout');");
    }

    private void sayHello(PrintWriter out, String name) {
        out.println("girlscouts.components.login.sayHello('signedin', '" + name + "');");
    }
}

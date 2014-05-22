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

@SlingServlet(
   resourceTypes = "sling/servlet/default",
   selectors = "hello",
   extensions = "js",
   methods = "GET"
)
public class HelloServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 5981389970977916595L;

    private static Logger log = LoggerFactory.getLogger(HelloServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();

            HttpSession session = request.getSession(false);
            if (session == null) {
                sayPleaseSignIn(out);
            } else {
                User user = (User)session.getAttribute(User.class.getName());
                if (user == null) {
                    sayPleaseSignIn(out);
                } else {
                    String name = user.getName();
                    sayHello(out, name);
                }
            }
        } catch (IOException e) {
            log.error("Cannot get PrintWriter \"out\" from response.");
        }
    }
    
    private void sayPleaseSignIn(PrintWriter out) {
        out.println("girlscouts.components.login.sayHello('loggedout');");
    }
    
    private void sayHello(PrintWriter out, String name) {
        out.println("$.cookie('girl-scout-name', '" + name + "');");
        out.println("girlscouts.components.login.sayHello('loggedin', '" + name + "');");
    }
}

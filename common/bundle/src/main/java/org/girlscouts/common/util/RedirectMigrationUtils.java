/*

 */
package org.girlscouts.common.util;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

/**
 * JCR query example
 */
@SlingServlet(paths="/bin/updateredirect")
@SuppressWarnings("deprecation")
public class RedirectMigrationUtils extends SlingAllMethodsServlet {
    private static final long serialVersionUID = -8909492203133496844L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        try {
            final String councilName= request.getParameter("councilName");

            response.setContentType("text/plain;charset=UTF-8");
            if(!StringUtils.isNotEmpty(councilName)){
                response.getWriter().write("Please insert councilName as parameter");
            }
            final String migrate = request.getParameter("migrate");
            final String jcrContent = queryJcrContent(session, councilName, migrate);
            response.getWriter().write(jcrContent);
        }
        catch (RepositoryException ex) {
            throw new ServletException(ex);
        }


    }
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    String queryJcrContent(final Session session, final String councilName, final String migrate) throws RepositoryException {

        // get query manager
        QueryManager queryManager = session.getWorkspace().getQueryManager();

        // query for all nodes with tag "JCR"
        Query query = queryManager.createQuery("/jcr:root/content/"+councilName+"//jcr:content[@redirectTarget != \"\"]", Query.XPATH);

        // iterate over results
        QueryResult result = query.execute();
        NodeIterator nodes = result.getNodes();
        StringBuilder output = new StringBuilder();
        while (nodes.hasNext()) {
            Node node = nodes.nextNode();
            Property prop = node.getProperty("redirectTarget");
            if(!node.hasProperty("cq:redirectTarget") && BooleanUtils.toBoolean(migrate)){
                output.append("updated ");
                node.setProperty("cq:redirectTarget", prop.getValue());
            }
            output.append("path=" + node.getPath() + "\n");
        }
        session.save();
        return output.toString();
    }

}
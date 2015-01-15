/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package apps.wcm.core.components.bulkeditor;

import java.io.IOException;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.ServletException;

import org.apache.jackrabbit.commons.query.GQL;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.day.cq.commons.TidyJSONWriter;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Servers as base for image servlets
 */
public class json extends SlingAllMethodsServlet {
    /**
     * Query clause
     */
    public static final String QUERY_PARAM = "query";

    /**
     * Common path prefix
     */
    public static final String COMMON_PATH_PREFIX_PARAM = "pathPrefix";

    /**
     * Common path prefix
     */
    public static final String PROPERTIES_PARAM = "cols";

    public static final String TIDY_PARAM = "tidy";

    /**
     * Property name replacements
     */
    private static Map<String, String> PROPERTY_NAME_REPLACEMENTS = new HashMap<String, String>();
    static {
        PROPERTY_NAME_REPLACEMENTS.put("\\.", "_DOT_");
    }

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        if ("json".equals(request.getRequestPathInfo().getExtension())) {
            StringWriter buf = new StringWriter();

            String queryString = request.getParameter(QUERY_PARAM);
            String commonPathPrefix = request.getParameter(COMMON_PATH_PREFIX_PARAM);

            Session session = request.getResourceResolver().adaptTo(
                    Session.class);
            try {
                RowIterator hits;
                if (commonPathPrefix != null && queryString != null) {
                    hits = GQL.execute(queryString, session, commonPathPrefix);
                } else if (queryString != null) {
                    hits = GQL.execute(queryString, session);
                } else {
                    return;
                }

                long nbrOfResults = hits.getSize();

                TidyJSONWriter writer = new TidyJSONWriter(buf);
                writer.setTidy("true".equals(request.getParameter(TIDY_PARAM)));
                writer.object();
                writer.key("hits");
                writer.array();

                String tmp = request.getParameter(PROPERTIES_PARAM);
                String[] properties = (tmp != null) ? tmp.split(",") : null;
                while (hits.hasNext()) {
                    Row hit = hits.nextRow();
                    Node node = (Node) session.getItem(hit.getValue(JcrConstants.JCR_PATH).getString());
                    if (node != null) {
                        writer.object();
                        writer.key(JcrConstants.JCR_PATH).value(hit.getValue(JcrConstants.JCR_PATH).getString());
                        if (properties != null) {
                            for (String property : properties) {
                                if (node.hasProperty(property)) {
                                    Property prop = node.getProperty(property);
                                    writer.key(encodeString(property));
                                    if (prop.getType() != PropertyType.BINARY) {
                                        if (prop.getDefinition().isMultiple()) {
                                            writer.array();
                                            for (Value v : prop.getValues()) {
                                                writer.value(v.getString());
                                            }
                                            writer.endArray();
                                        } else {
                                            writer.value(prop.getString());
                                        }
                                    } else {
                                        writer.value("BINARY");
                                    }
                                }
                            }
                        }
                        writer.endObject();
                    }
                }

                writer.endArray();
                writer.key("results").value(nbrOfResults);
                writer.endObject();
            } catch (Exception e) {
                throw new ServletException(e);
            }

            // send string buffer
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(buf.getBuffer().toString());
        }
    }

    public static String encodeString(String s) {
        for (String key : PROPERTY_NAME_REPLACEMENTS.keySet()) {
            s = s.replaceAll(key, PROPERTY_NAME_REPLACEMENTS.get(key));
        }
        return s;
    }
}
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

package apps.girlscouts.wcm.components.designer;

import java.io.IOException;
import java.io.Writer;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.servlet.ServletException;

import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.commons.RequestHelper;
import com.day.text.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders the computed css styles
 */
public class designer_css extends SlingSafeMethodsServlet {
    private static final String CSS_FOLDER = "/etc/designs/girlscouts/css";
    private static final String BASE_CSS = CSS_FOLDER + "/base.css";
    private static final String HOME_CSS = CSS_FOLDER + "/home.css";

    /**
     * default logger
     */
    private static final Logger log = LoggerFactory.getLogger(designer_css.class);

    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/css");
        resp.setCharacterEncoding("utf-8");

        // get parent resource
        String path = req.getResource().getPath();
        Resource res = req.getResourceResolver().getResource(Text.getRelativeParent(path, 1));
        Design design = res == null ? null : res.adaptTo(Design.class);
        Node contentNode = design == null ? null : design.getContentResource().adaptTo(Node.class);
        if (contentNode != null) {
            if (RequestHelper.handleIfModifiedSince(req, resp, contentNode)) {
                return;
            }
        }

        Writer out = resp.getWriter();
        if (design == null) {
            out.write("/* no design at " + path + " */\n");
        } else {
            try {
                design.writeCSS(out, false);
            } catch (RepositoryException e) {
                log.error("Error while generating css for " + req.getResource().getPath(), e);
                resp.sendError(500);
            }
        }

        // Girl Scouts CSS
        ResourceResolver rr = req.getResourceResolver();
        ValueMap properties = (ValueMap)rr.resolve(path).adaptTo(ValueMap.class);
        writeCss(null, out, rr, properties);
        writeCss("home", out, rr, properties);
        writeCss("headerStyle", out, rr, properties);
        writeCss("navStyle", out, rr, properties);
        writeCss("headerFontStyle", out, rr, properties);
        writeCss("councilStyle", out, rr, properties);
    }

    private void writeCss(String property, Writer out, ResourceResolver rr, ValueMap properties) {
        String path;
        if (property == null) { // Use base.css if property is null
            path = BASE_CSS;
        } 
        else if (property == "home") {
            path = HOME_CSS;
        }
        else {
            path = properties.get(property, "");
        }

        if (path.isEmpty()) {
			log.warn("Cannot get property: " + property);
            return;
        }

        try {
            InputStream is = (InputStream)rr.resolve(path).adaptTo(InputStream.class);
            IOUtils.copy(is, out, "UTF-8");
        } catch (IOException e) {
            log.error("Error while writing css: " + path);
        }
    }
}
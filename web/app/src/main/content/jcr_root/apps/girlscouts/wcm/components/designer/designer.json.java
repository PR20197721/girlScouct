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

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.servlet.ServletException;

import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.commons.RequestHelper;
import com.day.text.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders the computed css styles
 */
public class designer_json extends SlingSafeMethodsServlet {

    /**
     * default logger
     */
    private static final Logger log = LoggerFactory.getLogger(designer_json.class);

    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
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
            out.write("{}\n");
        } else {
            out.write(design.getJSON());
        }
    }


}
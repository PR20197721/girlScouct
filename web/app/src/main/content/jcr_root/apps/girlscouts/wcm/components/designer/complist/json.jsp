<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.
  --%><%@ page import="java.io.IOException,
                 java.io.StringWriter,
                 com.day.cq.wcm.api.components.Component,
                 com.day.text.Text,
                 org.apache.sling.api.SlingHttpServletRequest,
                 org.apache.sling.api.SlingHttpServletResponse,
                 org.apache.sling.commons.json.io.JSONWriter,
                 org.slf4j.Logger,
                 org.slf4j.LoggerFactory,
                 com.day.cq.wcm.commons.GlobPattern,
                 com.day.cq.wcm.api.components.ComponentManager" %><%

%><%!

    /**
     * default log
     */
    private final static Logger log = LoggerFactory.getLogger(Component.class);

    /**
     * Generates a JSON export of the component list:
     * <xmp>
     * <xmp>
     * [
     *      {
     *      text:"Image Component",
     *      value:"/apps/components/geometrixx/image",
     *      qtip:"Geometrixx Image Component"
     *      },
     * ....
     * ]
     * </xmp>
     *
     * {@inheritDoc}
     */
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        SlingHttpServletRequest req = (SlingHttpServletRequest) request;
        SlingHttpServletResponse resp = (SlingHttpServletResponse) response;
        resp.setContentType("application/json");

        ComponentManager compMgr = req.getResourceResolver().adaptTo(ComponentManager.class);
        String parent = req.getParameter("parent");

        // todo: respect allowedChildren
        String[] allowedChildren = new String[]{}; //parSys.getProperties().get("allowedChildren", new String[0]);

        // don't use the response writer directly so we can handle errors
        // properly
        StringWriter buf = new StringWriter();
        try {
            JSONWriter w = new JSONWriter(buf);
            w.array();
            boolean writtenForms = false;
            for (Component c: compMgr.getComponents()) {
                if (!c.isAccessible() || !c.isEditable()) {
                    continue;
                }
                // filter out 'allowedChildren' of 'this' parent component
                if (allowedChildren.length > 0) {
                    boolean isAllowed = false;
                    for (String allowed: allowedChildren) {
                        if (GlobPattern.matches(allowed, c.getPath())) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        continue;
                    }
                }
                // filter out 'allowedParents' of the component
                if (parent != null) {
                    String[] allowedParents = c.getProperties().get("allowedParents", new String[0]);
                    boolean isAllowed = false;
                    for (String allowed : allowedParents) {
                        if (GlobPattern.matches(allowed, parent)) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        continue;
                    }
                }

                String title = c.getTitle();
                if (title == null) {
                    title = Text.getName(c.getPath());
                }
                String description = c.getDescription();
                String path = c.getPath();
                
                // Workaround for #18754
                if ( "Form".equals(c.getComponentGroup()) ) {
                    if ( writtenForms ) {
                        continue;                        
                    }
                    writtenForms = true;
                    path = "group:Form";
                    title = "Forms";
                    description = "Components for form handling.";
                }
                // End workaround
                w.object();
                w.key("value").value(path);
                w.key("text").value(title);
                if (description != null) {
                    w.key("qtip").value(description);
                }
                w.endObject();
            }
            w.endArray();
        } catch (Exception e) {
            log.error("Error while generating JSON component list", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
        // send string buffer
        resp.getWriter().print(buf.getBuffer().toString());
    }

%>

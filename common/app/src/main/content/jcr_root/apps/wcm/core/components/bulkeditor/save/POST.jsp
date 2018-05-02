<%@ page import="com.day.cq.commons.servlets.HtmlStatusResponseHelper,
                 org.apache.sling.api.servlets.HtmlResponse,
                 java.util.Enumeration,
                 org.apache.sling.api.resource.Resource,
                 javax.jcr.Node,
                 com.day.cq.commons.jcr.JcrUtil,
                 javax.jcr.Property,
                 javax.jcr.Session,
                 com.day.text.Text" %>
<%@ page session="false" %>
<%
%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%
%><sling:defineObjects/><%
    HtmlResponse htmlResponse = null;
    try {
        Session session = resourceResolver.adaptTo(Session.class);
        boolean updated = false;
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            try {
                String path = (String) names.nextElement();
                if( path.startsWith("/")) {
                    boolean isDelete = false;
                    if( path.indexOf("@Delete") != -1) {
                        isDelete = true;
                        path = path.substring(0, path.indexOf("@Delete"));
                    }

                    String value = request.getParameter(path);
                    Resource r = resourceResolver.getResource(path);

                    String propertyName = Text.getName(path);
                    if (r == null) {
                        //resource does not exist. 2 cases:
                        // - maybe it is a non existing property? property has to be created
                        // - maybe it is a new row? node has to be created first
                        String rowPath = Text.getRelativeParent(path, 1);
                        Resource rowResource = resourceResolver.getResource(rowPath);
                        if (rowResource != null) {
                            //add property to node
                            Node rowNode = rowResource.adaptTo(Node.class);
                            if(rowNode != null ) {
                                rowNode.setProperty(propertyName,value);
                                updated = true;
                            }
                        } else {
                            //create node and add property
                            String parentPath = Text.getRelativeParent(path, 2);
                            Resource parentResource = resourceResolver.getResource(parentPath);
                            if (parentResource != null) {
                                Node parentNode = parentResource.adaptTo(Node.class);
                                if (parentNode != null) {
                                    String nodeToCreateName = Text.getName(rowPath);
                                    Node rowNode = parentNode.addNode(nodeToCreateName);
                                    rowNode.setProperty(propertyName,value);
                                    updated = true;
                                }
                            }
                        }
                    } else {
                        if( isDelete ) {
                            Node n = r.adaptTo(Node.class);
                            if( n != null) {
                                n.remove();
                                updated = true;
                            }
                        } else {
                            //path should already be the property path
                            Property p = r.adaptTo(Property.class);
                            if (p != null) {
                                p.setValue(value);
                                updated = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {}
        }

        if (updated) {
            session.save();
        }

        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true,
                "Data saved");
    } catch (Exception e) {
        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false,
                "Error while saving data", e.getMessage());
    }

    htmlResponse.send(response, true);
%>

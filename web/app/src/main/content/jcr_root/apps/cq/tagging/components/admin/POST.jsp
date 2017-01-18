<%@ page session="false" %><%
%><%@ page import="
        com.day.cq.tagging.*,
        java.util.Iterator,
        org.apache.sling.api.resource.Resource,
        java.security.AccessControlException,
        java.io.IOException"
        
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq"    uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt" %><%
%><%@ taglib prefix="fn"    uri="http://java.sun.com/jsp/jstl/functions" %><%
%><%!

    TagManager tagManager;

    private long getNumber(String str, long def) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String buildID(String base, char ch) {
        if (base.endsWith(":")) {
            return base + ch;
        } else {
            return base + "/" + ch;
        }
    }
    
    private long createRandomTagsRecursive(final String base, final long start, final long end, long count, final int maxLevel, int level, final JspWriter out) throws Exception {
        // create tags on this level
        if (level == maxLevel) {
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                String id = buildID(base, ch);
                if (count >= start) {
                    //out.println(id);
                    //out.println(" " + count);
                    tagManager.createTagByTitle(id);
                    //out.println("<br/>");
                    if (count % 100 == 0) {
                        out.println(" " + count + "...<br>");
                        out.flush();
                    }
                }
                count++;
                if (count >= end) {
                    break;
                }
            }
        }
        
        if (count < end && level < maxLevel) {
            // create child tags
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                String id = buildID(base, ch);
                if (count >= start) {
                    count = createRandomTagsRecursive(id, start, end, count, maxLevel, level+1, out);
                }
                if (count >= end) {
                    return count;
                }
            }
        }
        return count;
    }

    private void doCreateRandomTags(long start, long end, final JspWriter out) throws Exception {
        long time = System.currentTimeMillis();
        String base = "DummyRandomTags:";
        Tag tag = tagManager.resolveByTitle(base);
        if (tag == null) {
            tagManager.createTag("dummyrandomtags:", "DummyRandomTags", "Tags for debugging");
        }
        long count = 0;
        if (end < start) {
            long tmp = end;
            end = start;
            start = end;
        }
        
        int level = 0;
        while (count < end) {
            count = createRandomTagsRecursive(base, start, end, count, level, 0, out);
            level++;
        }
        
        tagManager.getSession().save();
        
        time = System.currentTimeMillis() - time;
        out.println("<br/>Created " + count + " tags in " + time + " ms");
    }

%><sling:defineObjects /><%

    this.tagManager = slingRequest.getResourceResolver().adaptTo(TagManager.class);
    String createTag = slingRequest.getParameter("createTag");
    String createTagTitlePath = slingRequest.getParameter("createTagTitlePath");
    String createRandomTags = slingRequest.getParameter("createRandomTags");
    Tag tag = null;
    try {
        if (createTag != null) {
            tag = tagManager.createTag(createTag, null, null);
        } else if (createTagTitlePath != null) {
            tag = tagManager.createTagByTitle(createTagTitlePath);
        } else if (createRandomTags != null) {
            long start = getNumber(slingRequest.getParameter("start"), 0);
            long end = getNumber(slingRequest.getParameter("end"), 10);
            doCreateRandomTags(start, end, out);
            return;
        }
        response.sendRedirect(request.getContextPath() + tag.getPath() + ".html");
    } catch (AccessControlException e) {
        response.sendError(403, e.getMessage());
    } catch (InvalidTagFormatException e) {
        response.sendError(400, e.getMessage());
    }
%>

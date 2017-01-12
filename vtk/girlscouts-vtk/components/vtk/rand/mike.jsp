/*<%@page import="javax.jcr.query.*,
                javax.jcr.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
ALEX
<%
    Session session = resourceResolver.adaptTo(Session.class);
    QueryManager qm = session.getWorkspace().getQueryManager();
//String query = "SELECT excerpt(.) ,[jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [dam:Asset] WHERE ISDESCENDANTNODE([/content/dam/gssem/documents]) AND contains(*, 'membership')";


/*good
String query = "SELECT excerpt(.) FROM nt:base  WHERE ISDESCENDANTNODE([/content/dam/gssem/documents]) ";// AND contains(*, 'membership')";
Query q = qm.createQuery(query, Query.SQL);
*/

//String query="/jcr:root/content/dam/gssem/documents//*[jcr:contains(., 'membership')]/rep:excerpt(.)";


String query = "//element(*,nt:unstructured)[jcr:contains(., 'membership')]/rep:excerpt(.)";
Query q = qm.createQuery(query,  Query.XPATH);


//String query = "select * from [nt:base] WHERE ISDESCENDANTNODE([/]) AND contains(*, 'membership')";
//Query q = qm.createQuery(query, Query.JCR_SQL2);




    QueryResult result = q.execute();
    String[] columns = result.getColumnNames();
    for (String column : columns) {
        %>column: <%= column %> <br/><%
    }
    for (RowIterator it = result.getRows(); it.hasNext(); ) {
        Row r = it.nextRow();
        String path = r.getValue("jcr:path").getString();
        out.println("<font color=\"red\">"+path +"</font><br/>");
        /*
        String title = "";
        title = getValue(r, "dc:title");

        if (title.isEmpty()) {
            title = getValue(r, "jcr:content/jcr:title");
        }
        if (title.isEmpty()) {
            title = path.substring(path.lastIndexOf("/") + 1);
        }

        %><%=path%>: <%= title %><br/><%

        String description = getValue(r, "jcr:content/metadata/dc:description");
        if (!description.isEmpty()) {
            %>Description: <%= description %> <br/><%
        }
        */
        String excerpt = getValue(r, "rep:excerpt(.)");

        if (!excerpt.isEmpty()) {
            %>Excerpt: <%= excerpt %> <br/><%
        }
    
    }
%>

<%!
    public String getValue(Row r, String column) throws ItemNotFoundException, RepositoryException {
        Value val = r.getValue(column);
        if (val == null) {
            return "";
        }
        String strVal = val.getString();
        if (strVal == null) {
            return "";
        }
        return strVal.trim();
    }
%>
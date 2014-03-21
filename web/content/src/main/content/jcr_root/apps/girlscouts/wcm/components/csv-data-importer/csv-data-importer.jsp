<%@page import="javax.jcr.Node,javax.jcr.NodeIterator,
    org.girlscouts.web.dataimport.DataImporter,
    org.girlscouts.web.dataimport.DataImporterFactory,
	java.io.StringReader"
%>
<%@include file="/libs/foundation/global.jsp" %>

<%
DataImporterFactory factory = sling.getService(DataImporterFactory.class);
String csv = "\"Test Event\",\"This is a test event.\",\"2014-02-04 17:38\"\n";
csv += "\"Test Event 2\",\"This is yet another test event.\",\"2014-02-05 17:38\"";
DataImporter importer = factory.getDataImporter("csv", new StringReader(csv), resourceResolver, "/etc/data-import/girlscouts/csv/Event", "/content/girlscouts-usa/en/events");
String[] msgs = importer.doDryRun();
for (String msg : msgs) {
    %><%= msg %><br /><%
}
%>
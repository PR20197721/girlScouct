<%@page import="javax.jcr.Node,javax.jcr.NodeIterator,
    org.girlscouts.web.dataimport.DataImporter,
    org.girlscouts.web.dataimport.DataImporterFactory,
	java.io.StringReader"
%>
<%@include file="/libs/foundation/global.jsp" %>

<%
    DataImporterFactory factory = sling.getService(DataImporterFactory.class);
    String csv = "\"Test Event\",\"2014-11-12\"";
    DataImporter importer = factory.getDataImporter("csv", new StringReader(csv), resourceResolver, "/etc/data-import/girlscouts/csv/Event", "/content/girlscouts-usa/en/events");
%>
<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch"%>


<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>


<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%

// This is the path where the documents will reside

String path = "/content/dam/girlscouts-shared/en/documents";
FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);
formsDocuImpl.test();


String q = request.getParameter("q");
String[] tags = new String[]{};
if (request.getParameterValues("tags") != null) {
	tags = request.getParameterValues("tags");
} 
















%>





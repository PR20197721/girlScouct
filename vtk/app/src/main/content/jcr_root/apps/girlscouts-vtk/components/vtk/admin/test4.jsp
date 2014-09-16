<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%@include file="../admin/toolbar.jsp"%>


<form action="/content/girlscouts-vtk/en/vtk.admin.test4.html">
<input type="text" name="xx44" value=""/>
<input type="submit" value="User id"/>
</form>

<% 


try{
	//org.girlscouts.vtk.auth.models.User x= 
	String t= new org.girlscouts.vtk.auth.dao.SalesforceDAO(userDAO).getcaca3( user.getApiConfig() , request.getParameter("xx44") ) ;
	out.println(t);
}catch(Exception e){e.printStackTrace();}
%>
done 
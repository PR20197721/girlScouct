<%@ page
import="org.girlscouts.web.checker.ReplicationChecker,
com.day.cq.dam.api.Asset,
com.day.cq.wcm.api.Page,
com.day.cq.tagging.Tag,
java.util.ArrayList,
java.util.Iterator,
javax.jcr.Repository,
java.lang.Exception,
javax.jcr.RepositoryException,
javax.jcr.LoginException,
javax.jcr.Session,
javax.jcr.SimpleCredentials,
org.apache.jackrabbit.commons.JcrUtils,
org.girlscouts.web.exception.GirlScoutsException,
org.apache.sling.jcr.api.SlingRepository"%>

<%@include file="/libs/foundation/global.jsp"%>

<%
String action = request.getParameter("action");
final String contentPath = request.getParameter("path");

if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("check")) {
    String pubServer = request.getParameter("server");
    if (pubServer == null || pubServer.isEmpty()) {
        %>Server URL is empty. Abort.<%
        return;
    }

    Session authSession = (Session) resourceResolver.adaptTo(Session.class);

    if (!authSession.nodeExists(contentPath)) {   
        %>Content path does not exist. Abort.<% 
    } 
    else {
    	try{

/*     		  Repository repository = JcrUtils.getRepository(pubServer);
    		  SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
    		  Session pubSession = repository.login(creds); */
          %>Checking <%=pubServer+contentPath%>...<br><%
          ReplicationChecker checker = sling.getService(ReplicationChecker.class);
	        ArrayList<Asset> assetList = new ArrayList<Asset>(checker.checkAssets(authSession, pubServer, resourceResolver,contentPath) );
	        %><br>Absent assets (<%=assetList.size()%> in total):<br><%
	        for (Asset asset : assetList) {
          %>"<%= asset.getPath()%>"<br><%
          }
      }catch(GirlScoutsException e){
        	 %>GirlScoutsException:<%=e.getException().getMessage()%><br>
        	 Reason:<%=e.getReason()%><%
        	 e.printStackTrace();
      }catch(Exception e){
          %>Exception:<%=e.getMessage()%><%
          e.printStackTrace();
          return;
      }finally{
        	authSession.save();
      }
    }
}else{
%><cq:include script="form.jsp"/><%
}
%>
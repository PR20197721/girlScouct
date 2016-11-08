<%@ page import="org.apache.sling.commons.json.io.*,
javax.jcr.security.AccessControlManager,
javax.jcr.security.Privilege,
javax.jcr.Session" %>
<%@include file="/libs/foundation/global.jsp"%>

<% 

response.setContentType("application/json");
response.setCharacterEncoding("utf-8");

String resourcePath = request.getParameter("resource");
String action = request.getParameter("sourceaction");
String editProp = request.getParameter("property");
String status = "failure - unspecified";
if(null == action){
	status = "failure - invalid call source";
}
else if(null != resourcePath){
	try{
		//Before any changes can occur, we must confirm that the user making the request has permission to read and write the resource.
		//Otherwise, someone could spoof the request to this servlet.
		Session session = resourceResolver.adaptTo(Session.class);
		AccessControlManager acMgr = session.getAccessControlManager();
		if(acMgr.hasPrivileges(resourcePath, new Privilege[] {acMgr.privilegeFromName(Privilege.JCR_READ), acMgr.privilegeFromName(Privilege.JCR_WRITE)})){
			Resource r = resourceResolver.resolve(resourcePath);
			Node resourceNode = r.adaptTo(Node.class);
			PageManager pm = resourceResolver.adaptTo(PageManager.class);
			Page p = pm.getContainingPage(r);
			String pageToResource = resourcePath.substring(p.getPath().length());
			
			Node pageNode = p.adaptTo(Node.class);
			Node liveSyncConfigNode = pageNode.getNode("jcr:content/cq:LiveSyncConfig");
			String masterPage = liveSyncConfigNode.getProperty("cq:master").getString();

			if(action.equals("EditProp")){
				if(null != editProp){
					Resource masterResource = resourceResolver.resolve(masterPage + pageToResource);
					try{
						Node masterNode = masterResource.adaptTo(Node.class);
						Value masterValue = masterNode.getProperty(editProp).getValue();
						resourceNode.setProperty(editProp, masterValue);
						session.save();
						status = "success";
					}catch(Exception e){
						status = "failure - unable to retrieve master properties";
						e.printStackTrace();
					}
				}
			}
			
		} else{
			status = "failure - insufficient credentials";
		}
	} catch(Exception e){
		status = "failure - invalid credentials";
		e.printStackTrace();
	}
	
}else{
	status = "failure - resource not found";
}

JSONWriter writer = new JSONWriter(response.getWriter());
writer.object();
writer.key("status");
writer.value(status);
writer.endObject();
%>
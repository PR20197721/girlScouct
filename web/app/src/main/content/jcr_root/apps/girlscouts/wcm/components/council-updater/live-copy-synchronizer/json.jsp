<%@ page import="org.apache.sling.commons.json.io.*,
javax.jcr.security.AccessControlManager,
javax.jcr.security.Privilege,
javax.jcr.Session,
java.util.Date,
java.util.Calendar,
com.day.cq.commons.jcr.JcrUtil,
com.day.cq.wcm.msm.api.LiveRelationshipManager,
com.day.cq.wcm.msm.api.LiveRelationship,
com.day.cq.wcm.msm.api.RolloutManager" %>
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
			Node liveSyncConfigNode = null;
			String masterPage = null;
			Resource masterResource = null;
			try{
				pageNode.getNode("jcr:content/cq:LiveSyncConfig");
				masterPage = liveSyncConfigNode.getProperty("cq:master").getString();
				masterResource = resourceResolver.resolve(masterPage + pageToResource);
			}catch(Exception e){}

			if(action.equals("EditProp")){
				if(null != editProp){
					try{
						Node masterNode = masterResource.adaptTo(Node.class);
						Value masterValue = masterNode.getProperty(editProp).getValue();
						resourceNode.setProperty(editProp, masterValue);
						session.save();
						status = "success";
					}catch(Exception e){
						status = "failure - unable to retrieve master properties";
						outputJSON(status, response);
						e.printStackTrace();
					}
				}
			}else if(action.equals("EditBase")){
				Calendar lastModifiedNew = Calendar.getInstance();
				lastModifiedNew.setTime(new Date());
				String lastModifiedBy = session.getUserID();
				try{
					LiveRelationshipManager lrm = sling.getService(LiveRelationshipManager.class);
					LiveRelationship lr = lrm.getLiveRelationship(r,false);
					RolloutManager rm = sling.getService(RolloutManager.class);
					rm.rollout(resourceResolver, lr, true);
					session.save();
					status = "success";
				}catch(Exception e){
					status = "failure - unable to synchronize node data";
					outputJSON(status, response);
					e.printStackTrace();
				}
			}else{
				status = "failure - invalid action";
			}
			
		} else{
			status = "failure - insufficient credentials";
		}
	} catch(Exception e){
		status = "failure - invalid credentials";
		outputJSON(status, response);
		e.printStackTrace();
	}
	
}else{
	status = "failure - resource not found";
}

outputJSON(status, response);
%>

<%!
public void outputJSON(String status, HttpServletResponse response){
	try{
		JSONWriter writer = new JSONWriter(response.getWriter());
		writer.object();
		writer.key("status");
		writer.value(status);
		writer.endObject();
	}catch(Exception e){
		e.printStackTrace();
	}
}
%>
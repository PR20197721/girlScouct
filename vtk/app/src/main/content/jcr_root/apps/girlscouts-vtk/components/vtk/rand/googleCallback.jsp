<%@page import="javax.jcr.query.RowIterator, javax.jcr.query.*, javax.jcr.Session, org.girlscouts.vtk.models.Troop, org.girlscouts.vtk.auth.permission.*, org.girlscouts.vtk.utils.VtkUtil"%>
<%@include file="/libs/foundation/global.jsp"%>


<h1>VTK  Google API</h1>
<%
final org.girlscouts.vtk.ejb.SessionFactory sessionFactory = sling.getService( org.girlscouts.vtk.ejb.SessionFactory.class);
String accessToken =doAuth(request.getParameter("code"));
%>
<br/> token: <%=request.getParameter("code")%>
<br/> accessToken: <font color="red"><%=accessToken %></font>
<%
java.util.List<String[]> rptResults = rptVtkDataFromDb(  sessionFactory); 
sendRpt(accessToken, rptResults);
%>
<%!
public String doAuth(String code) {
	String accessToken="";
    try {
        code = java.net.URLDecoder.decode(code, "UTF-8");
    } catch (Exception e) {
        System.err.println("Error decoding the code. Left it as is.");
    }

    org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();

    String tokenUrl = "https://accounts.google.com/o/oauth2/auth";
tokenUrl="https://www.googleapis.com/oauth2/v3/token";
    org.apache.commons.httpclient.methods.PostMethod post = new org.apache.commons.httpclient.methods.PostMethod(tokenUrl);
/*
    post.addParameter("response_type", "code");
    post.addParameter("scope", "https://www.googleapis.com/auth/analytics");
    post.addParameter("client_id", "415198072678-176k4l71spaqjfeis5gjugbommv3bgla.apps.googleusercontent.com");
    post.addParameter("state", "test123");
    post.addParameter("redirect_uri", "http://localhost:4503/content/girlscouts-vtk/controllers/vtk.googleCallback.html");
    post.addParameter("access_type", "online");
	post.addParameter("approval_prompt", "auto");
	post.addParameter("login_hint", "email");
	post.addParameter("include_granted_scopes", "true");
	*/
	post.addParameter("code", code);
	post.addParameter("client_id", "415198072678-176k4l71spaqjfeis5gjugbommv3bgla.apps.googleusercontent.com");
	post.addParameter("client_secret", "_E5kUFdEozPjuhVMkgWnd8AA");
	post.addParameter("redirect_uri", "http://localhost:4503/content/girlscouts-vtk/controllers/vtk.googleCallback.html");
	post.addParameter("grant_type", "authorization_code");
  
	try {

    	 org.apache.commons.httpclient.Header headers[] = post.getRequestHeaders();
        for (org.apache.commons.httpclient.Header h : headers) {
            System.err.println("Headers: " + h.getName() + " : " + h.getValue());
        }
       System.err.println(":::> " + post.getQueryString());
       httpclient.executeMethod(post);

        System.err.println("doAuth: " + post.getResponseBodyAsString());
        
        
        if (post.getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK) {
          
        	org.json.JSONObject authResponse = new org.json.JSONObject(new org.json.JSONTokener(
                        new java.io.InputStreamReader(
                                post.getResponseBodyAsStream())));

              accessToken = authResponse.getString("access_token");
        }

     }catch(Exception e){e.printStackTrace();}
       
    return accessToken;
}


public void sendRpt(String code, java.util.List <String[]> rptResultsToPost) {
    
	
    org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();

    String tokenUrl ="https://www.googleapis.com/oauth2/v3/token";
   // tokenUrl ="https://www.googleapis.com/upload/analytics/v3/management/accounts/61431888/webproperties/UA-61431888-1/customDataSources/n6eRDkX_RCu1yYQ69oYnhg/uploads";
    
      // tokenUrl ="https://www.googleapis.com/upload/analytics/v3/management/accounts/61431888/webproperties/UA-61431888-1/customDataSources/LRL3ltweTK6tX5dDJY3Jrw/uploads";
    
    
    tokenUrl ="https://www.googleapis.com/upload/analytics/v3/management/accounts/61431888/webproperties/UA-2646810-36/customDataSources/4muCUenURUeHi3nwC1dL2Q/uploads";
    
    
    org.apache.commons.httpclient.methods.PostMethod post = new org.apache.commons.httpclient.methods.PostMethod(tokenUrl);
    
    post.setRequestHeader("Authorization", "Bearer " +code);
    post.setRequestHeader("Content-Type", "application/octet-stream");
    
    //post.addParameter("test", "test123, test345");
    
   // post.setRequestBody("ga:dimension2,ga:dimension3\ntest1,test2");
    //post.setRequestBody("ga:transactionId\n"+"701G0000000uQzaIAE");
    //post.setRequestBody("ga:dimension2,ga:dimension3\ntest1,test2");
    
    String dataToUpload="ga:dimension1,ga:dimension3,ga:dimension4,ga:dimension2,ga:dimension6";
    //post.setRequestBody("ga:dimension1,ga:dimension3,ga:dimension4,ga:dimension2"); 
    for(String[] record : rptResultsToPost){
    	dataToUpload +="\n"+ record[3]+","+record[0]+",fromVtkDb,"+ record[2] +","+record[1];
    }
    
    post.setRequestBody(dataToUpload);
    try {

         org.apache.commons.httpclient.Header headers[] = post.getRequestHeaders();
        for (org.apache.commons.httpclient.Header h : headers) {
            System.err.println("Headers: " + h.getName() + " : " + h.getValue());
        }
       System.err.println(":::> " + post.getQueryString());
       httpclient.executeMethod(post);

        System.err.println("doAuth: " + post.getResponseBodyAsString());
        
        
        if (post.getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK) {
          
            org.json.JSONObject authResponse = new org.json.JSONObject(new org.json.JSONTokener(
                        new java.io.InputStreamReader(
                                post.getResponseBodyAsStream())));

              
        }

     }catch(Exception e){e.printStackTrace();}
       
   
}

public java.util.List<String[]> rptVtkDataFromDb( org.girlscouts.vtk.ejb.SessionFactory sessionFactory){
	
	   java.util.List<String[]> rptList = new java.util.ArrayList<String[]>();
	   Session session = null;
       try {
           session = sessionFactory.getSession();
           

          
           String sql = "";
           sql = "select sfUserId , jcr:lastModified ,  sfTroopId, sfTroopName from nt:base where jcr:path like '"+ VtkUtil.getYearPlanBase(user, troop) +"%' and ocm_classname='org.girlscouts.vtk.models.Troop'";

           javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
           javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
           QueryResult result = q.execute();

           for (RowIterator it = result.getRows(); it.hasNext();) {
               Row r = it.nextRow();
               javax.jcr.Value excerpt = r.getValue("jcr:path");
               String path = excerpt.getString();
               try{
	               javax.jcr.Value sfUserId =  r.getValue("sfUserId");
	               javax.jcr.Value lastMondif =  r.getValue("jcr:lastModified");
	               javax.jcr.Value sfTroopId =  r.getValue("sfTroopId");
	               javax.jcr.Value sfTroopName =  r.getValue("sfTroopName");
	               
	                
	               String rpt[] =new String[4];
	               rpt[0] = sfUserId ==null ? "" :  sfUserId.getString();
	               rpt[1] = lastMondif==null ? "" : lastMondif.getString();
	               rpt[2] = sfTroopId==null ? "" : sfTroopId.getString();
	               rpt[3] = sfTroopName==null ? "" : sfTroopName.getString();
	               rptList.add(rpt);
	               
               }catch(Exception e){e.printStackTrace();}
           }
       }catch(Exception e){e.printStackTrace();
       }finally{
    	   sessionFactory.closeSession(session);
       }
       return rptList;
}
%>
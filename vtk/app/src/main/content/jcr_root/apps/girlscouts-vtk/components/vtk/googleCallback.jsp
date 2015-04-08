<h1>VTK >> Google API</h1>
<%
System.err.println("google");

//https://accounts.google.com/o/oauth2/auth?client_id=415198072678-176k4l71spaqjfeis5gjugbommv3bgla.apps.googleusercontent.com&redirect_uri=http://localhost:4503/content/girlscouts-vtk/controllers/vtk.googleCallback.html&response_type=code&scope=https://www.googleapis.com/auth/analytics.readonly
String accessToken =doAuth(request.getParameter("code"));
sendRpt(accessToken);
%>

<br/> token: <%=request.getParameter("code")%>
<br/> accessToken: <font color="red"><%=accessToken %></font>

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


public void sendRpt(String code) {
    
   
    org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();

    String tokenUrl ="https://www.googleapis.com/oauth2/v3/token";
    tokenUrl ="https://www.googleapis.com/upload/analytics/v3/management/accounts/61431888/webproperties/UA-61431888-1/customDataSources/n6eRDkX_RCu1yYQ69oYnhg/uploads";
    org.apache.commons.httpclient.methods.PostMethod post = new org.apache.commons.httpclient.methods.PostMethod(tokenUrl);
    
    post.setRequestHeader("Authorization", "Bearer " +code);
    post.setRequestHeader("Content-Type", "application/octet-stream");
    
    //post.addParameter("test", "test123, test345");
    
    post.setRequestBody("ga:dimension2,ga:dimension3\ntest1,test2");
    
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
%>
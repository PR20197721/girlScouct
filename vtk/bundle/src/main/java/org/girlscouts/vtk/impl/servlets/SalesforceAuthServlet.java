package org.girlscouts.vtk.impl.servlets;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Dictionary;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.dao.SalesforceDAOFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.impl.helpers.ConfigListener;
import org.girlscouts.vtk.impl.helpers.ConfigManager;
import org.girlscouts.vtk.salesforce.Troop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    label="Girl Scouts VTK Salesforce Authentication Servlet",
    description="Handles OAuth Authentication with Salesforce",
    metatype=true, 
    immediate=true
)
@Service
@Properties ({
    @Property(propertyPrivate=true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
    @Property(propertyPrivate=true, name = "sling.servlet.selectors", value = "sfauth"),
    @Property(propertyPrivate=true, name = "sling.servlet.extensions", value = "html"),
    @Property(propertyPrivate=true, name = "sling.servlet.methods", value = "GET")
})
public class SalesforceAuthServlet extends SlingSafeMethodsServlet implements ConfigListener {
    private static final long serialVersionUID = 8152897311719564370L;

    private static final Logger log = LoggerFactory.getLogger(SalesforceAuthServlet.class);
    private static final String ACTION = "action";
    private static final String SIGNIN = "signin";
    private static final String SIGNOUT = "signout";
    // Salesforce callback code
    private static final String CODE = "code";
    
    private String OAuthUrl;
    private String clientId;
    private String callbackUrl;
    private String targetUrl;
    
    @Reference
    private ConfigManager configManager;
    
    @Reference
    private SalesforceDAOFactory salesforceDAOFactory;
    
    @Reference
    private CouncilMapper councilMapper;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String action = request.getParameter(ACTION);
 
//if(true){ autoLogin(request, response); return; }      
 
     if (action == null) {
            salesforceCallback(request, response);
        } else if (action.equals(SIGNIN)) {
            signIn(request, response);
        } else if (action.equals(SIGNOUT)){
            signOut(request, response);
        } else {
            log.error("Unsupported action: " + action);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void updateConfig(Dictionary configs) {
        OAuthUrl = (String)configs.get("OAuthUrl");
        clientId = (String)configs.get("clientId");
        callbackUrl = (String)configs.get("callbackUrl");
        targetUrl = (String)configs.get("targetUrl");
    }
    
    @Activate
    public void init() {
        configManager.register(this);
    }
    
    private void signIn(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        ApiConfig config = (ApiConfig)session.getAttribute(ApiConfig.class.getName());
        String redirectUrl;
        if (config == null || config.getId() == null) {
            redirectUrl = OAuthUrl 
                          + "/services/oauth2/authorize?prompt=login&response_type=code&client_id=" + clientId
                          + "&redirect_uri=" + callbackUrl
                          + "&state=" + targetUrl;
        } else {
            redirectUrl = targetUrl;
        }
        
        redirect(response, redirectUrl);
    }

    private void signOut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        
    	boolean isLogoutApi=false, isLogoutWeb=false;
    	
    	HttpSession session = request.getSession();
    	ApiConfig apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class.getName());
    	if( apiConfig!=null){
    		try{ isLogoutApi = logoutApi(apiConfig, false); }catch(Exception e){e.printStackTrace();}
    		
    		
    		try{ logoutApi(apiConfig, true); }catch(Exception e){e.printStackTrace();}
    		
    		try{ isLogoutWeb = logoutWeb(apiConfig); }catch(Exception e){e.printStackTrace();}
    	}//edn if
    	
    	
    	
    	//HttpSession session = request.getSession();
        session.invalidate();
        session=null;
        
        String redirectUrl;
        try {
            String councilId = apiConfig.getTroops().get(0).getCouncilId();
            redirectUrl = councilMapper.getCouncilUrl(councilId);
        } catch (ArrayIndexOutOfBoundsException e) {
            redirectUrl = councilMapper.getCouncilUrl();
        }
       
    	apiConfig=null;

        redirectUrl= redirectUrl.contains("?") ? (redirectUrl = redirectUrl +"&isSignOutSalesForce=true") : 
        	(redirectUrl = redirectUrl +"?isSignOutSalesForce=true") ;
       
        redirect(response, redirectUrl);
    }
    
    private void salesforceCallback(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        if ((ApiConfig)session.getAttribute(ApiConfig.class.getName()) != null) {
            log.error("In Salesforce callback but the ApiConfig already exists. Quit.");
            return;
        }
        
        String code = request.getParameter(CODE);
        if (code == null) {
            log.error("In Salesforce callback but \"code\" parameter not returned. Quit.");
            return;
        }
        
        SalesforceDAO dao = salesforceDAOFactory.getInstance();
        ApiConfig config = dao.doAuth(code);
        session.setAttribute(ApiConfig.class.getName(), config);

        User user = dao.getUser(config);
        session.setAttribute(User.class.getName(), user);
        config.setUser(user);
        
        redirect(response, targetUrl);
    }
    
    private void redirect(SlingHttpServletResponse response, String redirectUrl) {
        try {
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            // TODO: What to do here? Send 500?
            log.error("Error while sending redirect: " + redirectUrl);
        }
    }
    
    
    
    
    
  //aPI logout
  	public boolean logoutApi(ApiConfig apiConfig, boolean isRefreshToken) throws Exception{
  		
  		
  		
  		
  		
  		
  		DataOutputStream wr=null;
  		boolean isSucc=false;
  		URL obj=null;
  		HttpsURLConnection con =null;
  		
  		try{
  		String url = "https://test.salesforce.com/services/oauth2/revoke"; //DYNAMIC 
  		obj = new URL(url);
  		con= (HttpsURLConnection) obj.openConnection();
  		
  		con.setRequestMethod("POST");
  		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
  		String urlParameters = "token="+ apiConfig.getAccessToken();
  		if( isRefreshToken){
  			System.err.println("Revoke refresh token "+ apiConfig.getRefreshToken()); urlParameters = "token="+ apiConfig.getRefreshToken();}
  		else
  			System.err.println("REvoke token "+ apiConfig.getAccessToken());
  		
  		
  		
  		con.setDoOutput(true);
  		wr= new DataOutputStream(con.getOutputStream());
  		wr.writeBytes(urlParameters);
  		
  		wr.flush();
  		wr.close();
   
  		int responseCode = con.getResponseCode();
  		
  		System.err.println("resp code: "+ responseCode +" : "+ con.getResponseMessage() );
  		
  		if( responseCode==200)
  			isSucc=true;
   
  		
  		}catch(Exception e){e.printStackTrace();
  		}finally{
  			try{
  				wr=null;
  				obj=null;
  				con=null;
  			}catch(Exception e){
  				e.printStackTrace();
  			}
  		}
  		
  		
  		return isSucc;
  	}
  	
  	
  	
  	//web salesforce logout
  public boolean logoutWeb(ApiConfig apiConfig) throws Exception{
  		
  		DataOutputStream wr=null;
  		boolean isSucc=false;
  		URL obj=null;
  		HttpsURLConnection con =null;
  		
  		try{
  		String url = apiConfig.getInstanceUrl() +"/secur/logout.jsp?display=touch"; //DYNAMIC 
  		
  		obj = new URL(url);
  		con= (HttpsURLConnection) obj.openConnection();
  		
  		con.setRequestMethod("POST");
  		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
  		String urlParameters = "token="+ apiConfig.getAccessToken();
  		con.setDoOutput(true);
  		wr= new DataOutputStream(con.getOutputStream());
  		wr.writeBytes(urlParameters);
  		wr.flush();
  		wr.close();
   
  		int responseCode = con.getResponseCode();
  		if( responseCode==200)
  			isSucc=true;
   
  		
  		
  		
  		
  		/*
  		
  		System.out.println("\nSending 'POST' request to URL : " + url);
  		System.out.println("Post parameters : " + urlParameters);
  		System.out.println("Response Code : " + responseCode);
   
  		BufferedReader in = new BufferedReader(
  		        new InputStreamReader(con.getInputStream()));
  		String inputLine;
  		StringBuffer response = new StringBuffer();
   
  		while ((inputLine = in.readLine()) != null) {
  			response.append(inputLine);
  		}
  		in.close();
   
  		//print result
  		System.out.println(response.toString());
  		*/
  		
  		
  		}catch(Exception e){e.printStackTrace();
  		}finally{
  			try{
  				wr=null;
  				obj=null;
  				con=null;
  			}catch(Exception e){
  				e.printStackTrace();
  			}
  		}
  		
  		
  		return isSucc;
  	}
    
  
  private void autoLogin(SlingHttpServletRequest request, SlingHttpServletResponse response) {
      HttpSession session = request.getSession();
      
      
      
      ApiConfig config = new ApiConfig();
      config.setId("test");
      config.setAccessToken("test");
      config.setInstanceUrl("etst");
      config.setUserId("caca");
      config.setUser(new User() );
      
      java.util.List <Troop > troops= new java.util.ArrayList();
      Troop troop = new Troop();
      troop.setCouncilCode(1);
      troop.setGradeLevel("1-Brownie");
      troop.setTroopId("caca");
      troop.setTroopName("test");
      
      troops.add(troop);
      config.setTroops(troops);
      
      session.setAttribute(ApiConfig.class.getName(), config);
      
      
      
      
      String redirectUrl;
      
      
      //redirect(response, "http://localhost:4503/content/girlscouts-vtk/en/vtk.html");
  }
  
    
}
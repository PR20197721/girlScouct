package org.girlscouts.vtk.impl.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleOAuth  extends AbstractAppEngineAuthorizationCodeServlet {

		  protected void doGet(HttpServletRequest request, HttpServletResponse response)
		      throws IOException {
		    // do stuff
		  }

		  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		    url.setRawPath("/oauth2callback");
		    return url.build();
		  }

		 
		  protected AuthorizationCodeFlow initializeFlow() throws IOException {
		    return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
		        new UrlFetchTransport(),
		        new JacksonFactory(),
		        new GenericUrl("https://server.example.com/token"),
		        new BasicAuthentication("s6BhdRkqt3", "7Fjfp0ZBr1KtDRbnfVdmIw"),
		        "s6BhdRkqt3",
		        "https://server.example.com/authorize").setCredentialStore(
		            StoredCredential.getDefaultDataStore(AppEngineDataStoreFactory.getDefaultInstance()))
		        .build();
		  }
		}

		public class AppEngineCallbackSample extends AbstractAppEngineAuthorizationCodeCallbackServlet {

		  
		  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
		      throws ServletException, IOException {
		    resp.sendRedirect("/");
		  }

		  @Override
		  protected void onError(
		      HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
		      throws ServletException, IOException {
		    // handle error
		  }

		  @Override
		  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		    url.setRawPath("/oauth2callback");
		    return url.build();
		  }

		  @Override
		  protected AuthorizationCodeFlow initializeFlow() throws IOException {
		    return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
		        new UrlFetchTransport(),
		        new JacksonFactory(),
		        new GenericUrl("https://server.example.com/token"),
		        new BasicAuthentication("s6BhdRkqt3", "7Fjfp0ZBr1KtDRbnfVdmIw"),
		        "s6BhdRkqt3",
		        "https://server.example.com/authorize").setCredentialStore(
		            StoredCredential.getDefaultDataStore(AppEngineDataStoreFactory.getDefaultInstance()))
		        .build();
		  }
		}
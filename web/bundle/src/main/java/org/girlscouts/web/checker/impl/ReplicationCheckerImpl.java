package org.girlscouts.web.checker.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.RepositoryException;

import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationStatus;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.girlscouts.web.exception.GirlScoutsException;
import org.girlscouts.web.checker.ReplicationChecker;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.URIException;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;


@Component
@Service(value = ReplicationChecker.class)
@Properties({

	@Property(name = "service.pid", value = "org.girlscouts.web.checker.replicationchecker", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts replication check service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })

public class ReplicationCheckerImpl implements ReplicationChecker {
	private static Logger LOG = LoggerFactory.getLogger(ReplicationCheckerImpl.class);

	@Reference
	private SlingSettingsService slingSettings;

	private String pubUrl;

	public List<Asset> checkAssets(Session authSession, String pubUrl, ResourceResolver rr, String contentPath) 
			throws GirlScoutsException{
		if(!slingSettings.getRunModes().contains("author")){
			LOG.error("REPLICATION CHECKER ERROR: need to run in author mode.");
			throw new GirlScoutsException(new Exception("Replication Checker Run Mode Error"),
					"only runnable in author mode.");
		}
		Map<String, List<String>> pubAssetsMap = new HashMap<String, List<String>>();
		List<Asset> absentAssets = new ArrayList<Asset>();
		this.pubUrl=pubUrl;

		try {
			String sql = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE(s, '"
					+ contentPath + "')";
			LOG.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2);it.hasNext();){
				Resource assetRes = it.next();
				Asset asset = assetRes.adaptTo(Asset.class);
				ReplicationStatus repStatus = assetRes.adaptTo(ReplicationStatus.class);
				if(repStatus.isActivated()){
					if(!existInPublish(pubAssetsMap, asset)){
						absentAssets.add(asset);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new GirlScoutsException(e,null);
		} 
		pubAssetsMap = null;
		//sort by path
		Collections.sort(absentAssets, new Comparator<Asset>(){
			public int compare(Asset asset1, Asset asset2) {		 
			      return asset1.getPath().compareTo(asset2.getPath());
			    }
		});
		return absentAssets;


	}

	private boolean existInPublish(Map assetMap, Asset asset){
		String path = asset.getPath();
		if(path.lastIndexOf("/")>0){
			String parentPath = asset.getPath().substring(0,path.lastIndexOf("/"));
			if(!assetMap.containsKey(parentPath)){
				try{
					assetMap.put(parentPath, getAssetsFromJson(parentPath));
				}catch(GirlScoutsException e){
					LOG.error(e.getReason());
					return false;
				}
			}
			return (assetMap.get(parentPath)!=null)&&((List)(assetMap.get(parentPath))).contains(asset.getName());

		}else{
			return false;
		}
	}
	private List<String> getAssetsFromJson(String path) throws GirlScoutsException{
		try {
			//http GET url
			String url = URIUtil.encodePath(pubUrl+path+".1.json");
			HttpClient client = new HttpClient();

			// Create a method instance.
			GetMethod method = new GetMethod(url);

			//			method.addRequestHeader("accept", "application/json");
			// Provide custom retry handler is necessary
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
					new DefaultHttpMethodRetryHandler(3, false));
			System.out.println("<=============Sending Request: GET "+url+"================>");
			try {
				// Execute the method.
				int statusCode = client.executeMethod(method);
				if (statusCode != HttpStatus.SC_OK) {
					if(statusCode==HttpStatus.SC_NOT_FOUND ){
						System.err.println("Resource NOT Found");
					}else{
						LOG.error("Method failed: " + method.getStatusLine());
					}
					return null;
				}
				else{
					// Read the response body.
					System.out.println(method.getResponseBodyAsString());
					// Deal with the response.
					JSONObject jsonObject = new JSONObject(method.getResponseBodyAsString());
					return Arrays.asList(JSONObject.getNames(jsonObject));
				}

			} catch (HttpException e) {
				System.err.println("Fatal protocol violation: " + e.getMessage());
				e.printStackTrace();
				throw new GirlScoutsException(e,"HttpExceptions thrown when calling httpClient");
			} catch (IOException e) {
				System.err.println("Fatal transport error: " + e.getMessage());
				e.printStackTrace();
				throw new GirlScoutsException(e,"IOExceptions thrown when calling httpClient");
			} catch(JSONException e){
				e.printStackTrace();
				throw new GirlScoutsException(e,"JsonExceptions thrown when reading the response");
			}

			finally {
				// Release the connection.
				method.releaseConnection();
			} 
		} catch(URIException e){
			e.printStackTrace();
			throw new GirlScoutsException(e,"URIException thrown when encode the URL: "+pubUrl+path+".1.json");
		}
		// Create an instance of HttpClient.

	}



}

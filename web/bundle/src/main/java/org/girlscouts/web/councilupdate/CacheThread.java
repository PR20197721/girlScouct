package org.girlscouts.web.councilupdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

public class CacheThread implements Runnable {
	String initialPath;
	String initialDomain;
	String initialIp;
	String initialReferrer;
	
	public CacheThread(String path, String domain, String ip, String referrer){
		initialPath = path;
		initialDomain = domain;
		initialIp = ip;
		initialReferrer=referrer;
	}
	public void run(){
		try{
			buildCache(initialPath, initialDomain, initialIp, initialReferrer);
		}catch(Exception e){
			System.out.println("Build Cache Failed");
			e.printStackTrace();
		}
		return;
	}
	
	private void buildCache(String path, String domain, String ip, String referrer) throws Exception{
		URL url = new URL(domain + path);
		HttpURLConnection.setFollowRedirects(true);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(ip), 80));
		HttpURLConnection wget = (HttpURLConnection) url.openConnection(proxy);
		wget.setRequestMethod("GET");
		wget.setRequestProperty("Host", domain);
		wget.setRequestProperty("Accept", "text/html");
		wget.setRequestProperty("Accept-Encoding", "identity");
		wget.setRequestProperty("Connection", "Keep-Alive");
		wget.connect();
		ArrayList <String> pathsToRequest = new ArrayList <String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(wget.getInputStream()));
		String response = IOUtils.toString(in);
		wget.disconnect();
		System.out.println(response);
	}
}
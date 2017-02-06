package org.girlscouts.web.councilupdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.commons.io.IOUtils;

public class CacheThread implements Runnable {
	String initialPath;
	String initialDomain;
	String initialIp;
	String initialReferer;
	TreeSet<String> visitedPages;
	
	public CacheThread(String path, String domain, String ip, String referer){
		initialPath = path;
		initialDomain = domain;
		initialIp = ip;
		initialReferer=referer;
	}
	public void run(){
		try{
			visitedPages = new TreeSet<String>();
			visitedPages.add(initialPath);
			buildCache(initialPath, initialDomain, initialIp, initialReferer);
		}catch(Exception e){
			System.err.println("Delayed Page Activator - Build Cache Failed");
		}
		return;
	}
	
	private void buildCache(String path, String domain, String ip, String referer) throws Exception{
		URL url = new URL("http://" + domain + path);
		HttpURLConnection.setFollowRedirects(true);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(ip), 80));
		HttpURLConnection wget = (HttpURLConnection) url.openConnection(proxy);
		wget.setRequestMethod("GET");
		wget.setRequestProperty("Host", domain);
		wget.setRequestProperty("Accept", "text/html");
		wget.setRequestProperty("Accept-Encoding", "identity");
		wget.setRequestProperty("Connection", "Keep-Alive");
		if(!referer.equals("")){
			wget.setRequestProperty("Referer", referer);
		}
		wget.connect();
		System.out.println("Page: " + url.toString());
		TreeSet <String> pathsToRequest = new TreeSet <String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(wget.getInputStream()));
		String response = IOUtils.toString(in);
		wget.disconnect();
		Document doc = Jsoup.parse(response, "http://" + domain);
		Elements href = doc.select("a[href]");
		Elements media = doc.select("[src]");
		Elements imports = doc.select("link[href]");
		for(Element link : href){
			String attribute = link.attr("abs:href");
			if(!attribute.equals("")){
				URL tempUrl = new URL(attribute);
				if(tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html") && !visitedPages.contains(tempUrl.getPath())){
					visitedPages.add(tempUrl.getPath());
					pathsToRequest.add(tempUrl.getPath());
				}
			}
		}
		for(Element medium : media){
			String attribute = medium.attr("abs:src");
			if(!attribute.equals("")){
				URL tempUrl = new URL(attribute);
				if(tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html") && !visitedPages.contains(tempUrl.getPath())){
					visitedPages.add(tempUrl.getPath());
					pathsToRequest.add(tempUrl.getPath());
				}
			}
		}
		for(Element importElem : imports){
			String attribute = importElem.attr("abs:href");
			if(!attribute.equals("")){
				URL tempUrl = new URL(attribute);
				if(tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html") && !visitedPages.contains(tempUrl.getPath())){
					visitedPages.add(tempUrl.getPath());
					pathsToRequest.add(tempUrl.getPath());
				}
			}
		}
		if(pathsToRequest.size() > 0){
			for(String pathToRequest : pathsToRequest){
				buildCache(pathToRequest, domain, ip, "http://" + domain + path);
			}
		}
		return;
	}
}
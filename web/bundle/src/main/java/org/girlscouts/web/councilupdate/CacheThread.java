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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.commons.io.IOUtils;

public class CacheThread implements Runnable {
	private String initialPath;
	private String initialDomain;
	private String initialIp;
	private String initialReferer;
	private TreeSet<String> visitedPages;
	private ArrayList<String> statusList;
	private String dispTitle;
	private int crawlDepth;
	
	public CacheThread(String path, String domain, String ip, String referer, ArrayList<String> statusList,
			String dispTitle, int depth) {
		this.initialPath = path;
		this.initialDomain = domain;
		this.initialIp = ip;
		this.initialReferer = referer;
		this.statusList = statusList;
		this.dispTitle = dispTitle;
		this.crawlDepth = depth;
	}
	public void run(){
		visitedPages = new TreeSet<String>();
		visitedPages.add(initialPath);
		buildCache(initialPath, initialDomain, initialIp, initialReferer);
		return;
	}
	
	private void buildCache(String path, String domain, String ip, String referer) {
		try{
			String url = "http://" + domain + path;
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(ip), 80));
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection(proxy);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Host", domain);
			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("Accept-Encoding", "identity");
			conn.setRequestProperty("Connection", "Keep-Alive");
			if(!referer.equals("")){
				conn.setRequestProperty("Referer", referer);
			}
			conn.connect();
			boolean redirect = false;
			// normally, 3xx is redirect
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER) {
					redirect = true;
				}
			}
			if (redirect) {
				url = conn.getHeaderField("Location");
				statusList.add("redirected to " + url);
				if (url.indexOf(domain) != -1) {
					String cookies = conn.getHeaderField("Set-Cookie");
					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setRequestProperty("Cookie", cookies);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Host", domain);
					conn.setRequestProperty("Accept", "text/html");
					conn.setRequestProperty("Accept-Encoding", "identity");
					conn.setRequestProperty("Connection", "Keep-Alive");
				} else {
					statusList.add("will not request " + url);
				}
			}

			statusList.add(dispTitle + " - connection established with " + url + " with referer " + referer
					+ ". Status Code " + conn.getResponseCode());
			TreeSet<String> pathsToRequest = new TreeSet<String>();
			String response = "";
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				response = IOUtils.toString(in);
			} catch (Exception e) {
				conn.disconnect();
				statusList.add(dispTitle + " - could not parse content at " + path + " with referer " + referer
						+ ". This page may have a 500 error");
				System.err.println("GS Page Activator - Build Cache Failed on " + path + " with ip " + ip
						+ ", - Referer: " + referer);
				return;
			}
			conn.disconnect();
			Document doc = Jsoup.parse(response, "http://" + domain);
			Elements href = doc.select("a[href]");
			Elements media = doc.select("[src]");
			Elements imports = doc.select("link[href]");
			for (Element link : href) {
				String attribute = link.attr("abs:href");
				if (!attribute.equals("")) {
					URL tempUrl = new URL(attribute);
					if (tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html")
							&& !visitedPages.contains(tempUrl.getPath()) && isLessThanMaxDepth(tempUrl.getPath())) {
						visitedPages.add(tempUrl.getPath());
						pathsToRequest.add(tempUrl.getPath());
					}
				}
			}
			for (Element medium : media) {
				String attribute = medium.attr("abs:src");
				if (!attribute.equals("")) {
					URL tempUrl = new URL(attribute);
					if (tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html")
							&& !visitedPages.contains(tempUrl.getPath()) && isLessThanMaxDepth(tempUrl.getPath())) {
						visitedPages.add(tempUrl.getPath());
						pathsToRequest.add(tempUrl.getPath());
					}
				}
			}
			for (Element importElem : imports) {
				String attribute = importElem.attr("abs:href");
				if (!attribute.equals("")) {
					URL tempUrl = new URL(attribute);
					if (tempUrl.getHost().equals(domain) && tempUrl.getPath().endsWith(".html")
							&& !visitedPages.contains(tempUrl.getPath()) && isLessThanMaxDepth(tempUrl.getPath())) {
						visitedPages.add(tempUrl.getPath());
						pathsToRequest.add(tempUrl.getPath());
					}
				}
			}
			if (pathsToRequest.size() > 0) {
				for (String pathToRequest : pathsToRequest) {
					buildCache(pathToRequest, domain, ip, "http://" + domain + path);
				}
			}
		}catch(Exception e){
			System.err.println("GS Page Activator - Build Cache Failed on " + path + " with ip " + ip + ", - Referer: " + referer);
			statusList.add(dispTitle + " - Either unable to establish connection or unable to interpret response from " + domain + path + " with referer " + referer);
			return;
		}
	}

	boolean isLessThanMaxDepth(String path) {
		if (crawlDepth == -1) {
			return true;
		}
		int enIndex = path.indexOf("/en/");
		if (enIndex != -1) {
			String[] relativePathArr = path.substring(enIndex + 1).split("/");
			if (relativePathArr.length > crawlDepth) {
				return false;
			}
		}
		return true;
	}
}
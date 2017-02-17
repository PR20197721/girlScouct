package org.girlscouts.web.councilupdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.jcr.Node;

public interface PageActivator{
	void run();
	String getConfig(String key);
	HashMap<String,TreeSet<String>> getToBuild();
	HashMap<String, TreeSet<String>> getBuiltCouncils();
	HashMap<String, TreeSet<String>> getCurrentBatch();
	long getLastBatchTime();
	TreeSet<String> getUnmapped();
	ArrayList<Node> getReportNodes();
}
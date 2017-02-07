package org.girlscouts.web.councilupdate;

import java.util.HashMap;
import java.util.TreeSet;

public interface PageActivator{
	public HashMap<String,TreeSet<String>> getToBuild();
	public HashMap<String,TreeSet<String>> getBuiltCouncils();
	public HashMap<String,TreeSet<String>> getCurrentBatch();
	public String getConfig(String key);
	public long getLastBatchTime();
	public TreeSet<String> getUnmapped();
}
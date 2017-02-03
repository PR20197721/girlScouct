package org.girlscouts.web.councilupdate;

import java.util.TreeSet;

public interface PageActivator{
	public TreeSet<String> getToBuild();
	public TreeSet<String> getBuiltCouncils();
	public TreeSet<String> getCurrentBatch();
	public String getConfig(String key);
}
package org.girlscouts.web.councilupdate;

import java.util.TreeSet;

public interface DelayedPageActivator{
	void run();
	String getConfig(String key);
	TreeSet<String> getToBuild();
	TreeSet<String> getBuiltCouncils();
	TreeSet<String> getCurrentBatch();
}
package org.girlscouts.web.councilupdate;

import java.util.TreeSet;

public interface DelayedPageActivator extends PageActivator{
	void run();
	String getConfig(String key);
	TreeSet<String> getToBuild();
	TreeSet<String> getBuiltCouncils();
	TreeSet<String> getCurrentBatch();
}
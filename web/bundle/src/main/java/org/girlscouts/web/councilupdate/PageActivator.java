package org.girlscouts.web.councilupdate;

import javax.jcr.Session;

public interface PageActivator{
	void run();
	void process(String path, Session session);
}
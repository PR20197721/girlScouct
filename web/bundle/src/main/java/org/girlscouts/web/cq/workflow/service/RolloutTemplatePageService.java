package org.girlscouts.web.cq.workflow.service;

public interface RolloutTemplatePageService {
	
	public static ThreadLocal<String> blockReferenceUpdateAction = new ThreadLocal<String>();//GSWP-2235
	public void rollout(String path);

}

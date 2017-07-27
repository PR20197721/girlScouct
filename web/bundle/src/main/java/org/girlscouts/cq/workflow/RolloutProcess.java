package org.girlscouts.cq.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RangeIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.girlscouts.web.councilrollout.GirlScoutsRolloutReporter;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.girlscouts.web.councilupdate.PageActivator;

@Component
@Service
public class RolloutProcess implements WorkflowProcess {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(RolloutProcess.class);
    
    @Reference
    RolloutManager rolloutManager;

    @Reference
    private Replicator replicator;
    
    @Reference
    private LiveRelationshipManager relationManager;
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private GirlScoutsNotificationAction girlscoutsNotificationAction;
	
	@Reference
	private GirlScoutsRolloutReporter girlscoutsRolloutReporter;
	
	@Reference
	private PageActivator pa;


    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
		Date runtime = new Date();
		MetaDataMap mdm = item.getWorkflowData().getMetaDataMap();
		Set<String> councils = getCouncils(mdm);
		if (councils != null && councils.size() > 0) {
			try {
				Session session = workflowSession.getSession();
				ResourceResolver resourceResolver = resourceResolverFactory
						.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
				String srcPath = item.getWorkflowData().getPayload().toString();
				Resource srcRes = resourceResolver.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						String templatePath = "";
						Boolean useTemplate = false, dontSend = false, activate = true, crawl = false, delay = false;
						try {
							useTemplate = ((Value) mdm.get("useTemplate")).getBoolean();
						} catch (Exception e) {
						}
						try {
							templatePath = ((Value) mdm.get("template")).getString();
						} catch (Exception e) {
						}
						try {
							crawl = ((Value) mdm.get("crawl")).getBoolean();
						} catch (Exception e) {
						}
						try {
							dontSend = ((Value) mdm.get("dontsend")).getBoolean();
						} catch (Exception e) {
						}
						try {
							activate = !((Value) mdm.get("dontActivate")).getBoolean();
						} catch (Exception e) {
						}
						try {
							delay = ((Value) mdm.get("delayActivation")).getBoolean();
						} catch (Exception e) {
						}
						if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
							log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
							return;
						} else {
							RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
							Set<String> pagesToActivate = new HashSet<String>();
							while (relationIterator.hasNext()) {
								LiveRelationship relation = (LiveRelationship) relationIterator.next();
								String targetPath = relation.getTargetPath();
								Resource targetResource = resourceResolver.resolve(targetPath);
								if (!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
									int councilNameIndex = targetPath.indexOf("/", targetPath.indexOf("/") + 1);
									String councilPath = targetResource.getPath().substring(0, councilNameIndex);
									if (councils.contains(councilPath)) {
										Boolean breakInheritance = false;
										try {
											ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
											breakInheritance = contentProps.get("breakInheritance", false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										if (!breakInheritance) {
											rolloutManager.rollout(resourceResolver, relation, false);
											session.save();
											if (targetPath.endsWith("/jcr:content")) {
												targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
											}
											pagesToActivate.add(targetPath);
										}
									}
								}
							}
							if (activate) {
								appendPagesForActivation(session, resourceResolver, pagesToActivate);
							}
						}
						if (activate && !delay) {
							if (crawl) {
								gsPagesNode.setProperty("type", "ipa-c");
								session.save();
							} else {
								gsPagesNode.setProperty("type", "ipa-nc");
								session.save();
							}
							try {
								pa.run();
								gsPagesNode.setProperty("type", "dpa");
								session.save();
							} catch (Exception e) {
								log.error("Rollout Process - Immediate Activation Process Failed");
								e.printStackTrace();
							}
						}
						try {
							// girlscoutsRolloutReporter.execute(reportSubject,
							// messageLog,
							// resourceResolver);
							log.error("Report sent to Girlscouts Team");
						} catch (Exception e) {
							log.error("Failed to submit report");
						}
					} else {
						log.error("Resource is not a page. Quit. " + srcPath);
						return;
					}
				} else {
					log.error("Not a live copy source page. Quit. " + srcPath);
					return;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
    }

	private void appendPagesForActivation(Session session, ResourceResolver resourceResolver,
			Set<String> pagesToActivate) throws ItemExistsException, PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException, ValueFormatException,
			AccessDeniedException, ReferentialIntegrityException, InvalidItemStateException, NoSuchNodeTypeException {
		// If necessary, create the folder where the
		// temp
		// page nodes will be stored
		// This has to be thread safe
		Resource gsPagesRes = resourceResolver.resolve("/etc/gs-delayed-activations");
		Node gsPagesNode = null;
		if (gsPagesRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Resource etcRes = resourceResolver.resolve("/etc");
			Node etcNode = etcRes.adaptTo(Node.class);
			gsPagesNode = etcNode.addNode("gs-delayed-activations");
		} else {
			gsPagesNode = gsPagesRes.adaptTo(Node.class);
		}
		if (gsPagesNode.hasProperty("pages")) {
			Value[] propValues = gsPagesNode.getProperty("pages").getValues();
			for (Value value : propValues) {
				pagesToActivate.add(value.getString());
			}
		}
		Set<String> sortedPages = new TreeSet<String>();
		sortedPages.addAll(pagesToActivate);
		gsPagesNode.setProperty("pages", sortedPages.toArray(new String[sortedPages.size()]));
		session.save();
	}
    
	private Set<String> getCouncils(MetaDataMap mdm) {
		Set<String> councils = null;
		try {
			Value[] values = (Value[]) mdm.get("councils");
			if (values != null && values.length > 0) {
				councils = new HashSet<String>();
				for (Value value : values) {
					councils.add(value.getString().trim());
				}
			}
		} catch (Exception e) {
			try {
				Value singleValue = (Value) mdm.get("councils");
				if (singleValue != null) {
					councils.add(singleValue.getString().trim());
				}
			} catch (Exception e1) {
				System.err.println("Rollout Could Not Run - No Councils Selected");
				e1.printStackTrace();
			}
		}
		return councils;
	}

	public String getTemplate(String templatePath, ResourceResolver resourceResolver, Boolean subject) {
    	try{
    		Resource templateResource = resourceResolver.resolve(templatePath);
    		Resource dataResource = templateResource.getChild("jcr:content/data");
    		ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
    		String ret = "";
    		if(subject){
    			Resource contentResource = templateResource.getChild("jcr:content");
    			ValueMap contentProps = ResourceUtil.getValueMap(contentResource);
    			ret = contentProps.get("jcr:title","GSUSA Rollout Notification");
    		}else{
	    		String message = templateProps.get("content","");
	    		String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + 
	    				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + 
	    				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
	    				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
	    				"<title>Girl Scouts</title></head>";
	    		ret = head + "<body>" + message + "</body></html>";
    		}
    		return ret;
    	}catch(Exception e){
    		log.error("No valid template found");
    		e.printStackTrace();
    		return "";
    	}
    }
    
}

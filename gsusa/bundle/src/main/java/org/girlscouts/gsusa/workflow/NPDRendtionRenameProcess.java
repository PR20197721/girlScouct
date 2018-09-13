package org.girlscouts.gsusa.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component
@Service
public class NPDRendtionRenameProcess implements WorkflowProcess {
	@Property(value = "Rename the rendition from cq5dam.web to cq5dam.npd")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "NorthPoint Digital")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "NorthPoint Digital Rendition Rename Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(NPDRendtionRenameProcess.class);
    
    private Pattern RENDITION_PATTERN = Pattern.compile("cq5dam\\.web\\.(.*)\\.(.*)");
    
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
        Map<String, String> renditionsMap = new HashMap<String, String>();
        String[] renditionMappings = metadata.get("PROCESS_ARGS", String.class).split(",");
        boolean renameOriginal = false;
        for (String mapping : renditionMappings) {
            String[] tuple = mapping.split(":");
            renditionsMap.put(tuple[0].trim(), tuple[1].trim());
            if(tuple[0].trim().equals("original")){
            	renameOriginal = true;
            }
        }
        
        Session session = workflowSession.getSession();
        String originalPath = item.getWorkflowData().getPayload().toString();
        try {
            Node renditionsNode = session.getNode(originalPath);
            while (!renditionsNode.getName().equals("renditions")) {
                renditionsNode = renditionsNode.getParent();
            }
            String imagePath = renditionsNode.getPath().substring(0, renditionsNode.getPath().indexOf("/jcr:content/renditions"));
            
            NodeIterator iter = renditionsNode.getNodes();
            while (iter.hasNext()) {
                Node srcNode = iter.nextNode();
                String srcRendition = srcNode.getName();
                if(srcRendition.equals("original") && renameOriginal){
                	String [] fileTokens = imagePath.split("\\.(?=[^\\.]+$)");
                	String fileExtension = fileTokens[1];
                	String targetRendition = renditionsMap.get("original") + "." + fileExtension;
                	try{
                		JcrUtil.copy(srcNode, srcNode.getParent(), targetRendition);
                	}catch(Exception e){
                		e.printStackTrace();
                	}
                }else{
	                Matcher matcher = RENDITION_PATTERN.matcher(srcRendition);
	                if (matcher.find()) {
	                    String srcShortRendition = matcher.group(1);
	                    String extension = matcher.group(2);
	                    String targetShortRendition = renditionsMap.get(srcShortRendition);
	
	                    if (targetShortRendition != null) {
	                        String targetRendition = srcNode.getParent().getPath() + "/" + "cq5dam.npd." + targetShortRendition + "." + extension;
							if (session.nodeExists(targetRendition)) {
								session.removeItem(targetRendition);
							}
							log.info("Creating rendition " + targetRendition);
							session.move(srcNode.getPath(), targetRendition);
	                    }
	                } else {
	                    continue;
	                }
                }
            }
            session.save();
        } catch (PathNotFoundException e) {
		log.error("Unable to find path " + originalPath);
        } catch (RepositoryException e) {
		e.printStackTrace();
        }
    }
}

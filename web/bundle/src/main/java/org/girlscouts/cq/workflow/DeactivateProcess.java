package org.girlscouts.cq.workflow;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component
@Service({WorkflowProcess.class})
@Property(name="process.label", value={"Girl Scouts Deactivate Node"})
public class DeactivateProcess implements WorkflowProcess {
    private static Logger log = Logger.getLogger(DeactivateProcess.class);

    @Reference
    private Replicator replicator;

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap meta)
            throws WorkflowException {
        Session session = workflowSession.getSession();
        String path = workItem.getWorkflowData().getPayload().toString();
        try {
            replicator.replicate(session, ReplicationActionType.DELETE, path);
        } catch (ReplicationException e) {
            log.error("Cannot deactivate: " + path);
        }
    }
    
    
}
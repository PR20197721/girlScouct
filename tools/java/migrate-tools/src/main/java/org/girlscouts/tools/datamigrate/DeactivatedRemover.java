package org.girlscouts.tools.datamigrate;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: server username password [root] [dryrun]
// Remove deactivated assets pages
// server example: http://localhost:4502/crx/server/

public class DeactivatedRemover 
{
    private static String REPLICATION_PROP = "cq:lastReplicationAction";
    private static String REPLICATION_VAL = "Deactivate";
    private boolean isDryRun;
    private int totalCount = 0, removedCount = 0;
    private List<String> toRemove;
    private Session session;
    
    public DeactivatedRemover(String server, String username, String password, boolean isDryRun) throws RepositoryException {
        this.isDryRun = isDryRun;
        this.toRemove = new ArrayList<String>();
        this.session = getSession(server, username, password);
    }
    
    private Session getSession(String server, String username, String password) throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        return session;
    }
    
    void scan(String parentPath) throws RepositoryException {
        Node parentNode = session.getNode(parentPath);
        NodeIterator iter = parentNode.getNodes();
        while (iter.hasNext()) {
            totalCount++;
            Node node = iter.nextNode();
            System.out.println("checking deactivated: " + node.getPath());
            if (node.hasProperty(REPLICATION_PROP) &&
                    node.getProperty(REPLICATION_PROP).getString().equals(REPLICATION_VAL)) {
                removedCount++;
                if (node.getName().equals("jcr:content")) {
                    toRemove.add(parentPath);
                    break; // Parent is removed, iteration no longer needed.
                } else {
                    toRemove.add(node.getPath());
                }
            } else {
                // Normal node, go deeper 
                scan(node.getPath());
            }
        }
    }
    
    void doRemove() throws RepositoryException {
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("TO REMOVE");
        if (isDryRun) {
            System.out.println("dry run");
        }
        System.out.println("=======================");
        System.out.println("=======================");
        for (String nodePath : toRemove) {
            System.out.println(nodePath);
            if (!isDryRun) {
                session.removeItem(nodePath);
                session.save();
            }
        }
        System.out.println("=======================");
        System.out.println("Total Nodes scanned: " + totalCount + " Total nodes removed: " + removedCount);
    }
}
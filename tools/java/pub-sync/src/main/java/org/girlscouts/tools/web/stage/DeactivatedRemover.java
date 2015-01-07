package org.girlscouts.tools.web.stage;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: server username password
// Remove deactivated assets pages
// server example: http://localhost:4502/crx/server/

public class DeactivatedRemover 
{
    public static void main(String[] args)
    {
        if (args.length < 3) {
            System.out.println("Remove deactivated assets and pages");
            System.out.println("Params: server username password [dryrun]");
            System.out.println("Server example: http://localhost:4502/crx/server");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];
        boolean isDryRun = args.length >= 4 && args[3].equals("dryrun");

        try {
            DeactivatedRemover remover = new DeactivatedRemover(server, username, password, isDryRun);
            System.out.println("Scanning repo for deactivated nodes ...");
            remover.scan("/content");
            remover.doRemove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isDryRun;
    private int totalCount = 0, removedCount = 0;
    private List<String> toRemove;
    private Session session;
    private static String REPLICATION_PROP = "cq:lastReplicationAction";
    private static String REPLICATION_VAL = "Deactivate";
    
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
    
    private void scan(String parentPath) throws RepositoryException {
        Node parentNode = session.getNode(parentPath);
        NodeIterator iter = parentNode.getNodes();
        while (iter.hasNext()) {
            totalCount++;
            Node node = iter.nextNode();
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
    
    private void doRemove() throws RepositoryException {
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("TO REMOVE");
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
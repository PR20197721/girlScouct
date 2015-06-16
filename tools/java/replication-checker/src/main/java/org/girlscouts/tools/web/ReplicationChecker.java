package org.girlscouts.tools.web;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: server username password
// server example: http://localhost:4503/crx/server/
public class ReplicationChecker 
{
    public static void main(String[] args)
    {
        if (args.length < 3) {
            System.out.println("VTK meeting updater");
            System.out.println("Params: server username password");
            System.out.println("Server example: http://localhost:4503/crx/server");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];

        try {
            ReplicationChecker updater = new ReplicationChecker(server, username, password);
            updater.doUpdate();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    
    private static String REF_ID_PROP = "refId";
    private static String[] SKIPPED_NODES = {"last-import-timestamp", "global-settings"};
    private static Map<String, String> MEETING_MAP;
    static {
        MEETING_MAP = new HashMap<String, String>();
        MEETING_MAP.put("D14BS01", "D14DP01");
        MEETING_MAP.put("D14BS02", "D14DP02");
        MEETING_MAP.put("D14BS13", "D14TC13");
        MEETING_MAP.put("D14BS14", "D14DP11");
        MEETING_MAP.put("D14BS15", "D14DP12");
        MEETING_MAP.put("D14TC01", "D14DP01");
        MEETING_MAP.put("D14TC02", "D14DP02");
        MEETING_MAP.put("B14WA01", "B14B01");
        MEETING_MAP.put("B14WA02", "B14B02");
        MEETING_MAP.put("B13WA13", "B14OG13");
        MEETING_MAP.put("B13WA14", "B14OG14");
        MEETING_MAP.put("B13WA15", "B14OG15");
        MEETING_MAP.put("B14WA16", "B14OG16");
        MEETING_MAP.put("B14WA17", "B14OG17");
        MEETING_MAP.put("B14WA18", "B14OG18");
        MEETING_MAP.put("B14WA19", "B14OG19");
        MEETING_MAP.put("B14WA20", "B14OG20");
        MEETING_MAP.put("B14WA21", "B14OG21");
        MEETING_MAP.put("B14WA22", "B14OG22");
        MEETING_MAP.put("B14OG01", "B14B01");
        MEETING_MAP.put("B14OG02", "B14B02");
        MEETING_MAP.put("B14OG11", "B14B03");
        MEETING_MAP.put("B14OG12", "B14B04");
        MEETING_MAP.put("J14GM01", "J14B01");
        MEETING_MAP.put("J14GM02", "J14B02");
        MEETING_MAP.put("J14GM11", "J14B03");
        MEETING_MAP.put("J14GM12", "J14B04");
        MEETING_MAP.put("J14GM13", "J14B09");
        MEETING_MAP.put("J14GM14", "J14B10");
        MEETING_MAP.put("J14GM15", "J14B11");
        MEETING_MAP.put("J14YS01", "J14B01");
        MEETING_MAP.put("J14YS02", "J14B02");
        MEETING_MAP.put("J14YS11", "J14B05");
        MEETING_MAP.put("J14YS12", "J14B06");
        MEETING_MAP.put("J14GM16", "J14B18");
        MEETING_MAP.put("J14GM17", "J14B19");
        MEETING_MAP.put("J14GM18", "J14B20");
        MEETING_MAP.put("J14GM19", "J14B16");
        MEETING_MAP.put("J14GM20", "J14B17");
    }
    
    private String server;
    private String username;
    private String password;
    private Session session;
    private int allCount = 0;
    private int updatedCount = 0;
    
    public ReplicationChecker(String server, String username, String password) throws RepositoryException {
        this.server = server;
        this.username = username;
        this.password = password;
        getSession();
    }
    
    private void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }
    
    private void doUpdate() throws RepositoryException {
        allCount = 0;
        updatedCount = 0;
        Node vtkRootNode = session.getNode("/vtk");
        NodeIterator councilIter = vtkRootNode.getNodes();
        while (councilIter.hasNext()) {
            Node councilNode = councilIter.nextNode();
            boolean isSkipped = false;
            for (String skipped : SKIPPED_NODES) {
                if (skipped.equals(councilNode.getName())) {
                    isSkipped = true;
                    break;
                }
            }
            if (isSkipped) {
                continue;
            }
                
            NodeIterator troopIter = councilNode.getNodes();
            Node troopNode = null;
            while (troopIter.hasNext()) {
                try {
                    troopNode = troopIter.nextNode();
                    NodeIterator userIter = troopNode.getNode("users").getNodes();
                    while (userIter.hasNext()) {
                        Node userNode = userIter.nextNode();
                        try {
                            updateUserMeetings(userNode);
                        } catch (RepositoryException re) {
                            System.err.println("ERROR: Cannot update meeting for " + userNode.getPath());
                        }
                    }
                } catch (RepositoryException e) {
                    System.err.println("ERROR: loading troop: " + troopNode.getPath());
                }
            }
        }
        System.out.println("All meeting count: " + Integer.toString(allCount));
        System.out.println("Updated meeting count: " + Integer.toString(updatedCount));
    }
    
    private void updateUserMeetings(Node userNode) throws PathNotFoundException, RepositoryException {
        allCount++;
        Node allMeetingsNode = userNode.getNode("yearPlan/meetingEvents");
        NodeIterator meetingIter = allMeetingsNode.getNodes();
        while (meetingIter.hasNext()) {
            Node meetingNode = meetingIter.nextNode();
            String refId = meetingNode.getProperty(REF_ID_PROP).getString();
            for (String oldMeeting : MEETING_MAP.keySet()) {
                if (refId.endsWith(oldMeeting)) {
                    String newMeeting = MEETING_MAP.get(oldMeeting);
                    String oldRefId = refId;
                    refId = refId.replaceAll(oldMeeting + "$", newMeeting); 
                    // There might be customized meetings like B14B01_xxxxxx, so we added a $
                    meetingNode.setProperty(REF_ID_PROP, refId);
                    updatedCount++;
                    System.out.println(meetingNode.getPath() + " : " + oldRefId + " => " + refId);
                    session.save();
                    break; // Only one mapping is possible
                }
            }
            
            if (!session.nodeExists(refId)) {
                System.err.println("ERROR: " + meetingNode.getPath() + " : Path not found: " + refId);
            }
        }
    }
}
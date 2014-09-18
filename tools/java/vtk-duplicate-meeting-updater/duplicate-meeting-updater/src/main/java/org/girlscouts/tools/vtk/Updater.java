package org.girlscouts.tools.vtk;

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
// server example: http://localhostL4503/crx/server/
public class Updater 
{
    public static void main(String[] args)
    {
        String server = args[0];
        String username = args[1];
        String password = args[2];

        try {
            Updater updater = new Updater(server, username, password);
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
        MEETING_MAP.put("B14G01", "B14B01");
        MEETING_MAP.put("B14G02", "B14B02");
        MEETING_MAP.put("B14G11", "B14B03");
        MEETING_MAP.put("B14G12", "B14B04");
        MEETING_MAP.put("J14GM01", "J14B01");
        MEETING_MAP.put("J14MG02", "J14B02");
        MEETING_MAP.put("J14GM11", "J14B03");
        MEETING_MAP.put("J14GM12", "J14B04");
        MEETING_MAP.put("J14GM13", "J14B09");
        MEETING_MAP.put("J14GM14", "J14B10");
        MEETING_MAP.put("J14GM15", "J14B11");
        MEETING_MAP.put("J14YS01", "J14B01");
        MEETING_MAP.put("J14YS02", "J14B02");
        MEETING_MAP.put("J14YS11", "J14B05");
        MEETING_MAP.put("J14YS12", "J14B06");
        MEETING_MAP.put("B14WA16", "B14OG16");
        MEETING_MAP.put("B14WA17", "B14OG17");
        MEETING_MAP.put("B14WA18", "B14OG18");
        MEETING_MAP.put("B14WA19", "B14OG19");
        MEETING_MAP.put("B14WA20", "B14OG20");
        MEETING_MAP.put("B14WA21", "B14OG21");
        MEETING_MAP.put("B14WA22", "B14OG22");
        MEETING_MAP.put("J14B18", "J14GM16");
        MEETING_MAP.put("J14B19", "J14GM17");
        MEETING_MAP.put("J14B20", "J14GM18");
        MEETING_MAP.put("J14B16", "J14GM19");
        MEETING_MAP.put("J14B17", "J14GM20");
    }
    
    private String server;
    private String username;
    private String password;
    private Session session;
    private int allCount = 0;
    private int updatedCount = 0;
    
    public Updater(String server, String username, String password) throws RepositoryException {
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
                }
            }
            if (isSkipped) {
                continue;
            }
                
            NodeIterator troopIter = councilNode.getNodes();
            while (troopIter.hasNext()) {
                try {
                    NodeIterator userIter = troopIter.nextNode().getNode("users").getNodes();
                    while (userIter.hasNext()) {
                        Node userNode = userIter.nextNode();
                        try {
                            updateUserMeetings(userNode);
                        } catch (RepositoryException re) {
                            System.err.println("Cannot update meeting for " + userNode.getPath());
                        }
                    }
                } catch (RepositoryException e) {
                    System.err.println("Error loading troop: " + troopIter.nextNode().getPath());
                }
            }
        }
        System.out.println("All meeting count: " + Integer.toString(allCount));
        System.out.println("Updated meeting count: " + Integer.toString(updatedCount));
    }
    
    private void updateUserMeetings(Node userNode) throws PathNotFoundException, RepositoryException {
        System.out.println("# " + allCount + " Evaluating user node: " + userNode.getPath());
        allCount++;
        Node allMeetingsNode = userNode.getNode("yearPlan/meetingEvents");
        NodeIterator meetingIter = allMeetingsNode.getNodes();
        while (meetingIter.hasNext()) {
            Node meetingNode = meetingIter.nextNode();
            String refId = meetingNode.getProperty(REF_ID_PROP).getString();
            System.out.println(refId);
            for (String oldMeeting : MEETING_MAP.keySet()) {
                if (refId.contains(oldMeeting)) {
                    String newMeeting = MEETING_MAP.get(oldMeeting);
                    refId = refId.replaceAll(oldMeeting, newMeeting);
                    meetingNode.setProperty(REF_ID_PROP, refId);
                    updatedCount++;
                    System.out.println(meetingNode.getPath() + " : " + 
                            oldMeeting + " => " + newMeeting);
                    session.save();
                    break; // Only one mapping is possible
                }
            }
        }
    }
}
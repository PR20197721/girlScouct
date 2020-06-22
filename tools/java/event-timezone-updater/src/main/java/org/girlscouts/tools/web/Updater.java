package org.girlscouts.tools.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

// Params: server username password
// server example: http://localhost:4503/crx/server/
public class Updater 
{
	private static Logger log = LoggerFactory.getLogger(Updater.class);

	public static void main(String[] args)
    {
        if (args.length < 3) {
        	log.info("Event Updater");
        	log.info("Params: server username password");
        	log.info("Server example: http://localhost:4503/crx/server");
            System.exit(-1);
        }
        String server = args[0];
        String username = args[1];
        String password = args[2];

        try {
            Updater updater = new Updater(server, username, password);
            updater.doUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Map<String, String> TIMEZONE_MAP;
    static {
        TIMEZONE_MAP = new LinkedHashMap<String, String>();
        TIMEZONE_MAP.put("girlscouts-future", "US/Eastern");
        TIMEZONE_MAP.put("girlscoutcsa", "US/Eastern");
        TIMEZONE_MAP.put("gsnetx", "US/Central");
        TIMEZONE_MAP.put("gswcf", "US/Eastern");
        TIMEZONE_MAP.put("girlscoutsnccp", "US/Eastern");
        TIMEZONE_MAP.put("gateway", "US/Eastern");
        TIMEZONE_MAP.put("gssem", "US/Eastern");
        TIMEZONE_MAP.put("gssjc", "US/Central");
        TIMEZONE_MAP.put("gsctx", "US/Central");
        TIMEZONE_MAP.put("girlscoutsaz", "US/Arizona"); // No daylight savings
        TIMEZONE_MAP.put("gswestok", "US/Central");
        TIMEZONE_MAP.put("gssnv", "US/Pacific");
        TIMEZONE_MAP.put("kansasgirlscouts", "US/Central");
    }
    
    private String server;
    private String username;
    private String password;
    private Session session;
    private int count;
    
    public Updater(String server, String username, String password) throws RepositoryException {
        this.server = server;
        this.username = username;
        this.password = password;
        this.count = 0;
        getSession();
    }
    
    private void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(server);
        SimpleCredentials creds = new SimpleCredentials(username, password.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }
    
    private void doUpdate() throws RepositoryException, ParseException {
        for (String council : TIMEZONE_MAP.keySet()) {
            String branch = "/content/" + council + "/en";
            if (!session.nodeExists(branch)) {
            	log.error("Node not found: " + branch);
                continue;
            }

            String timezoneStr = TIMEZONE_MAP.get(council);
            Node branchNode = session.getNode(branch + "/jcr:content");
            branchNode.setProperty("timezone", timezoneStr);
            try {
                session.save();
            } catch (RepositoryException re) {
            	log.error("Cannot update timezone for branch: " + branch);
                this.session.refresh(false);
            }
            
            Node repoNode = session.getNode(branch + "/events-repository");
            NodeIterator yearIter = repoNode.getNodes();
            while (yearIter.hasNext()) {
                Node yearNode = yearIter.nextNode();
                if (yearNode.getName().equals("jcr:content")) {
                    continue;
                }
                NodeIterator eventIter = yearNode.getNodes();
                while (eventIter.hasNext()) {
                    Node eventNode = null;
                    try {
                        eventNode = eventIter.nextNode();
                        if (eventNode.getName().equals("jcr:content")) {
                            continue;
                        }
                        Node dataNode = eventNode.getNode("jcr:content/data");
                        updateTimezone(dataNode, "start", timezoneStr); 
                        updateTimezone(dataNode, "end", timezoneStr); 
                        count++;
                        log.info("Updated event: " + eventNode.getPath());
                    } catch (RepositoryException re) {
                    	log.error("Cannot update event: " + eventNode.getPath());
                        re.printStackTrace();
                        this.session.refresh(false);
                    }
                }
            }
        }
        log.info("# of events updated: " + Integer.toString(this.count));
    }
    
    private void updateTimezone(Node node, String key, String timezoneStr) throws RepositoryException, ParseException {
        if (!node.hasProperty(key)) {
            return;
        }
        Date origDate = node.getProperty(key).getDate().getTime();
        DateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String dateStr = oldFormat.format(origDate);

        TimeZone tz = TimeZone.getTimeZone(timezoneStr);
        Calendar cal = Calendar.getInstance(tz);
        DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        newFormat.setCalendar(cal);
        Date date = newFormat.parse(dateStr);
        cal.setTime(date);

        node.setProperty(key, cal);
        session.save();
    }
}
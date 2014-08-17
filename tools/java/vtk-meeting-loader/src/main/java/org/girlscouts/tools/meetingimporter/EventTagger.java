package org.girlscouts.tools.meetingimporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;

import org.apache.jackrabbit.commons.JcrUtils;


public class EventTagger {
    private static String JCR_SERVER = "http://localhost:4502/crx/server/";
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    
    private static String TAG_PROP = "cq:tags";
    private static String TITLE_PROP = "jcr:title";
    
    private String taggingFile;
    private String eventBranch;
    private String tagPrefix;
    private Map<String, String> tagMap;
    
    public EventTagger(String taggingFile, String eventBranch, String tagPrefix) {
        this.taggingFile = taggingFile;
        this.eventBranch = eventBranch;
        this.tagPrefix = tagPrefix;
    }
    
    public void readTagging() throws Exception {
        tagMap = new HashMap<String, String>();
        
        BufferedReader reader = new BufferedReader(new FileReader(taggingFile));
        String line = null;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            String[] settings = line.split("\t");
            if (settings.length < 2) {
                throw new Exception("Error parsing tagging file on line: " + Integer.toString(lineCount));
            }
            String title = settings[0].trim();
            String tag = settings[1].trim();
            
            if (tagMap.containsKey(title)) {
                if (!tag.equals(tagMap.get(title))) {
                    throw new Exception ("Title <-> Tag relationship not uniq on line: " + Integer.toString(lineCount));
                }
            }
            tagMap.put(title, tag);
        }
        reader.close();
    }
    
    public void applyTags() throws Exception {
        Session session = getSession();
        Node parentNode = session.getNode(eventBranch);
        
        NodeIterator iter = parentNode.getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode().getNode("jcr:content");
            List<String> tags = new ArrayList<String>();
            if (node.hasProperty(TAG_PROP)) {
                Value[] originalValues = node.getProperty(TAG_PROP).getValues();
                for (Value value : originalValues) {
                    tags.add(value.getString());
                }
            }

            String title = node.getProperty(TITLE_PROP).getString();
            String tagToAdd = tagMap.get(title);
            if (tagToAdd == null) {
                System.err.println("No tag associated to title: " + title);
                continue;
            }
            
            tagToAdd = tagPrefix + tagToAdd;
            tags.add(tagToAdd);
            
            node.setProperty(TAG_PROP, tags.toArray(new String[tags.size()]));
        }
        session.save();
    }
    
    private Session getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(JCR_SERVER);
        SimpleCredentials creds = new SimpleCredentials(USERNAME, PASSWORD.toCharArray());
        Session session = repository.login(creds);
        return session;
    }

    public static void main(String[] args) {
        
    }
    
}

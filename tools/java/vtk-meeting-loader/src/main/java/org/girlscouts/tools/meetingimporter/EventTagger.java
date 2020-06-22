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

import com.day.cq.commons.jcr.JcrUtil;


public class EventTagger {
    private static String JCR_SERVER = "http://localhost:4502/crx/server/";
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    
    private static String TAG_PROP = "cq:tags";
    private static String TITLE_PROP = "jcr:title";
    private static String CATEGORY_TAG = "categories";
    private static String DEFAULT_NAMESPACE = "girlscouts";
    private static String TAG_BRANCH = "/etc/tags";
    private static String SLING_RESOURCE_TYPE_PROP = "sling:resourceType";
    private static String SLING_RESOURCE_TYPE_VALUE = "cq/tagging/components/tag";
    
    private Session session;
    private String taggingFile;
    private String eventBranch;
    private String tagPrefix;
    private Map<String, String> tagMap;
    private static Logger log = LoggerFactory.getLogger(EventTagger.class);

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
        Node parentNode = session.getNode(eventBranch);
        
        NodeIterator iter = parentNode.getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            if (node.getName().equals("jcr:content")) {
                continue;
            }

            node = node.getNode("jcr:content");
            List<String> tags = new ArrayList<String>();
            if (node.hasProperty(TAG_PROP)) {
                Value[] originalValues = node.getProperty(TAG_PROP).getValues();
                for (Value value : originalValues) {
                    tags.add(value.getString().replaceAll("^" + DEFAULT_NAMESPACE, tagPrefix));
                }
            }
            
            String title = node.getProperty(TITLE_PROP).getString();
            String tagsToAdd = tagMap.get(title);
            if (tagsToAdd == null) {
            	log.error("No tag associated to title: " + title);
                continue;
            }
            
            for (String tagToAdd : tagsToAdd.split(",")) {
                tagToAdd = tagToAdd.trim();
                tagToAdd = createOrGetCategoryTag(tagToAdd);
                
                tagToAdd = tagPrefix + ":" + CATEGORY_TAG + "/" + tagToAdd;
                tags.add(tagToAdd);
            }
            
            node.setProperty(TAG_PROP, tags.toArray(new String[tags.size()]));
            log.info("Tag: " + tagsToAdd + " applied to " + node.getPath());
        }
        session.save();
        log.info("Session saved.");
    }
    
    private String createOrGetCategoryTag(String category) throws RepositoryException {
        String tagNodeName = category.toLowerCase()
                .replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9\\-]", "")
                .replaceAll("\\-+", "-");
        String tagPath = TAG_BRANCH + "/" + tagPrefix + "/" + CATEGORY_TAG + "/" + tagNodeName;
        String tagRelPath = tagPath.substring(1); // Skip root slash
        Node root = session.getRootNode();
        if (!root.hasNode(tagRelPath)) {
            Node tagNode = JcrUtil.createPath(tagPath, "cq:Tag", session);
            tagNode.setProperty(TITLE_PROP, category);
            tagNode.setProperty(SLING_RESOURCE_TYPE_PROP, SLING_RESOURCE_TYPE_VALUE);
            session.save();
        }
        return tagNodeName;
    }
    
    public void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(JCR_SERVER);
        SimpleCredentials creds = new SimpleCredentials(USERNAME, PASSWORD.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }

    // Example Args
    ///Users/mike/Desktop/taggings.txt /content/girlscoutsnccp/en/events-repository/2014 girlscoutsnccp
    public static void main(String[] args) throws Exception {
        EventTagger eventTagger = new EventTagger(args[0], args[1], args[2]);
        eventTagger.getSession();
        eventTagger.readTagging();
        eventTagger.applyTags();
    }
    
}

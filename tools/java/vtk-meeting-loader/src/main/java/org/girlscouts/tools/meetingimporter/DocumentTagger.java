package org.girlscouts.tools.meetingimporter;

import java.io.FileInputStream;
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
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import com.day.cq.commons.jcr.JcrUtil;

public class DocumentTagger {
    private static String JCR_SERVER = "http://localhost:4502/crx/server/";
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";

    private static String TAG_PROP = "cq:tags";
    private static String JCR_TITLE_PROP = "jcr:title";
    private static String DC_TITLE_PROP = "dc:title";
    private static String DC_DESCRIPTION = "dc:description";
    private static String DOCUMENT_TAG = "forms_documents";
    private static String TAG_BRANCH = "/etc/tags";
    private static String SLING_RESOURCE_TYPE_PROP = "sling:resourceType";
    private static String SLING_RESOURCE_TYPE_VALUE = "cq/tagging/components/tag";

    private Session session;
    private String taggingFile;
    private String documentsBranch;
    private String tagPrefix;
    private Map<String, String[]> docMap;
    private static Logger log = LoggerFactory.getLogger(DocumentTagger.class);

    public DocumentTagger(String taggingFile, String eventBranch,
            String tagPrefix) {
        this.taggingFile = taggingFile;
        this.documentsBranch = eventBranch;
        this.tagPrefix = tagPrefix;
    }

    public void readMetadata() throws Exception {
        docMap = new HashMap<String, String[]>();
        
        FileInputStream fis = new FileInputStream(taggingFile);
        Workbook workbook = WorkbookFactory.create(fis);
        
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Sheet sheet = workbook.getSheetAt(0);
        // Skip the title line
        for(int i = 2; i < sheet.getLastRowNum(); i++){
            String filename = getCellVal( evaluator, sheet, "A"+i );
            if(filename == null || filename.isEmpty()) {
                log.error("Filename error: " + filename + " on line: " + i);
                continue;
            }
            
            String title = getCellVal(evaluator, sheet, "B" + i);
            String description = getCellVal(evaluator, sheet, "E" + i);
            String categories = getCellVal(evaluator, sheet, "D" + i);
            
            String[] meta = new String[]{title, description, categories};
            docMap.put(filename, meta);
        }
    }

    private String getCellVal(FormulaEvaluator evaluator, Sheet sheet, String field) {
        String toRet = "";
        CellReference ref = new CellReference(field);

        Row row = sheet.getRow(ref.getRow());
        if (row != null) {
            Cell cell = row.getCell(ref.getCol());
            CellValue value = evaluator.evaluate(cell);

            if (value != null)
                toRet = value.getStringValue();
        }
        return toRet.trim();
    }

    public void applyMeta() throws Exception {
        Node parentNode = session.getNode(documentsBranch);

        NodeIterator iter = parentNode.getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            if (node.getName().equals("jcr:content")) {
                continue;
            }

            String filename = node.getName();
            node = node.getNode("jcr:content/metadata");
            
            String[] meta = docMap.get(filename);
            if (meta == null) {
                log.error("Cannot get metadata for file: " + filename);
                continue;
            }
            
            String title = meta[0];
            String description = meta[1];
            String categoriesStr = meta[2];
            
            removeProperty(node, DC_TITLE_PROP);
            removeProperty(node, JCR_TITLE_PROP);
            removeProperty(node, DC_DESCRIPTION);
            session.save();
            node.setProperty(DC_TITLE_PROP, title);
            node.setProperty(JCR_TITLE_PROP, title);
            node.setProperty(DC_DESCRIPTION, description);
            
            List<String> tags = new ArrayList<String>();
            for (String category : categoriesStr.split(",")) {
                category = category.trim();
                String categoryTag = createOrGetCategoryTag(category);
                categoryTag = tagPrefix + ":" + DOCUMENT_TAG + "/" + categoryTag;
                tags.add(categoryTag);
            }
            
            if (!tags.isEmpty()) {
                node.setProperty(TAG_PROP, tags.toArray(new String[tags.size()]));
            }
            log.info("Metadata added to " + node.getPath());
        }
        session.save();
        log.info("Session saved.");
    }
    
    private void removeProperty(Node node, String property) throws Exception, VersionException, LockException, ConstraintViolationException, RepositoryException {
        if (node.hasProperty(property)) {
            if (node.getProperty(property).isMultiple()) {
                node.setProperty(property, (String[])null);
            } else {
                node.setProperty(property, (String)null);
            }
        }
    }
    
    private String createOrGetCategoryTag(String category) throws RepositoryException {
        String tagNodeName = category.toLowerCase()
                .replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9\\-]", "")
                .replaceAll("\\-+", "-");
        String tagPath = TAG_BRANCH + "/" + tagPrefix + "/" + DOCUMENT_TAG + "/" + tagNodeName;
        String tagRelPath = tagPath.substring(1); // Skip root slash
        Node root = session.getRootNode();
        if (!root.hasNode(tagRelPath)) {
            Node tagNode = JcrUtil.createPath(tagPath, "cq:Tag", session);
            tagNode.setProperty(JCR_TITLE_PROP, category);
            tagNode.setProperty(SLING_RESOURCE_TYPE_PROP, SLING_RESOURCE_TYPE_VALUE);
            session.save();
        }
        return tagNodeName;
    }

    public void getSession() throws RepositoryException {
        Repository repository = JcrUtils.getRepository(JCR_SERVER);
        SimpleCredentials creds = new SimpleCredentials(USERNAME,
                PASSWORD.toCharArray());
        Session session = repository.login(creds);
        this.session = session;
    }

    // Example Args
    // /Users/mike/Desktop/documents.xlsx
    // /content/dam/gssem/documents gssem 
    public static void main(String[] args) throws Exception {
        DocumentTagger eventTagger = new DocumentTagger(args[0], args[1], args[2]);
        eventTagger.readMetadata();
        eventTagger.getSession();
        eventTagger.applyMeta();
    }
}

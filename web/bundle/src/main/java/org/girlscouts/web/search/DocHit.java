package org.girlscouts.web.search;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.result.Hit;

public final class DocHit {
    private static final Logger log = LoggerFactory.getLogger(DocHit.class);
    private final Hit hit;
    private static final Pattern STRONG_PATTERN = Pattern.compile("<strong>.*?</strong>");
    private static final Pattern STRIP_STRONG_PATTERN = Pattern.compile("</?strong>");
    private static final Pattern[] EXCERPT_PATTERNS = {
        Pattern.compile("<.*?>"),
        Pattern.compile("<.*?$"),
        Pattern.compile("^.*?>")
    };

    public DocHit(Hit hit) {
        this.hit = hit;
    }

    public String getTitle() throws RepositoryException {
        String excerpt = (String) this.hit.getExcerpts().get("jcr:title");
        if (excerpt != null) {
            return excerpt;
        }
        return getPageOrAsset().getName();
    }

    public String getExcerpt() throws RepositoryException {
        String excerpt = this.hit.getExcerpt();
        log.error("#### orig excerpt:" + excerpt);
        
        Matcher queryMatcher = STRONG_PATTERN.matcher(excerpt);
        String queryString = null;
        if (queryMatcher.find()) {
            queryString = STRIP_STRONG_PATTERN.matcher(queryMatcher.group()).replaceAll("");
        }

        for (Pattern pattern: EXCERPT_PATTERNS) {
            excerpt = pattern.matcher(excerpt).replaceAll("");
        }

        if (queryString != null) {
            excerpt = excerpt.replaceAll(queryString, "<strong>" + queryString + "</strong>");
        }
        return excerpt;
    }

    public String getURL() throws RepositoryException {
        Node n = getPageOrAsset();
        String url = n.getPath();
        if (isPage(n)) {
            url = url + ".html";
        }
        return url;
    }

    public String getIcon() throws RepositoryException {
        String url = getURL();
        int idx = url.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        String ext = url.substring(idx + 1);
        if (ext.equals("html")) {
            return "";
        }
        //TODO
        String path = "/etc/designs/default/0.gif";
        if (path == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<img src='");
        sb.append(path).append("'/>");
        return sb.toString();
    }

    public String getExtension() throws RepositoryException {
        String url = getURL();
        int idx = url.lastIndexOf('.');
        return idx >= 0 ? url.substring(idx + 1) : "";
    }

    public Map getProperties() throws RepositoryException {
        return this.hit.getProperties();
    }

    private boolean isPageOrAsset(Node n) throws RepositoryException {
        return (isPage(n)) || (n.isNodeType("dam:Asset"));
    }

    private boolean isPage(Node n) throws RepositoryException {
        return (n.isNodeType("cq:Page")) || (n.isNodeType("cq:PseudoPage"));
    }

    private Node getPageOrAsset() throws RepositoryException {
        Node n = this.hit.getNode();
        while ((!isPageOrAsset(n)) && (n.getName().length() > 0)) {
            n = n.getParent();
        }
        return n;
    }
}
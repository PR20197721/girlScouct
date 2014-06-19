package org.girlscouts.web.search;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.result.Hit;

public final class DocHit extends DocHitBase {
    private static final Logger log = LoggerFactory.getLogger(DocHit.class);
    private final Hit hit;
    private static final Pattern STRONG_PATTERN = Pattern.compile("<strong>.*?</strong>");
    private static final Pattern STRIP_STRONG_PATTERN = Pattern.compile("</?strong>");
    private static final Pattern[] EXCERPT_PATTERNS = {
        Pattern.compile("<.*?>"),
        Pattern.compile("<.*?$"),
        Pattern.compile("^.*?>")
    };

    public DocHit(Hit hit) throws RepositoryException {
        super(hit.getNode());
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

    public Map getProperties() throws RepositoryException {
        return this.hit.getProperties();
    }
}
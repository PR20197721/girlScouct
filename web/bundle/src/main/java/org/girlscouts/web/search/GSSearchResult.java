package org.girlscouts.web.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GSSearchResult implements GSSearchResultConstants {

    private static final Logger log = LoggerFactory.getLogger(GSSearchResult.class);

	private final Node resultNode;
	private final Double score;
	private final String path;
	private final Row row;

	public GSSearchResult(Row row) throws RepositoryException {
		this.score = row.getValue("jcr:score").getDouble();
		this.resultNode = getPageOrAsset(row.getNode());
		if (this.resultNode != null) {
			this.path = this.resultNode.getPath();
		} else {
			this.path = null;
		}
		this.row = row;
    }

	protected Node getPageOrAsset(Node node) throws RepositoryException {
		if (node != null) {
			while ((!isPageOrAsset(node)) && (node.getName().length() > 0)) {
				node = node.getParent();
			}
			return node;
		} else {
			return node;
		}
	}

	private boolean isPageOrAsset(Node n) throws RepositoryException {
		return (isPage(n)) || (n.isNodeType(NODE_TYPE_DAM_ASSET));
	}

	protected boolean isPage(Node n) throws RepositoryException {
		return (n.isNodeType(NODE_TYPE_CQ_PAGE)) || (n.isNodeType(NODE_TYPE_CQ_PSEUDO_PAGE));
	}

	public String getDescription() {
		String description = "";
		try {
			if (this.resultNode.hasProperty(PROPERTY_SRCH_DESC)) {
				return this.resultNode.getProperty(PROPERTY_SRCH_DESC).getString();
			}
			if (this.resultNode.hasProperty(PROPERTY_DC_DESCRIPTION)) {
				if (this.resultNode.getProperty(PROPERTY_DC_DESCRIPTION).isMultiple()) {
					Value[] value = this.resultNode.getProperty(PROPERTY_DC_DESCRIPTION).getValues();
					if ((!value[0].getString().isEmpty()) && (value[0].getString() != null)) {
						return (value[0].getString());
					}
				}
				return this.resultNode.getProperty(PROPERTY_DC_DESCRIPTION).getString();
			}
		} catch (Exception e) {
			log.info("Cannot get description. Return empty string.");
		}

		return description;
	}

	public String getTitle() {
		String title = "";
		try {
			try {
				String seoTitle = this.resultNode.getProperty(PROPERTY_SEO_TITLE).getString();
				if (seoTitle != null) {
					return seoTitle;
				}
			} catch (Exception e) {

			}

			String primaryType = this.resultNode.getPrimaryNodeType().getName();
			if (primaryType.equals(NODE_TYPE_CQ_PAGE) && this.resultNode.hasProperty(PROPERTY_JCR_TITLE)) {
				return this.resultNode.getProperty(PROPERTY_JCR_TITLE).getString();
			} else if (primaryType.equals(NODE_TYPE_DAM_ASSET) && this.resultNode.hasProperty(PROPERTY_DC_TITLE)) {
				if (this.resultNode.getProperty(PROPERTY_DC_TITLE).isMultiple()) {
					Value[] value = this.resultNode.getProperty(PROPERTY_DC_TITLE).getValues();
					if ((!value[0].getString().isEmpty()) && (value[0].getString() != null)) {
						return (value[0].getString());
					}
				}
				title = this.resultNode.getProperty(PROPERTY_DC_TITLE).getString();
			}
			if (StringUtils.isEmpty(title)) {
				title = this.resultNode.getName();
			}
		} catch (Exception e) {
			log.info("Cannot get the title. Use node name instead.");
		}
		return title;
	}

	public String getUrl() {
		String url=path;
		try {
			String primaryType = this.resultNode.getPrimaryNodeType().getName();
			if (primaryType.equals(NODE_TYPE_CQ_PAGE)) {
			url = path + ".html";
			}
		} catch (Exception e) {
		}
		return url;
	}

	public String getExcerpt() {
		String excerpt="";
		try {
			final Set<String> excerptPropNames = new HashSet<String>();
			excerptPropNames.addAll(Arrays.asList(EXCERPT_PROPERTIES));
			GSSearchResultExcerpt resultExcerpt = GSSearchResultExcerpt.create(this, excerptPropNames, EXCERPT_LENGTH);
			if (resultExcerpt != null) {
				excerpt = resultExcerpt.getText();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		return excerpt;
	}

	public Node getResultNode() {
		return resultNode;
	}

	public Double getScore() {
		return score;
	}

	public String getPath() {
		return path;
	}

	public Row getRow() {
		return row;
	}

}

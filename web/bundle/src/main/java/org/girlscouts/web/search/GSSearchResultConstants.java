package org.girlscouts.web.search;

public interface GSSearchResultConstants {

	public static final String NODE_JCR_CONTENT = "jcr:content";

	public static final String NODE_TYPE_DAM_ASSET = "dam:Asset";
	public static final String NODE_TYPE_CQ_PAGE = "cq:Page";
	public static final String NODE_TYPE_CQ_PSEUDO_PAGE = "cq:PseudoPage";

	public static final String PROPERTY_JCR_TITLE = NODE_JCR_CONTENT + "/jcr:title";
	public static final String PROPERTY_DC_TITLE = NODE_JCR_CONTENT + "/metadata/dc:title";
	public static final String PROPERTY_DC_DESCRIPTION = NODE_JCR_CONTENT + "/metadata/dc:description";
	public static final String PROPERTY_SRCH_DESC = NODE_JCR_CONTENT + "/data/srchdisp";
	public static final String PROPERTY_SEO_TITLE = "seoTitle";

	public static final String MSWORD = "application/msword";
	public static final String APPL_VND = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	public static final String[] EXCERPT_PROPERTIES = { "text", "jcr:description", "jcr:title" };
	public static final int EXCERPT_LENGTH = 150;
}

package org.girlscouts.common.constants;

import javax.jcr.observation.Event;

public interface AssetTrackingConstants {

	public static final int EVENTS = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;

	public static final boolean IS_DEEP = true;

	public static final boolean NO_LOCAL = true;

	public static final String ROOT_PATH = "/content";

	public static final String REFERENCE_PROPERTY = "fileReference";

	public static final String[] DAM_ASSET_PATH = { "/content/dam/girlscouts-shared/" };

	public static final String JCR_CONTENT_NODE = "jcr:content";

	public static final String ASSET_TRACK_NODE = "assetTrackNode";

	public static final String COUNCIL_PAGE_URL = "councilPageUrl";

	public static final String JCR_LAST_MODIFIED_DATE = "jcr:lastModified";

	public static final String ASSET_ADDED_DATE = "assetUsedDate";

	public static final String EVENT_TYPE = "eventType";

	public static final String EVENT_BEFORE_VALUE = "beforeValue";

	public static final String EVENT_AFTER_VALUE = "afterValue";

	public static final String EVENT_TYPE_ADDED = "Asset Added";

	public static final String EVENT_TYPE_REMOVED = "Asset Removed";
	
	public static final String COMPONENT_PATH = "componentPath";

}

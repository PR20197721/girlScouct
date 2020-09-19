package org.girlscouts.common.listeners;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.constants.AssetTrackingConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = EventListener.class)

public class AssetUsageListener implements EventListener {

	private static final Logger logger = LoggerFactory.getLogger(AssetUsageListener.class);

	@Reference
	org.apache.sling.jcr.api.SlingRepository slingRepository;

	@Reference
	private SlingSettingsService settingsService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private ResourceResolver resourceResolver;

	private Session session;

	private ObservationManager observationManager;

	@Activate
	protected void activate(ComponentContext componentContext) {
		logger.info("Activating assetUsageListener observation");

		Map<String, Object> params = new HashMap<>();
		params.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");

		try {
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
			session = resourceResolver.adaptTo(Session.class);
			if (null != session) {
				observationManager = session.getWorkspace().getObservationManager();
				observationManager.addEventListener(this, AssetTrackingConstants.EVENTS,
						AssetTrackingConstants.ROOT_PATH, AssetTrackingConstants.IS_DEEP, null, null,
						AssetTrackingConstants.NO_LOCAL);
			}

		} catch (LoginException | RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void onEvent(EventIterator events) {
		logger.info("Executing AssetUsageListener onEvent()");
		if (!isPublisher()) {
			while (events.hasNext()) {
				Event event = events.nextEvent();

				try {
					String propertyPath = event.getPath();
					String trackingProp = StringUtils.substringAfterLast(propertyPath, "/");
					String resourcePath = StringUtils.substringBeforeLast(propertyPath, "/");
					boolean trackingPropValidation = Arrays.stream(AssetTrackingConstants.TRACKING_PROPERTY)
							.anyMatch(trackingProp::equals);
					if (trackingPropValidation) {
						getEventInfo(event, resourcePath);
					}

				} catch (RepositoryException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 
	 * @param event
	 * @param resourcePath
	 */
	private void getEventInfo(Event event, String resourcePath) {

		String eventType = null;
		Map eventInfoMap;
		List<String> assetsList = null;
		try {
			eventInfoMap = event.getInfo();
			if (event.getType() == Event.PROPERTY_ADDED) {
				String propVal = eventInfoMap.get(AssetTrackingConstants.EVENT_AFTER_VALUE).toString();
				assetsList = extractAssetInfo(propVal);
				if (!assetsList.isEmpty()) {
					eventType = AssetTrackingConstants.EVENT_TYPE_ADDED;
					addTrackingInformation(assetsList, session, resourcePath, eventType);
				}
			} else if (event.getType() == Event.PROPERTY_CHANGED) {
				String propValBefore = eventInfoMap.get(AssetTrackingConstants.EVENT_BEFORE_VALUE).toString();
				String propValAfter = eventInfoMap.get(AssetTrackingConstants.EVENT_AFTER_VALUE).toString();
				List<String> assetListBefore = extractAssetInfo(propValBefore);
				List<String> assetListAfter = extractAssetInfo(propValAfter);
				if (assetListBefore.isEmpty() && !assetListAfter.isEmpty()) {
					eventType = AssetTrackingConstants.EVENT_TYPE_ADDED;
					addTrackingInformation(assetListAfter, session, resourcePath, eventType);
				}

				if (!assetListBefore.isEmpty() && assetListAfter.isEmpty()) {
					eventType = AssetTrackingConstants.EVENT_TYPE_REMOVED;
					addTrackingInformation(assetListBefore, session, resourcePath, eventType);
				}

				if (!assetListBefore.isEmpty() && !assetListAfter.isEmpty()) {
					List<String> before = new ArrayList<>(assetListBefore);
					List<String> after = new ArrayList<>(assetListAfter);
					before.removeAll(after);
					if (!before.isEmpty()) {
						logger.info("Asset removed");
						eventType = AssetTrackingConstants.EVENT_TYPE_REMOVED;
						addTrackingInformation(before, session, resourcePath, eventType);
					}

					assetListAfter.removeAll(assetListBefore);
					if (!assetListAfter.isEmpty()) {
						logger.info("Asset added");
						eventType = AssetTrackingConstants.EVENT_TYPE_ADDED;
						addTrackingInformation(assetListAfter, session, resourcePath, eventType);
					}

				}

			} else if (event.getType() == Event.PROPERTY_REMOVED) {
				String propVal = eventInfoMap.get(AssetTrackingConstants.EVENT_BEFORE_VALUE).toString();
				assetsList = extractAssetInfo(propVal);
				if (!assetsList.isEmpty()) {
					logger.info("Asset removed");
					eventType = AssetTrackingConstants.EVENT_TYPE_REMOVED;
					addTrackingInformation(assetsList, session, resourcePath, eventType);
				}
			}

		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param assetsList
	 * @param session
	 * @param resourcePath
	 * @param eventType
	 */

	private void addTrackingInformation(List<String> assetsList, Session session, String resourcePath,
			String eventType) {

		logger.info("adding tracking information");

		try {
			for (String str : assetsList) {
				if (null != session.getNode(str)) {
					Node assetNode = session.getNode(str);
					if (assetNode.hasNode(AssetTrackingConstants.JCR_CONTENT_NODE)) {
						Node assetJcrContentNode = assetNode.getNode(AssetTrackingConstants.JCR_CONTENT_NODE);

						if (null != assetJcrContentNode) {
							createTrackingNode(assetJcrContentNode, session, resourcePath, eventType);
						}

					} else {
						logger.error("Invalid asset");
					}
				} else {
					logger.error("Invalid asset");
				}
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @param assetJcrContentNode
	 * @param session
	 * @param resourcePath
	 * @param eventType
	 */
	private void createTrackingNode(Node assetJcrContentNode, Session session, String resourcePath, String eventType) {
		Node assetTrackDataNode = null;
		Node councilNode = null;
		Node yearNode = null;
		Node monthNode = null;
		Node dayNode = null;
		String councilVal = resourcePath.split("/")[2];
		String councilPageUrl = StringUtils.substringBefore(resourcePath, AssetTrackingConstants.JCR_CONTENT_NODE);

		try {
			if (assetJcrContentNode.hasNode(AssetTrackingConstants.ASSET_TRACK_NODE)) {
				assetTrackDataNode = assetJcrContentNode.getNode(AssetTrackingConstants.ASSET_TRACK_NODE);
			} else {
				assetJcrContentNode.addNode(AssetTrackingConstants.ASSET_TRACK_NODE);
				assetTrackDataNode = assetJcrContentNode.getNode(AssetTrackingConstants.ASSET_TRACK_NODE);
			}

			if (null != assetTrackDataNode) {
				if (assetTrackDataNode.hasNode(councilVal)) {
					councilNode = assetTrackDataNode.getNode(councilVal);
				} else {
					assetTrackDataNode.addNode(councilVal);
					councilNode = assetTrackDataNode.getNode(councilVal);
				}
				Calendar calendar = Calendar.getInstance();

				String yearVal = String.valueOf(calendar.get(Calendar.YEAR));

				String monthVal = String.valueOf(calendar.get(Calendar.MONTH) + 1);

				String dayVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

				if (councilNode.hasNode(yearVal)) {
					yearNode = councilNode.getNode(yearVal);
				} else {
					councilNode.addNode(yearVal);
					yearNode = councilNode.getNode(yearVal);
				}

				if (yearNode.hasNode(monthVal)) {
					monthNode = yearNode.getNode(monthVal);
				} else {
					yearNode.addNode(monthVal);
					monthNode = yearNode.getNode(monthVal);
				}

				if (monthNode.hasNode(dayVal)) {
					dayNode = monthNode.getNode(dayVal);
				} else {
					monthNode.addNode(dayVal);
					dayNode = monthNode.getNode(dayVal);
				}

				Node dataNode = dayNode.addNode(councilVal + calendar.getTimeInMillis());

				dataNode.setProperty(AssetTrackingConstants.COUNCIL_PAGE_URL, councilPageUrl);
				dataNode.setProperty(AssetTrackingConstants.ASSET_ADDED_DATE, calendar);
				dataNode.setProperty(AssetTrackingConstants.EVENT_TYPE, eventType);
				dataNode.setProperty(AssetTrackingConstants.COMPONENT_PATH, resourcePath);

			}
			session.save();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @param propVal
	 * @return
	 */
	private List<String> extractAssetInfo(String propVal) {

		List<String> assetPathList = new ArrayList<>();
		Pattern patteren = Pattern.compile(
				"(?:\\/content\\/dam\\/girlscouts-shared\\/.*.(?:tif|tiff|bmp|jpg|jpeg|gif|png|TIF|TIFF|BMP|JPG|JPEG|GIF|PNG))");
		Matcher matcher = patteren.matcher(propVal);
		while (matcher.find()) {
			assetPathList.add(matcher.group());
		}
		return assetPathList;
	}

	/**
	 * 
	 * @return
	 */
	private boolean isPublisher() {
		return settingsService.getRunModes().contains("publish");
	}

	/**
	 * 
	 */
	@Deactivate
	public void deactivate() {
		logger.info("assetUsageListener Deactivated");
		try {
			if (observationManager != null) {
				observationManager.removeEventListener(this);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != session)
				session.logout();
			if (null != resourceResolver)
				resourceResolver.close();
		}

	}

}

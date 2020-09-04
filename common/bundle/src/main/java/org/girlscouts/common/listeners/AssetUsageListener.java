package org.girlscouts.common.listeners;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
		String eventType;
		if (!isPublisher()) {
			try {
				while (events.hasNext()) {
					Event event = events.nextEvent();
					Map eventInfoMap = event.getInfo();
					String propertyPath = event.getPath();
					String fileRefStr = StringUtils.substringAfterLast(propertyPath, "/");
					String resourcePath = StringUtils.substringBeforeLast(propertyPath, "/");
					if (event.getType() == Event.PROPERTY_ADDED && StringUtils.isNotBlank(fileRefStr)
							&& StringUtils.equals(fileRefStr, AssetTrackingConstants.REFERENCE_PROPERTY)) {
						eventType = AssetTrackingConstants.EVENT_TYPE_ADDED;
						String assetPath = eventInfoMap.get(AssetTrackingConstants.EVENT_AFTER_VALUE).toString();
						boolean assetPathValidation = assetPathCheck(assetPath, AssetTrackingConstants.DAM_ASSET_PATH);
						if (StringUtils.isNotBlank(assetPath) && assetPathValidation) {
							updateTrackingInformation(assetPath, session, resourcePath, eventType);
						}

					} else if (event.getType() == Event.PROPERTY_CHANGED && StringUtils.isNotBlank(fileRefStr)
							&& StringUtils.equals(fileRefStr, AssetTrackingConstants.REFERENCE_PROPERTY)) {
						String assetPathAfter = eventInfoMap.get(AssetTrackingConstants.EVENT_AFTER_VALUE).toString();
						String assetPathBefore = eventInfoMap.get(AssetTrackingConstants.EVENT_BEFORE_VALUE).toString();
						boolean assetPathBeforValidation = assetPathCheck(assetPathBefore,
								AssetTrackingConstants.DAM_ASSET_PATH);
						boolean assetPathAfterValidation = assetPathCheck(assetPathAfter,
								AssetTrackingConstants.DAM_ASSET_PATH);
						if (StringUtils.isNotBlank(assetPathAfter) && assetPathAfterValidation) {
							eventType = AssetTrackingConstants.EVENT_TYPE_ADDED;
							updateTrackingInformation(assetPathAfter, session, resourcePath, eventType);
						}

						if (StringUtils.isNotBlank(assetPathBefore) && assetPathBeforValidation) {
							eventType = AssetTrackingConstants.EVENT_TYPE_REMOVED;
							updateTrackingInformation(assetPathBefore, session, resourcePath, eventType);
						}

					} else if (event.getType() == Event.PROPERTY_REMOVED && StringUtils.isNotBlank(fileRefStr)
							&& StringUtils.equals(fileRefStr, AssetTrackingConstants.REFERENCE_PROPERTY)) {
						String assetPathBefore = eventInfoMap.get(AssetTrackingConstants.EVENT_BEFORE_VALUE).toString();
						boolean assetPathValidation = assetPathCheck(assetPathBefore,
								AssetTrackingConstants.DAM_ASSET_PATH);
						if (StringUtils.isNotBlank(assetPathBefore) && assetPathValidation) {
							eventType = AssetTrackingConstants.EVENT_TYPE_REMOVED;
							updateTrackingInformation(assetPathBefore, session, resourcePath, eventType);
						}

					}
				}

			} catch (RepositoryException e) {
				logger.error(e.getMessage(), e);
			}

		}
	}

	private void updateTrackingInformation(String assetPath, Session session, String resourcePath, String eventType) {

		try {
			if (null != session.getNode(assetPath)) {
				Node assetNode = session.getNode(assetPath);

				if (assetNode.hasNode(AssetTrackingConstants.JCR_CONTENT_NODE)) {
					Node assetJcrContentNode = assetNode.getNode(AssetTrackingConstants.JCR_CONTENT_NODE);

					if (null != assetJcrContentNode) {
						addTrackingInformation(assetJcrContentNode, session, resourcePath, eventType);
					}

				}
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void addTrackingInformation(Node assetJcrContentNode, Session session, String resourcePath,
			String eventType) {
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

	private Boolean assetPathCheck(String assetPath, String[] damAssetPath) {
		boolean result = false;

		if (Stream.of(damAssetPath).anyMatch(assetPath::startsWith)) {
			result = true;
		}
		return result;
	}

	private boolean isPublisher() {
		return settingsService.getRunModes().contains("publish");
	}

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

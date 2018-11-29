package org.girlscouts.common.osgi.component.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.common.osgi.component.GirlscoutsImagePathProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import com.day.cq.wcm.foundation.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;

@Component(service = {
		GirlscoutsImagePathProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsImagePathProviderImpl")
public class GirlscoutsImagePathProviderImpl implements GirlscoutsImagePathProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsImagePathProviderImpl.class);

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Activate
	private void activate() {
		log.info("Girl Scouts Image Path Provider Activated.");
	}

	@Override
	public String getImagePath(String path, String rendition) {
		return formatPath(path, rendition);
	}

	@Override
	public String getImagePath(Asset asset, String rendition) {
		return formatPath(asset.getPath(), rendition);
	}

	@Override
	public String getImagePathByLocation(Resource resource) {
		log.debug("Choosing image based on location for resource: " + resource);
		String pathToImage = "";
		try {
			pathToImage = String.valueOf(resource.adaptTo(ValueMap.class).get("fileReference"));
			// String path = resource.getPath();
			// if (path.indexOf("jcr:content/content/top/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.top");
			// } else if (path.indexOf("jcr:content/content/left/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.left");
			// } else if (path.indexOf("jcr:content/content/right/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.right");
			// }
		} catch (Exception e) {
			log.error("Girl Scouts Image Path Provider encountered error:", e);
		}
		return getImagePath(pathToImage, "cq5dam.web.1280.1280");
	}

	@Override
	public String getImagePathByLocation(Image image) {
		log.debug("Choosing image based on location for image: " + image);
		String pathToImage = "";
		try {
			pathToImage = image.getFileReference();
			if (pathToImage.isEmpty()) {
				pathToImage = image.getFileNodePath() + ".img" + image.getExtension() + image.getSuffix();
			}
			// String path = image.getParent().getPath();
			// if (path.indexOf("jcr:content/content/top/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.top");
			// } else if (path.indexOf("jcr:content/content/left/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.left");
			// } else if (path.indexOf("jcr:content/content/right/par") != -1) {
			// return getImagePath(pathToImage, "cq5dam.npd.right");
			// }
		} catch (Exception e) {
			log.error("Girl Scouts Image Path Provider encountered error:", e);
		}
		return getImagePath(pathToImage, "cq5dam.web.1280.1280");
	}

	private String formatPath(String path, String rendition) {
		String imagePathPattern = ".transform/{rendition}/img{extension}";
		log.debug("Generating rendition=" + rendition + " path for: " + path);
		String pathToImage = path;
		try {
			if (!StringUtils.isBlank(path)) {
				String extension = "jpg";
				if (path.indexOf(".") != -1) {
					extension = path.substring(path.lastIndexOf("."));
				}
				imagePathPattern = imagePathPattern.replace("{extension}", extension);
				pathToImage = path + imagePathPattern.replace("{rendition}", rendition);
			} else {
				return "";
			}

		} catch (Exception e) {
			log.error("Girl Scouts Image Path Provider encountered error:", e);
		}
		log.debug("Generated pathToImage=" + pathToImage);
		return pathToImage;
	}
	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts Image Path Provider Deactivated.");
	}
}

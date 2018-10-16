package org.girlscouts.common.osgi.component.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.common.osgi.component.GirlscoutsImagePathProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;

@Component(service = {
		GirlscoutsImagePathProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsImagePathProviderImpl")
public class GirlscoutsImagePathProviderImpl implements GirlscoutsImagePathProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsImagePathProviderImpl.class);
	String imagePathPattern = ".transform/{rendition}/img.png";

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Activate
	private void activate() {
		log.info("Girl Scouts Image Path Provider Activated.");
	}

	@Override
	public String getImagePath(String path, String rendition) {
		String pathToImage = "";
		try{
			pathToImage = path + imagePathPattern.replace("{rendition}", rendition);
		}catch(Exception e){
			log.error("Girl Scouts Image Path Provider encountered error:", e);
		}
		return pathToImage;
	}

	@Override
	public String getImagePath(Asset asset, String rendition) {
		String pathToImage = "";
		try {
			pathToImage = asset.getPath() + imagePathPattern.replace("{rendition}", rendition);
		} catch (Exception e) {
			log.error("Girl Scouts Image Path Provider encountered error:", e);
		}
		return pathToImage;
	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts Image Path Provider Deactivated.");
	}
}

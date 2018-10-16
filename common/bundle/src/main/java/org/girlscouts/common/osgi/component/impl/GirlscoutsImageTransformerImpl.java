package org.girlscouts.common.osgi.component.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.common.osgi.component.GirlscoutsImageTransformer;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;

@Component(service = {
		GirlscoutsImageTransformer.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsImageTransformerImpl")
public class GirlscoutsImageTransformerImpl implements GirlscoutsImageTransformer {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsImageTransformerImpl.class);
	private Map<String, Object> serviceParams;
	String transformPath = ".transform/{}/img.png";

	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Activate
	private void activate() {
		this.serviceParams = new HashMap<String, Object>();
		this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-service");
		log.info("Girl Scouts Image Transformer Activated.");
	}

	@Override
	public String getRendition(String path, String rendition) {
		return path + ".transform/cq5dam.thumbnail.319.319.img.png";
	}

	@Override
	public String getRendition(Asset asset, String rendition) {
		return asset.getPath() + ".transform/cq5dam.thumbnail.319.319.img.png";
	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts Image Transformer Deactivated.");
	}
}

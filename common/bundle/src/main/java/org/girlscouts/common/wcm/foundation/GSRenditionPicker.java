package org.girlscouts.common.wcm.foundation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.jackrabbit.util.Text;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;

public class GSRenditionPicker implements RenditionPicker {

	// Allow original to be selected
	private static final String ORIGINAL = "original";
	private static final String DAM_ORIGINAL = "cq5dam.npd.original";
	
	//GSUSA Renditions
	private static final String TOP = "cq5dam.npd.top";
	private static final String RIGHT = "cq5dam.npd.right";
	private static final String MIDDLE = "cq5dam.npd.middle";
	private static final String LEFT = "cq5dam.npd.left";
	private static final String HERO = "cq5dam.npd.hero";
	
	//GSUSA Retina Renditions
	private static final String TOP2X = "cq5dam.npd.top@2x";
	private static final String RIGHT2X = "cq5dam.npd.right@2x";
	private static final String MIDDLE2X = "cq5dam.npd.middle@2x";
	private static final String LEFT2X = "cq5dam.npd.left@2x";
	private static final String HERO2X = "cq5dam.npd.hero@2x";
	
	//Councils Renditions
	private static final String R48X48 = "cq5dam.thumbnail.48.48";
	private static final String R140X100 = "cq5dam.thumbnail.140.100";
	private static final String R319X319 = "cq5dam.thumbnail.319.319";
	private static final String R120X80 = "cq5dam.web.120.80";
	private static final String R240X240 = "cq5dam.web.240.240";
	private static final String R400X400 = "cq5dam.web.400.400";
	private static final String R520X520 = "cq5dam.web.520.520";
	private static final String R1280X1280 = "cq5dam.web.1280.1280";

	private static final List<String> COUNCIL_RENDITIONS = Arrays.asList(DAM_ORIGINAL, ORIGINAL, R48X48, R140X100, R319X319, R120X80, R240X240, R400X400, R520X520, R1280X1280);
	
	 String[] targetRenditions;
	 public GSRenditionPicker(String... targetRenditions) {
	        this.targetRenditions = targetRenditions;
	 }
		
	private String getCouncilAnalog(String gsusaRenditionName){

	 	Optional<String> matchedCouncilRendition = COUNCIL_RENDITIONS
				.stream()
				.filter(gsusaRenditionName::equals)
				.findAny();

	 	if(matchedCouncilRendition.isPresent()){
	 		return matchedCouncilRendition.get();
		}
		
		if(TOP.equals(gsusaRenditionName))
			return R1280X1280;
				
		if(RIGHT.equals(gsusaRenditionName))
			return R520X520;

		if(MIDDLE.equals(gsusaRenditionName))
			return R520X520;
					
		if(LEFT.equals(gsusaRenditionName))
			return R400X400;
					
		if(HERO.equals(gsusaRenditionName))
			return R520X520;
					
		if(TOP2X.equals(gsusaRenditionName))
			return R1280X1280;
				
		if(RIGHT2X.equals(gsusaRenditionName))
			return R520X520;
					
		if(MIDDLE2X.equals(gsusaRenditionName))
			return R520X520;
					
		if(LEFT2X.equals(gsusaRenditionName))
			return R520X520;
					
		if(HERO2X.equals(gsusaRenditionName))
			return R520X520;
							
		return R520X520;
	}

	public Rendition getRendition(Asset asset) {
		List<Rendition> renditions = asset.getRenditions();
        for (String targetRendition : targetRenditions) {
	        	String councilRendition = this.getCouncilAnalog(targetRendition);
	        	for (Rendition rendition: renditions) {
                if (Text.getName(rendition.getPath()).startsWith(councilRendition)) {
					return rendition;
                }
            }
        }
        return asset.getOriginal();
	}

}

package org.girlscouts.web.wcm.foundation;


import java.util.List;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;

public class GSRenditionPicker implements RenditionPicker {

	public Rendition getRendition(Asset asset)
	{
		List<Rendition> renditions = asset.getRenditions();
        
        for (Rendition rendition: renditions) {
        	
        	if (rendition.getName().startsWith("cq5dam.web.1280.1280")) {
        		return rendition;
        	}
        }
       
		
		return  asset.getOriginal();
		
		
	}

}

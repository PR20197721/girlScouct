package org.girlscouts.common.wcm.foundation.gsusa;

import java.util.List;

import org.apache.jackrabbit.util.Text;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;

public class GSRenditionPicker implements RenditionPicker {

    String[] targetRenditions;
    public GSRenditionPicker(String... targetRenditions) {
        this.targetRenditions = targetRenditions;
    }

    public Rendition getRendition(Asset asset) {
        List<Rendition> renditions = asset.getRenditions();
        for (String targetRendition : targetRenditions) {
        	for (Rendition rendition: renditions) {
                if (rendition.getName().indexOf("@2x") == -1 && Text.getName(rendition.getPath()).startsWith(targetRendition)) {
					return rendition;
                }
            }
        }
        // 2. return current rendition
		return asset.getOriginal();
    }

}

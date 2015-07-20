package org.girlscouts.web.gsusa.workflow;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.OrientationUtil;
import com.day.cq.dam.commons.util.WebEnabledImageCreator;
import com.day.image.Layer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSWebEnabledImageCreator extends WebEnabledImageCreator {

  private static final Logger log = LoggerFactory.getLogger(GSWebEnabledImageCreator.class);
  private static final String WEB_SPECIFIER = "web";
  private Asset asset;
  private MimeTypeService mimeTypeService;

  public GSWebEnabledImageCreator(Asset asset, MimeTypeService mimeTypeService) {
	  super(asset, mimeTypeService);
  }

  public void create(BufferedImage image, String defaultMimetype, String dimensions, String keepFormat, String qualityStr, boolean force) throws RepositoryException, IOException {
    int maxWidth = getDimension(dimensions)[0].intValue();
    int maxHeight = getDimension(dimensions)[1].intValue();
    String oriMimeType = getMimeType(this.asset);
    String mimetype = (StringUtils.isNotBlank(oriMimeType)) && (keepFormat.contains(oriMimeType)) ? oriMimeType : defaultMimetype;
    double quality = mimetype.equals("image/gif") ? getQuality(255.0D, qualityStr) : getQuality(1.0D, qualityStr);
    Layer layer = createImage(image, maxWidth, maxHeight);

    if (OrientationUtil.hasOrientationMetadata(this.asset)) {
      OrientationUtil.adjustOrientation(this.asset, layer);
    }

    String renditionName = "cq5dam.npd." + maxWidth + "." + maxHeight + "." + getExtension(mimetype);

    if (StringUtils.contains(keepFormat, oriMimeType)) {
      if ((image.getHeight() == layer.getHeight()) && (image.getWidth() == layer.getWidth()) && (!force)) {
        InputStream oriIs = null;
        try {
          oriIs = ((Node)this.asset.getOriginal().adaptTo(Node.class)).getProperty("jcr:content/jcr:data").getBinary().getStream();
          this.asset.addRendition(renditionName, oriIs, mimetype);
        } finally {
          IOUtils.closeQuietly(oriIs);
        }
      } else {
        saveImage(this.asset, layer, mimetype, quality, renditionName);
      }
    } else
      saveImage(this.asset, layer, mimetype, quality, renditionName);
  }
}

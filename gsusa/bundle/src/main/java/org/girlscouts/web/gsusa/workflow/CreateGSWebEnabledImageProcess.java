package org.girlscouts.web.gsusa.workflow;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.cache.BufferedImageCache;
import com.day.cq.dam.api.cache.BufferedImageCache.Entry;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.commons.process.AbstractAssetWorkflowProcess;
import com.day.cq.dam.commons.util.MemoryUtil;
import com.day.cq.dam.commons.util.WebEnabledImageCreator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.metadata.MetaDataMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype=false)
@Service
@Property(name="process.label", value={"Create Web Enabled Girl Scouts Image"})
public class CreateGSWebEnabledImageProcess extends AbstractAssetWorkflowProcess{
  private static final Logger log = LoggerFactory.getLogger(CreateGSWebEnabledImageProcess.class);
  private static final int MAX_TRIALS = 100;

  @Reference(policy=ReferencePolicy.STATIC)
  private BufferedImageCache imageCache;

  public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaData)
    throws WorkflowException
  {
    String[] args = buildArguments(metaData);
    try
    {
      Asset asset = getAssetFromPayload(workItem, workflowSession.getSession());

      if ((null != asset) && (!doIgnore(args, asset))) {
        asset.setBatchMode(true);

        String dimensions = getValuesFromArgs("dimension", args).size() > 0 ? (String)getValuesFromArgs("dimension", args).get(0) : null;

        String mimetype = getValuesFromArgs("mimetype", args).size() > 0 ? (String)getValuesFromArgs("mimetype", args).get(0) : "image/png";

        String keepFormat = getValuesFromArgs("keepFormatList", args).size() > 0 ? (String)getValuesFromArgs("keepFormatList", args).get(0) : "image/pjpeg,image/jpeg,image/jpg,image/gif,image/png,image/x-png";

        String qualityStr = getValuesFromArgs("quality", args).size() > 0 ? (String)getValuesFromArgs("quality", args).get(0) : "60";

        if (MemoryUtil.hasEnoughSystemMemory(asset))
        {
          AssetHandler handler = getAssetHandler(asset.getMimeType());
          WebEnabledImageCreator creator = new WebEnabledImageCreator(asset, this.mimeTypeService);
          Rendition original = asset.getOriginal();

          BufferedImageCache.Entry image = null;
          long trials = 100L;
          try {
            while ((image == null) && (trials > 0L)) {
              trials -= 1L;
              try {
                image = this.imageCache.getImage(original, handler);
              } catch (IOException e) {
                if (((e instanceof IIOException)) && (e.getMessage().contains("Not enough memory"))) {
                  log.debug("execute: insufficient memory, reloading image. Free mem [{}]. Asset [{}].", Long.valueOf(Runtime.getRuntime().freeMemory()), asset.getPath());

                  Thread.sleep((long) (5000d * (Math.random() + 2d)));
                  continue;
                }
                log.debug("execute: error while loading image for [{}]: ", asset.getPath(), e);
                throw new IOException(e.getMessage());
              }

              if (image != null)
              {
                try
                {
                  creator.create(image.getImage(), mimetype, dimensions, keepFormat, qualityStr, true);
                } catch (IOException e) {
                  if (((e instanceof IIOException)) && (e.getMessage().contains("Not enough memory"))) {
                    if (log.isDebugEnabled()) {
                      log.debug("execute: insufficient memory, reloading image. Free mem [{}]. Asset [{}].", Long.valueOf(Runtime.getRuntime().freeMemory()), asset.getPath());
                    }

                    image.release();
                    image = null;

                    Thread.sleep((long) (5000d * (Math.random() + 2d)));
                  } else {
                    log.debug("execute: error while loading web enabled image for [{}]: ", asset.getPath(), e);

                    throw new IOException(e.getMessage());
                  }
                }
              }
              else log.warn("execute: cannot extract image from [{}].", asset.getPath());
            }
          }
          finally
          {
            if (image != null) {
              image.release();
            }
          }
          if (trials == 0L) {
            log.warn("execute: failed creating web enabled image, insufficient memory even after [{}] trials for [{}].", Integer.valueOf(100), asset.getPath());
          }
        }
        else
        {
          log.warn("execute: failed loading image,  insufficient memory. Increase heap size up to [{}bytes] for asset [{}].", Long.valueOf(MemoryUtil.suggestMaxHeapSize(asset)), asset.getPath());
        }

      }
      else
      {
        if (asset == null) {
          String wfPayload = workItem.getWorkflowData().getPayload().toString();
          String message = "execute: cannot create web enabled image, asset [{" + wfPayload + "}] in payload doesn't exist for workflow [{" + workItem.getId() + "}].";
          throw new WorkflowException(message);
        }
        log.debug("execute: asset [{}] exists, but configured to ignore.", asset.getPath());
      }
    }
    catch (Exception e) {
      throw new WorkflowException(e);
    }
  }

  private boolean doIgnore(String[] args, Asset asset)
  {
    String mimeType = asset.getMimeType();
    if (mimeType == null)
    {
      log.debug("doIgnore: no mimetype available for asset [{}].", asset.getPath());
      return true;
    }
    List<String> values = getValuesFromArgs("skip", args);
    for (String val : values)
    {
      if (mimeType.matches(val)) {
        return true;
      }
    }
    return false;
  }

  public String[] buildArguments(MetaDataMap metaData)
  {
    String processArgs = (String)metaData.get(Arguments.PROCESS_ARGS.name(), String.class);
    if ((processArgs != null) && (!processArgs.equals(""))) {
      return processArgs.split(",");
    }

    List arguments = new ArrayList();

    String width = (String)metaData.get(Arguments.WIDTH.name(), String.class);
    String height = (String)metaData.get(Arguments.HEIGHT.name(), String.class);
    if ((StringUtils.isNotBlank(width)) && (StringUtils.isNotBlank(height))) {
      StringBuilder builder = new StringBuilder();
      builder.append(Arguments.DIMENSION.getArgumentPrefix()).append(width).append(":").append(height);
      arguments.add(builder.toString());
    }

    String value = (String)metaData.get(Arguments.QUALITY.name(), String.class);
    if (StringUtils.isNotBlank(value)) {
      StringBuilder builder = new StringBuilder();
      builder.append(Arguments.QUALITY.getArgumentPrefix()).append(value);
      arguments.add(builder.toString());
    }

    value = (String)metaData.get(Arguments.MIME_TYPE.name(), String.class);
    if (StringUtils.isNotBlank(value)) {
      StringBuilder builder = new StringBuilder();
      builder.append(Arguments.MIME_TYPE.getArgumentPrefix()).append(value);
      arguments.add(builder.toString());
    }

    String[] skipValues = (String[])metaData.get(Arguments.SKIP.name(), String[].class);
    if (skipValues != null) {
      for (String skipValue : skipValues) {
        StringBuilder builder = new StringBuilder();
        builder.append(Arguments.SKIP.getArgumentPrefix()).append(skipValue);
        arguments.add(builder.toString());
      }
    }

    String[] keepValues = (String[])metaData.get(Arguments.KEEP_FORMAT_LIST.name(), String[].class);
    if (keepValues != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(Arguments.KEEP_FORMAT_LIST.getArgumentPrefix());
      for (int i = 0; i < keepValues.length; i++) {
        builder.append(keepValues[i]);
        if (i < keepValues.length - 1) {
          builder.append(",");
        }
      }
      arguments.add(builder.toString());
    }
    return (String[])arguments.toArray(new String[arguments.size()]);
  }

  protected void bindImageCache(BufferedImageCache paramBufferedImageCache)
  {
    this.imageCache = paramBufferedImageCache;
  }

  protected void unbindImageCache(BufferedImageCache paramBufferedImageCache)
  {
    if (this.imageCache == paramBufferedImageCache)
      this.imageCache = null;
  }


  public enum Arguments {
	  PROCESS_ARGS("PROCESS_ARGS"), DIMENSION("dimension"), WIDTH("width"), HEIGHT("height"), QUALITY("quality"), MIME_TYPE("mimetype"), 
	  SKIP("skip"), KEEP_FORMAT_LIST("keepFormatList");

	  private String argumentName;

	  private Arguments(String argumentName) {
	    this.argumentName = argumentName;
	  }

	  public String getArgumentName() {
	    return this.argumentName;
	  }

	  public String getArgumentPrefix() {
	    return this.argumentName + ":";
	  }
	}

}



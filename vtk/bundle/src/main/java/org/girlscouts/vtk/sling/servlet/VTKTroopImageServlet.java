package org.girlscouts.vtk.sling.servlet;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.image.Layer;
import org.apache.commons.codec.binary.Base64;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.http.HttpSession;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@SlingServlet(label = "Girl Scouts VTK Troop Image servlet", description = "Uploads troop image to repository", paths = {}, methods = {"POST"}, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = {"girlscouts/servlets/troopImage"}, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = {"html", "htm"}  // Ignored if paths is set
)
public class VTKTroopImageServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(VTKTroopImageServlet.class);
    @Reference
    public ResourceResolverFactory resourceFactory;
    @Reference
    public VtkUtil vtkUtil;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        Troop selectedTroop = VtkUtil.getTroop(session);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        ResourceResolver vtkServiceResolver = null;
        try {
            vtkServiceResolver = resourceFactory.getServiceResourceResolver(paramMap);
            if (request.getParameter("isRmTroopImg") != null) {
                Session __session = null;
                String troopPhotoUrl = "";
                try {
                    __session = vtkServiceResolver.adaptTo(Session.class);
                    troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data" + VtkUtil.getCurrentGSYear() + "/" + selectedTroop.getCouncilCode() + "/" + selectedTroop.getTroopId() + "/imgLib/troop_pic.png";
                    __session.removeItem(troopPhotoUrl);
                    __session.save();
                } catch (Exception e) {
                    log.error("Error Occurred deleting troop image "+troopPhotoUrl, e);
                }
            }else {
                int x1 = -1, x2 = -1, y1 = -1, y2 = -1, width = -1, height = -1;
                double maxW = 960;
                int[] coords = new int[0];
                if (request.getParameter("coords") != null) {
                    String coordString = request.getParameter("coords").toString();
                    String[] nums = coordString.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                    coords = new int[nums.length];
                    for (int i = 0; i < nums.length; i++) {
                        try {
                            coords[i] = Integer.parseInt(nums[i]);
                        } catch (NumberFormatException nfe) {
                            log.error("Exception occured:", nfe);
                        }
                    }
                }
                if (coords.length == 7) {
                    x1 = coords[0];
                    y1 = coords[1];
                    x2 = coords[2];
                    y2 = coords[3];
                    width = coords[4];
                    height = coords[5];
                    maxW = coords[6];
                }
                String imgData = request.getParameter("imageData");
                imgData = imgData.replace("data:image/png;base64,", "");
                byte[] decoded = Base64.decodeBase64(imgData);
                if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0 && width >= 0 && height >= 0) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                    BufferedImage inputImage = ImageIO.read(bais);
                    String formatName = "PNG";
                    Layer layer = new Layer(inputImage);
                    int smallerX = Math.min(x1, x2);
                    int smallerY = Math.min(y1, y2);
                    double ratio = 1;
                    if (layer.getWidth() > maxW) {
                        ratio = layer.getWidth() / maxW;
                    }
                    Rectangle2D rect = new Rectangle2D.Double(smallerX * ratio, smallerY * ratio, width * ratio, height * ratio);
                    layer.crop(rect);
                    inputImage = layer.getImage();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    ImageIO.write(inputImage, formatName, outStream);
                    decoded = outStream.toByteArray();
                }
                //creates folder path if it doesn't exist yet
                String path = "/content/dam/girlscouts-vtk/troop-data" + vtkUtil.getCurrentGSYear() + "/" + selectedTroop.getCouncilCode() + "/" + selectedTroop.getTroopId() + "/imgLib";
                String pathWithFile = path + "/troop_pic.png/jcr:content";
                Session __session = vtkServiceResolver.adaptTo(Session.class);
                Node baseNode = JcrUtil.createPath(path, "nt:folder", __session);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(decoded);
                ValueFactory vf = __session.getValueFactory();
                Binary bin = vf.createBinary(byteStream);
                //for some reason, the data property can't be updated, just remade
                try {
                    __session.removeItem(path + "/troop_pic.png");
                    __session.save();
                } catch (Exception e) {
                    log.error("Exception occured:", e);
                }
                //creates file and jcr:content nodes if they don't exist yet
                Node jcrNode = JcrUtil.createPath(pathWithFile, false, "nt:file", "nt:resource", __session, false);
                jcrNode.setProperty("jcr:data", bin);
                jcrNode.setProperty("jcr:mimeType", "image/png");
                __session.save();
            }
        } catch (Exception e) {
            log.error("Failed to upload image for this troop: " + selectedTroop.getPath() + "/yearPlan" + " : ", e);
        } finally {
            try {
                if (vtkServiceResolver != null) {
                    vtkServiceResolver.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            Troop selectedTroop = VtkUtil.getTroop(session);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
            ResourceResolver adminResolver = null;
            Session __session = null;
            String troopPhotoUrl = "";
            try {
                adminResolver = resourceFactory.getServiceResourceResolver(paramMap);
                troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data" + VtkUtil.getCurrentGSYear() + "/" + selectedTroop.getCouncilCode() + "/" + selectedTroop.getTroopId() + "/imgLib/troop_pic.png";
                __session.removeItem(troopPhotoUrl);
                __session.save();
            } catch (Exception e) {
                log.error("Failed to delete image : " + troopPhotoUrl, e);
            } finally {
                try {
                    if (adminResolver != null) {
                        adminResolver.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
        }
    }

    @Override
    public boolean accepts(SlingHttpServletRequest request) {
        HttpSession session = request.getSession();
        if ((ApiConfig) session.getAttribute(ApiConfig.class.getName()) != null) {
            return true;
        } else {
            return false;
        }

    }
}
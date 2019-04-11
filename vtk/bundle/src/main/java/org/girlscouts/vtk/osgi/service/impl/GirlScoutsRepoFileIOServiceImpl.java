package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlScoutsRepoFileIOService.class }, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsRepoFileIOServiceImpl")
public class GirlScoutsRepoFileIOServiceImpl extends BasicGirlScoutsService implements GirlScoutsRepoFileIOService {

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    private static Logger log = LoggerFactory.getLogger(GirlScoutsRepoFileIOServiceImpl.class);

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        log.info(this.getClass().getName()+" activated.");
    }
    @Override
    public String readFile(String path) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            InputStream is = session.getNode(path + "/jcr:content").getProperty("jcr:data").getBinary().getStream();
            String content = IOUtils.toString(new InputStreamReader(is, StandardCharsets.UTF_8));
            return content;
        } catch (Exception e) {
            log.error("Cannot get file node: " + path + " due to ", e);
        }finally {
            try {
                if(rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return null;
    }

    @Override
    public void writeFile(String path, String content) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            String nodeName = path + "/jcr:content";
            if (!session.nodeExists(path)) {
                JcrUtil.createPath(path, "nt:unstructured", session);
                session.save();
            }
            if (!session.nodeExists(nodeName)) {
                JcrUtil.createPath(nodeName, false, "nt:file", "nt:resource", session, false);
            }
            InputStream is = IOUtils.toInputStream(content);
            Node node = session.getNode(path + "/jcr:content");
            ValueFactory factory = session.getValueFactory();
            Binary binary = factory.createBinary(is);
            node.setProperty("jcr:data", binary);
            session.save();
        } catch (Exception e) {
            log.error("Cannot get file node: " + path + " due to RepositoryException");
        }finally {
            try {
                if(rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
    }
}

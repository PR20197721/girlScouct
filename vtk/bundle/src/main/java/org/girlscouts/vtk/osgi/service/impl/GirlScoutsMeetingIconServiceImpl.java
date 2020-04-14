package org.girlscouts.vtk.osgi.service.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.osgi.cache.VTKMeetingIconCache;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingIconService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlScoutsMeetingIconService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMeetingIconServiceImpl")
public class GirlScoutsMeetingIconServiceImpl implements GirlScoutsMeetingIconService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private VTKMeetingIconCache iconCache;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public String getIconPathById(String id) {
        if(id != null){
            id = id.trim();
            if(iconCache.contains(id)){
                return iconCache.read(id);
            }
            String meetingIconPath = searchIconByTags(id);
            if(meetingIconPath != null){
                iconCache.write(id, meetingIconPath);
            }
            return meetingIconPath;
        }else{
            return null;
        }
    }

    private String searchIconByTags(String id) {
        String tag = generateTagName(id);
        if (tag != null && !tag.trim().equals("")) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                QueryManager qm = session.getWorkspace().getQueryManager();
                String sql = buildIconsQuery(tag);
                Query q = qm.createQuery(sql, Query.JCR_SQL2);
                log.debug("Executing JCR query: " + sql);
                QueryResult result = q.execute();
                RowIterator it = result.getRows();
                while(it.hasNext()){
                    Row asset = it.nextRow();
                    Node node = asset.getNode();
                    if(node.hasNode("/jcr:content/metadata")){
                        Node metadata  = node.getNode("/jcr:content/metadata");
                        if(metadata.hasProperty("dc:format")){
                            String format = metadata.getProperty("dc:format").getString();
                            if(format.startsWith("image")){
                                return asset.getPath();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        }
        return null;
    }

    private String generateTagName(String id) {
        if(id != null){
            return "vtkcontent:meetings/brownie/"+id.trim().toLowerCase();
        }
        return null;
    }


    private String buildIconsQuery(String tag) {
        if(tag != null){
            tag = tag.trim();
        }
        String sql = "SELECT s.* FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-vtk/meeting-icons]) AND s.[jcr:content/metadata/cq:tags] LIKE '\" + tag + \"'";
        return sql;
    }


}

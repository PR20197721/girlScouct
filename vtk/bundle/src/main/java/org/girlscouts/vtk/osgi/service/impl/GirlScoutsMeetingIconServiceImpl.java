package org.girlscouts.vtk.osgi.service.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
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
import java.io.InputStream;
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

    public InputStream getIconByMeetingId(String id) {
        if (id != null) {
            id = id.trim();
            if (iconCache.contains(id)) {
                String meetingIconPath = iconCache.read(id);
                return getInputStream(meetingIconPath);
            }
            String meetingIconPath = searchIconByTags(id);
            if (meetingIconPath != null) {
                iconCache.write(id, meetingIconPath);
                return getInputStream(meetingIconPath);
            }
        }
        return null;
    }

    private InputStream getInputStream(String imagePath) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            try {
                Resource assetRes = rr.resolve(imagePath);
                if (assetRes != null) {
                    Asset asset = assetRes.adaptTo(Asset.class);
                    Rendition original = asset.getOriginal();
                    return original.getStream();
                }
            } catch (Exception e2) {
                log.error("Exception thrown loading {}", imagePath, e2);
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
        return null;
    }

    private String searchIconByTags(String id) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String tag = generateTagName(id, rr);
            if (tag != null && !tag.trim().equals("")) {
                Session session = rr.adaptTo(Session.class);
                QueryManager qm = session.getWorkspace().getQueryManager();
                String sql = buildIconsQuery(tag);
                Query q = qm.createQuery(sql, Query.JCR_SQL2);
                log.debug("Executing JCR query: " + sql);
                QueryResult result = q.execute();
                RowIterator it = result.getRows();
                while (it.hasNext()) {
                    Row asset = it.nextRow();
                    Node node = asset.getNode();
                    if (node.hasNode("jcr:content/metadata")) {
                        Node metadata = node.getNode("jcr:content/metadata");
                        if (metadata.hasProperty("dc:format")) {
                            String format = metadata.getProperty("dc:format").getString();
                            if (format.startsWith("image")) {
                                return asset.getPath();
                            }
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
        return null;
    }

    private String generateTagName(String id, ResourceResolver rr) {
        if (id != null) {
            id = id.trim().toLowerCase();
            TagManager tagManager = rr.adaptTo(TagManager.class);
            String gradeLevel = null;
            Character levelCode = id.charAt(0);
            switch (levelCode) {
                case 'd':
                    gradeLevel = "daisy";
                    break;
                case 'b':
                    gradeLevel = "brownie";
                    break;
                case 'j':
                    gradeLevel = "junior";
                    break;
                case 's':
                    gradeLevel = "senior";
                    break;
                case 'a':
                    gradeLevel = "ambassador";
                    break;
                case 'c':
                    gradeLevel = "cadette";
                    break;
                case 'm':
                    gradeLevel = "multi-level";
                    break;
            }
            Tag searchByTag = null;
            if (gradeLevel != null) {
                searchByTag = tagManager.resolve("/etc/tags/vtkcontent/meetings/" + gradeLevel + "/" + id);
            }
            if (searchByTag == null) {
                searchByTag = tagManager.resolve("/etc/tags/vtkcontent/meetings/" + id);
            }
            if (searchByTag != null) {
                return searchByTag.getTagID();
            }
        }
        return null;
    }

    private String buildIconsQuery(String tag) {
        if (tag != null) {
            tag = tag.trim();
        }
        String sql = "SELECT s.* FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-vtk/meeting-icons]) AND s.[jcr:content/metadata/cq:tags] LIKE '" + tag + "'";
        return sql;
    }

}

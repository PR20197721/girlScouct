package org.girlscouts.vtk.osgi.service;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.Map;

public interface GirlScoutsJCRService {

    public ValueMap getNode(String path);
    public ValueMap getNodeById(String id);
    public ValueMap createPath(String path);
    public boolean removeNode(String path);
    public boolean setNodeProperties(String nodePath, Map<String, String> singleProps, Map<String, String[]> multiProps);
}

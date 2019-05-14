package org.girlscouts.vtk.osgi.service;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.ocm.JcrNode;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

public interface GirlScoutsJCRRepository {

    public ValueMap getNode(String path);
    public ValueMap getNodeById(String path);
    public ValueMap createPath(String path);
    public boolean removeNode(String path);
    public boolean setNodeProperties(String nodePath, Map<String, String> singleProps, Map<String, String[]> multiProps);

}
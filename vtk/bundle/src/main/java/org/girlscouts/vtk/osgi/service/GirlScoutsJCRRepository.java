package org.girlscouts.vtk.osgi.service;

import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.girlscouts.vtk.ocm.JcrNode;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

public interface GirlScoutsJCRRepository {

    public QueryResult executeQuery(String query);
    public SearchResult executeQuery(Map<String, String> predicates, Integer start, Integer hitsPerPage, Boolean excerpt);
    public Node getNode(String path);
    public Node getNodeById(String path);
    public Node createPath(String path);
    public boolean removeNode(String path);
    public boolean setNodeProperties(String nodePath, Map<String, String> singleProps, Map<String, String[]> multiProps);

}
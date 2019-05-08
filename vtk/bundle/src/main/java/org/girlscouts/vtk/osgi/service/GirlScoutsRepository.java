package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.ocm.JcrNode;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

public interface GirlScoutsRepository {

    public QueryResult executeQuery(String query);
    public Node getNode(String path);
}

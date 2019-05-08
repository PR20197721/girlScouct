package org.girlscouts.vtk.osgi.service;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;

public interface GirlScoutsRepoService {

    public QueryResult executeQuery(String query);

    public Node getNode(String path);

}

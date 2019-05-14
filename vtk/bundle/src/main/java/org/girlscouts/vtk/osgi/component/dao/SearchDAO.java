package org.girlscouts.vtk.osgi.component.dao;

import java.util.List;

public interface SearchDAO {
    List<String> getData(String query);
}

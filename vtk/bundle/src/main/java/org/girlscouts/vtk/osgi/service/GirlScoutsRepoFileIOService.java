package org.girlscouts.vtk.osgi.service;

public interface GirlScoutsRepoFileIOService {
    String readFile(String path);

    void writeFile(String path, String content);

}

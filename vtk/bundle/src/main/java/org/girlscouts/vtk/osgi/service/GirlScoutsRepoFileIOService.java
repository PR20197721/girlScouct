package org.girlscouts.vtk.osgi.service;

public interface GirlScoutsRepoFileIOService {

    public String readFile(String path);
    public void writeFile(String path, String content);

}

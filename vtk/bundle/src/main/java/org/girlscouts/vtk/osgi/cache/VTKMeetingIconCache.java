package org.girlscouts.vtk.osgi.cache;

public interface VTKMeetingIconCache {

    boolean contains(String key);
    <T extends String> T read(String key);
    <T extends String> void write(String key, T entity);

}

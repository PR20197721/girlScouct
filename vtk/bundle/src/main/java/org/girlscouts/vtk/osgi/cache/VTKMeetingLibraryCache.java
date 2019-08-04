package org.girlscouts.vtk.osgi.cache;

import org.girlscouts.vtk.ocm.JcrNode;

import java.util.List;

public interface VTKMeetingLibraryCache {

    boolean contains(String key);
    <T extends JcrNode> T read(String key);
    <T extends JcrNode> void write(String key, T entity);
    boolean containsList(String key);
    <T extends JcrNode> List<T> readList(String key);
    <T extends JcrNode> void writeList(String key, List<T> list);

}

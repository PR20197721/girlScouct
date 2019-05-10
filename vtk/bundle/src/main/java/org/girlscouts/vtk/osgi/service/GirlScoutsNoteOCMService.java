package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Note;

import java.util.List;
import java.util.Map;

public interface GirlScoutsNoteOCMService {

    public Note create(Note object);
    public Note update(Note object);

    public Note read(String path);
    public boolean delete(Note object);

    public Note findObject(String path, Map<String, String> params);
    public List<Note> findObjects(String path, Map<String, String> params);

}

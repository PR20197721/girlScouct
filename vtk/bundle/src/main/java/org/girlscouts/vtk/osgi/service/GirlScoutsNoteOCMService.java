package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Note;

import java.util.List;
import java.util.Map;

public interface GirlScoutsNoteOCMService {
    Note create(Note object);

    Note update(Note object);

    Note read(String path);

    boolean delete(Note object);

    Note findObject(String path, Map<String, String> params);

    List<Note> findObjects(String path, Map<String, String> params);

}

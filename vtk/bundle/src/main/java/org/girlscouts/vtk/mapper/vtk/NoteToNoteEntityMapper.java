package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Note;
import org.girlscouts.vtk.rest.entity.vtk.NoteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoteToNoteEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(NoteToNoteEntityMapper.class);

    public static NoteEntity map(Note note){
        if(note != null) {
            try {
                NoteEntity entity = new NoteEntity();
                entity.setCreatedByUserId(note.getCreatedByUserId());
                entity.setCreatedByUserName(note.getCreatedByUserName());
                entity.setCreateTime(note.getCreateTime());
                entity.setDbUpdate(note.isDbUpdate());
                entity.setMessage(note.getMessage());
                entity.setPath(note.getPath());
                entity.setRefId(note.getRefId());
                entity.setUid(note.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Note to NoteEntity ", e);
            }
        }
        return null;
    }
}

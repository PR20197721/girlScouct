package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.rest.entity.vtk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CollectionModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(CollectionModelToEntityMapper.class);

    public static List<ActivityEntity> mapActivities(List<Activity> activities) {
        if (activities != null) {
            List<ActivityEntity> entities = new ArrayList<ActivityEntity>();
            for (Activity activity : activities) {
                try {
                    entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(activity));
                } catch (Exception ex) {
                    log.error("Error occurred:", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static List<LocationEntity> mapLocations(List<Location> locations) {
        if (locations != null) {
            List<LocationEntity> entities = new ArrayList<LocationEntity>();
            for (Location location : locations) {
                try {
                    entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(location));
                } catch (Exception ex) {
                    log.error("Error occurred: ", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static List<MeetingCanceledEntity> mapMeetingsCanceled(List<MeetingCanceled> meetingsCanceled) {
        if (meetingsCanceled != null) {
            List<MeetingCanceledEntity> meetingCanceledList = new ArrayList<MeetingCanceledEntity>();
            for (MeetingCanceled meetingCanceled : meetingsCanceled) {
                try {
                    meetingCanceledList.add(ModelToRestEntityMapper.INSTANCE.toEntity(meetingCanceled));
                } catch (Exception ex) {
                    log.error("Error occurred: ", ex);
                }
            }
            return meetingCanceledList;
        }
        return null;
    }

    public static List<MeetingEEntity> mapMeetingEvents(List<MeetingE> meetingsE) {
        if (meetingsE != null) {
            List<MeetingEEntity> meetingEList = new ArrayList<MeetingEEntity>();
            for (MeetingE meetingE : meetingsE) {
                try {
                    meetingEList.add(ModelToRestEntityMapper.INSTANCE.toEntity(meetingE));
                } catch (Exception ex) {
                    log.error("Error occurred: ", ex);
                }
            }
            return meetingEList;
        }
        return null;
    }

    public static List<MilestoneEntity> mapMilestones(List<Milestone> milestones) {
        if (milestones != null) {
            List<MilestoneEntity> entities = new ArrayList<MilestoneEntity>();
            for (Milestone milestone : milestones) {
                try {
                    entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(milestone));
                } catch (Exception ex) {
                    log.error("Error occurred: ", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static List<AssetEntity> mapAssets(List<Asset> assets) {
        if (assets != null) {
            List<AssetEntity> entities = new ArrayList<>();
            for (Asset asset : assets) {
                try {
                    entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(asset));
                } catch (Exception ex) {
                    log.error("Error occurred:", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static List<SentEmailEntity> mapSentEmails(List<SentEmail> sentEmails, String template) {
        if (sentEmails != null) {
            List<SentEmailEntity> entities = new ArrayList<SentEmailEntity>();
            for (SentEmail sentEmail : sentEmails) {
                try {
                    SentEmailEntity sentEmailEntity = ModelToRestEntityMapper.INSTANCE.toEntity(sentEmail);
                    sentEmailEntity.setHtmlMsg(sentEmail.getHtmlMsg(template));
                    entities.add(sentEmailEntity);
                } catch (Exception ex) {
                    log.error("Error occurred:", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static List<NoteEntity> mapNotes(List<Note> notes) {
        if (notes != null) {
            List<NoteEntity> entities = new ArrayList<NoteEntity>();
            for (Note note : notes) {
                try {
                    entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(note));
                } catch (Exception ex) {
                    log.error("Error occurred: ", ex);
                }
            }
            return entities;
        }
        return null;
    }

    public static Map<String, JcrCollectionHoldStringEntity> mapMeetingInfo(Map<String, JcrCollectionHoldString> meetingInfos) {
        if (meetingInfos != null) {
            Map<String, JcrCollectionHoldStringEntity> entities = new TreeMap<String, JcrCollectionHoldStringEntity>();
            Set<String> keys = meetingInfos.keySet();
            if (keys != null) {
                for (String key : keys) {
                    try {
                        entities.put(key, ModelToRestEntityMapper.INSTANCE.toEntity(meetingInfos.get(key)));
                    } catch (Exception ex) {
                        log.error("Error occurred: ", ex);
                    }
                }
            }
            return entities;
        }
        return null;
    }

    public static Map<Date, BaseEntity> mapYearPlanComponents(Map<Date, YearPlanComponent> yearPlanComponents) {
        if (yearPlanComponents != null) {
            Map<Date, BaseEntity> entities = new TreeMap<Date, BaseEntity>();
            Set<Date> keys = yearPlanComponents.keySet();
            if (keys != null) {
                for (Date key : keys) {
                    try {
                        Object o = yearPlanComponents.get(key);
                        Date entityKey = new Date(key.getTime());
                        while (entities.containsKey(entityKey)) {
                            entityKey.setTime(entityKey.getTime() + 10000);
                        }
                        if (o instanceof MeetingE) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((MeetingE) o));
                            continue;
                        }
                        if (o instanceof Activity) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((Activity) o));
                            continue;
                        }
                        if (o instanceof Meeting) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((Meeting) o));
                            continue;
                        }
                        if (o instanceof Milestone) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((Milestone) o));
                            continue;
                        }
                        if (o instanceof MeetingCanceled) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((MeetingCanceled) o));
                            continue;
                        }
                        if (o instanceof YearPlanComponent) {
                            entities.put(entityKey, ModelToRestEntityMapper.INSTANCE.toEntity((YearPlanComponent) o));
                            continue;
                        }
                    } catch (Exception ex) {
                        log.error("Error occurred: ", ex);
                    }
                }
            }
            return entities;
        }
        return null;
    }
}

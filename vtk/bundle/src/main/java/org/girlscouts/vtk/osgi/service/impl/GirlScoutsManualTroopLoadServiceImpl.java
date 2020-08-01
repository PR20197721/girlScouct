package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.conf.GirlScoutsManualTroopLoadServiceConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsManualTroopLoadService;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
import org.girlscouts.vtk.osgi.service.GirlScoutsTroopOCMService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = {GirlScoutsManualTroopLoadService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsManualTroopLoadServiceImpl")
@Designate(ocd = GirlScoutsManualTroopLoadServiceConfig.class)
public class GirlScoutsManualTroopLoadServiceImpl extends BasicGirlScoutsService implements GirlScoutsManualTroopLoadService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsManualTroopLoadServiceImpl.class);
    @Reference
    GirlScoutsRepoFileIOService girlScoutsRepoFileIOService;
    @Reference
    GirlScoutsTroopOCMService girlScoutsTroopOCMService;

    private Boolean active;
    private String localDataPath;
    private String vtkBase;
    private String data;
    private Map<String, List<Record>> recordMap = new HashMap<>();

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.localDataPath = getConfig("localDataPath");
        this.vtkBase = getConfig("vtkBase");
        this.active = getBooleanConfig("active");
        if (this.active && this.localDataPath != null) {
            this.data = girlScoutsRepoFileIOService.readFile(this.localDataPath);
            int total = 0;
            if (this.data != null) {
                try {
                    Record[] records = new Gson().fromJson(this.data, Record[].class);
                    for (Record record : records) {
                        if (record != null) {
                            if (recordMap.containsKey(record.getSfUserId())) {
                                recordMap.get(record.getSfUserId()).add(record);
                            } else {
                                List<Record> temp = new ArrayList<>();
                                temp.add(record);
                                recordMap.put(record.getSfUserId(), temp);
                            }
                            total++;
                        }
                    }
                }catch(Exception e){
                    log.error("Error occurred: ", e);
                }
            }
            log.info("GirlScoutsManualTroopLoadServiceImpl loaded "+total+ " records for "+recordMap.size()+" users");
        }
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public List<Troop> loadTroops(User user) {
        List <Troop> my20Troops = new ArrayList<>();
        if(user != null && user.getSfUserId() != null){
            List<Record> my20Records = recordMap.get(user.getSfUserId().trim());
            if(my20Records != null) {
                for (Record record : my20Records) {
                    try {
                        String crxTroopId = record.getSfTroopId();
                        if (record.getIRM()) {
                            crxTroopId = "IRM_" + crxTroopId;
                        }
                        String troopPath = this.vtkBase + "/" + record.getCouncilCode() + "/troops/" + crxTroopId;
                        Troop troop = girlScoutsTroopOCMService.read(troopPath);
                        troop.setIsLoadedManualy(true);
                        troop.setCouncilCode(record.getCouncilCode().toString());
                        troop.setTroopName(troop.getSfTroopName());
                        troop.setTroopId(troop.getSfTroopId());
                        troop.setId(troop.getSfTroopId());
                        troop.setGradeLevel(troop.getSfTroopAge());
                        troop.setSfUserId(record.getSfUserId());
                        troop.setCouncilPath(this.vtkBase + "/"  + record.getCouncilCode());
                        if (record.getTroopLeader()) {
                            troop.setRole("DP");
                            troop.setParticipationCode("Troop");
                        } else {
                            if (record.getParent()) {
                                troop.setRole("PA");
                                troop.setParticipationCode("Troop");
                                if (record.getIRM()) {
                                    troop.setIsIRM(true);
                                }else{
                                    troop.setIsIRM(false);
                                }
                            }
                        }
                        my20Troops.add(troop);
                    }catch(Exception e){
                        log.error("Error manually loading troop "+this.vtkBase+"/"+record.getCouncilCode()+"/"+record.getSfTroopId());
                    }
                }
            }else{
                log.debug("No troop records found in "+this.localDataPath+" for user: "+user.getSfUserId());
            }
        }
        return my20Troops;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    private class Record {
        @SerializedName("SF User ID")
        private String sfUserId;
        @SerializedName("SF Troop Id")
        private String sfTroopId;
        @SerializedName("isTroopLeader")
        private Boolean isTroopLeader;
        @SerializedName("isParent")
        private Boolean isParent;
        @SerializedName("isIRM")
        private Boolean isIRM;
        @SerializedName("PGL")
        private String gradeLevel;
        @SerializedName("TroopCouncil")
        private Integer councilCode;

        public String getSfUserId() {
            return sfUserId;
        }

        public void setSfUserId(String sfUserId) {
            this.sfUserId = sfUserId;
        }

        public String getSfTroopId() {
            return sfTroopId;
        }

        public void setSfTroopId(String sfTroopId) {
            this.sfTroopId = sfTroopId;
        }

        public Boolean getTroopLeader() {
            return isTroopLeader;
        }

        public void setTroopLeader(Boolean troopLeader) {
            isTroopLeader = troopLeader;
        }

        public Boolean getParent() {
            return isParent;
        }

        public void setParent(Boolean parent) {
            isParent = parent;
        }

        public Boolean getIRM() {
            return isIRM;
        }

        public void setIRM(Boolean IRM) {
            isIRM = IRM;
        }

        public String getGradeLevel() {
            return gradeLevel;
        }

        public void setGradeLevel(String gradeLevel) {
            this.gradeLevel = gradeLevel;
        }

        public Integer getCouncilCode() {
            return councilCode;
        }

        public void setCouncilCode(Integer councilCode) {
            this.councilCode = councilCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Record record = (Record) o;
            return Objects.equals(sfUserId, record.sfUserId) && Objects.equals(sfTroopId, record.sfTroopId) && Objects.equals(isTroopLeader, record.isTroopLeader) && Objects.equals(isParent, record.isParent) && Objects.equals(isIRM, record.isIRM) && Objects.equals(gradeLevel, record.gradeLevel) && Objects.equals(councilCode, record.councilCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sfUserId, sfTroopId, isTroopLeader, isParent, isIRM, gradeLevel, councilCode);
        }
    }
}

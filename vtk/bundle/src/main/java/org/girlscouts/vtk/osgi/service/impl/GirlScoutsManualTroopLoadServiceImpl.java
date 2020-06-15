package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.osgi.conf.GirlScoutsManualTroopLoadServiceConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsManualTroopLoadService;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
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
    private Boolean active;
    private String localDataPath;
    private String data;
    private Map<String, List<Record>> recordMap = new HashMap<>();

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.localDataPath = getConfig("localDataPath");
        this.active = getBooleanConfig("active");
        if (this.active && this.localDataPath != null) {
            this.data = girlScoutsRepoFileIOService.readFile(this.localDataPath);
            int total = 0;
            if (this.data != null) {
                RecordRows rows = new Gson().fromJson(this.data, RecordRows.class);
                Record[] records = rows.getRecords();
                for (Record record : records) {
                    if (record != null) {
                        if(recordMap.containsKey(record.getSfUserId())){
                            recordMap.get(record.getSfUserId()).add(record);
                        }else{
                            List<Record> temp = new ArrayList<>();
                            temp.add(record);
                            recordMap.put(record.getSfUserId(),temp);
                        }
                        total ++;
                    }
                }
            }
            log.info("GirlScoutsManualTroopLoadServiceImpl loaded "+total+ "records for "+recordMap.size()+" users");
        }
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public List<Troop> loadTroops(String userId) {
        return null;
    }

    private class RecordRows {
        Record[] records;

        public Record[] getRecords() {
            return records;
        }

        public void setRecords(Record[] records) {
            this.records = records;
        }
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

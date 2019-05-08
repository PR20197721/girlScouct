package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;

@Node
public class TroopNode extends JcrNode implements Serializable, MappableToModel {

    @Bean(autoUpdate = false)
    private YearPlanNode yearPlan;
    @Field
    private String sfUserId;
    @Field
    private String sfTroopId;
    @Field
    private String sfTroopName;
    @Field
    private String sfTroopAge;
    @Field
    private String sfCouncil;
    @Field
    private String currentTroop;
    @Field
    private String errCode;
    @Field
    private String refId;

    public YearPlanNode getYearPlan() {
        return yearPlan;
    }

    public void setYearPlan(YearPlanNode yearPlan) {
        this.yearPlan = yearPlan;
    }

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

    public String getSfTroopName() {
        return sfTroopName;
    }

    public void setSfTroopName(String sfTroopName) {
        this.sfTroopName = sfTroopName;
    }

    public String getSfTroopAge() {
        return sfTroopAge;
    }

    public void setSfTroopAge(String sfTroopAge) {
        this.sfTroopAge = sfTroopAge;
    }

    public String getSfCouncil() {
        return sfCouncil;
    }

    public void setSfCouncil(String sfCouncil) {
        this.sfCouncil = sfCouncil;
    }

    public String getCurrentTroop() {
        return currentTroop;
    }

    public void setCurrentTroop(String currentTroop) {
        this.currentTroop = currentTroop;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    @Override
    public Object toModel() {
        return NodeToModelMapper.INSTANCE.toModel(this);
    }
}
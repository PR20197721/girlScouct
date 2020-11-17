package org.girlscouts.vtk.auth.models;

import org.girlscouts.vtk.exception.VtkError;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.sling.servlet.MeetingFilter;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

public class ApiConfig implements Serializable {
    private static final long serialVersionUID = 7310414085726791761L;
    //error msg
    List<VtkError> errors;
    private String userId; /* userId should be moved out.User obj exists* */
    private User user;
    private java.util.List<Troop> troops;
    private boolean isFail;

    private TreeMap<String, MeetingFilter.FilterOption> filters;


    public java.util.List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(java.util.List<Troop> troops) {
        this.troops = troops;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public java.util.List<VtkError> getErrors() {
        return errors;
    }

    public void setErrors(java.util.List<VtkError> errors) {
        this.errors = errors;
    }


    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean isFail) {
        this.isFail = isFail;
    }

    public TreeMap<String, MeetingFilter.FilterOption> getFilters() {
        return filters;
    }

    public void setFilters(TreeMap<String, MeetingFilter.FilterOption> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return "ApiConfig{" + "errors=" + errors + ", userId='" + userId + '\'' + ", user=" + user + ", troops=" + troops + ", isFail=" + isFail + ", filters=" + filters + '}';
    }
}
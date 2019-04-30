package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.StringTokenizer;

@Node
public class Cal implements Serializable {

    @Field(path = true)
    String path;
    @Field
    String dates;
    private boolean isDbUpdate = false;

    public String getDates() {


        String toRet = fmtDates();

        return toRet;//fmtDates(); //dates;
    }

    public void setDates(String dates) {

        dates = fmtDates(dates);

        if (dates != null && this.dates != null && !this.dates.equals(dates))
            isDbUpdate = true;

        this.dates = dates;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void fmtDate(java.util.List<org.joda.time.DateTime> sched) {

        String fmtDates = "";
        for (int i = 0; i < sched.size(); i++) {

            fmtDates = fmtDates + sched.get(i).toDate().getTime() + ",";
        }

        setDates(fmtDates);
    }

    public void addDate(java.util.Date date) {

        if (date == null) return;

        String fmtDates = getDates();
        fmtDates += date.getTime() + ",";

    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean isDbUpdate) {
        this.isDbUpdate = isDbUpdate;
    }

    public String fmtDates() {
        return fmtDates(this.dates);
    }

    public String fmtDates(String dates) {


        if (dates == null || dates.indexOf(",") == -1)
            return dates;

        java.util.List<Long> fmtList = new java.util.ArrayList<Long>();
        try {
            StringTokenizer t = new StringTokenizer(dates, ",");
            while (t.hasMoreElements()) {

                long _date = Long.parseLong(t.nextToken());
                gt:
                for (int i = 0; i < 35; i++) {
                    if (fmtList.contains(_date)) {
                        System.err.println("Found duplicate date in YP schedule " + _date + ".. Adding 1.");
                        _date += 1;
                        break gt;
                    }
                }

                fmtList.add(_date);
            }

            java.util.Collections.sort(fmtList);


        } catch (Exception e) {
            e.printStackTrace();
            dates = null;
        }

        String toRet = "";
        if (dates != null) {
            for (int i = 0; i < fmtList.size(); i++)
                toRet += fmtList.get(i) + ",";
            return toRet;
        }
        return null;
    }
}

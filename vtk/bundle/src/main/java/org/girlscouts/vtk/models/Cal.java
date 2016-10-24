package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.List;

import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Cal implements Serializable {

	@Field(path = true) String path;
	@Field String dates;
	private boolean isDbUpdate=false;
	
	public String getDates() {

		return dates;
	}

	public void setDates(String dates) {
		
		if( dates!=null && this.dates!=null && !this.dates.equals(dates))
			isDbUpdate =true;
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

		if( date ==null ) return;
		
		String fmtDates = getDates();
		fmtDates += date.getTime() + ",";

	}

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}
}

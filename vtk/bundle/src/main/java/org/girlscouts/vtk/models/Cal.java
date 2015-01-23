package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes="mix:lockable" )
public class Cal  implements Serializable{

	@Field(path=true) String path;
	@Field String dates;
	
	

	public String getDates() {
		
		    return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}

	
	
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void fmtDate( java.util.List <org.joda.time.DateTime> sched  ){
		
		String fmtDates ="";
		for(int i=0;i<sched.size();i++){
			
			fmtDates = fmtDates + sched.get(i).toDate().getTime() + ",";
		}
		
		
		
		setDates( fmtDates ) ;
	}
	

	public void addDate( java.util.Date date ){
		
		
		//TODO check if null or empty
		
		String fmtDates = getDates();
		fmtDates+= date.getTime() + ",";
		
	}
	
	
	
	

	
	
	
	
	
}

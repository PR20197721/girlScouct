package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.StringTokenizer;
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

		//String _frmDates = fmtDates();
		//return _frmDates == null ? dates : _frmDates;
		return fmtDates(); //dates;
	}

	public void setDates(String dates) {
		
		dates = fmtDates(dates);
		//String _frmDates = fmtDates();
		//dates= _frmDates==null ? dates : _frmDates;
		
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
	
	public String fmtDates(){ return fmtDates(this.dates); }
	public String fmtDates( String dates ){
		
//if( true ) return dates;

		if( dates ==null || dates.indexOf(",") ==-1 )
			return dates;
		
		java.util.List<Long> fmtList = new java.util.ArrayList <Long>();
		try{
			StringTokenizer t= new StringTokenizer(dates,",");
			while( t.hasMoreElements()){
				
				long _date= Long.parseLong( t.nextToken() );
				gt:for(int i=0;i<35;i++){
					if( fmtList.contains(_date) ){
						System.err.println("Found duplicate date in YP schedule "+ _date +".. Adding 1.");
						_date += 1;
						break gt;
					}
				}
				
				fmtList.add( _date );
			}
System.err.println("b4 testRR: "+  fmtList);			
			java.util.Collections.sort( fmtList );
System.err.println("after testRR: "+  fmtList);	
			
		}catch(Exception e){
			e.printStackTrace();
			dates=null;
		}
		
		String toRet= "";
		if( dates !=null ){
			for(int i=0;i< fmtList.size();i++)
				toRet += Long.toString( fmtList.get(i) )+",";
			return toRet;
		}
		return null;
	}
}

package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class MeetingE1   implements Serializable {
	
public MeetingE1(MeetingE meetingE){
	this.path= meetingE.getPath();
	this.id= meetingE.getId();
}

	@Field(path = true)
	private String path;
	
	
	@Field
	private Integer id;


	
}

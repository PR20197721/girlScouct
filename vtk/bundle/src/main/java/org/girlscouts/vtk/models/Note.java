package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class Note implements Serializable{

	    @Field private String message;
		@Field(path = true) String path;		
		@Field(id = true) String uid;
		@Field Long createTime;
		@Field String createdByUserName;
		@Field String createdByUserId;
		@Field String refId; //meetingId, activity, anything else
		
		private boolean isDbUpdate=false;
		
		public Note() {
			this.uid = "N" + new java.util.Date().getTime() + "_" + Math.random();
			this.createTime = new java.util.Date().getTime();
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
			isDbUpdate=true;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public boolean isDbUpdate() {
			return isDbUpdate;
		}

		public long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public String getCreatedByUserName() {
			return createdByUserName;
		}

		public void setCreatedByUserName(String createdByUserName) {
			this.createdByUserName = createdByUserName;
		}

		public String getCreatedByUserId() {
			return createdByUserId;
		}

		public void setCreatedByUserId(String createdByUserId) {
			this.createdByUserId = createdByUserId;
		}

		public String getRefId() {
			return refId;
		}

		public void setRefId(String refId) {
			this.refId = refId;
		}

	
}

package org.girlscouts.vtk.models;

import java.io.Serializable;

import javax.persistence.*;


@Entity
@Table( name="Note")
public class Note implements Serializable{

	    @Column private String message;
		@Transient String path;		
		@Id @Column String uid;
		@Column Long createTime;
		@Column String createdByUserName;
		@Column String createdByUserId;
		@Column String refId; //meetingId, activity, anything else
		
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

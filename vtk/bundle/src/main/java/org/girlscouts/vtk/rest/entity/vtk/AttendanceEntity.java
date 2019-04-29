package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class AttendanceEntity extends BaseEntity {

    @SerializedName("path")
	private String path;
    @SerializedName("id")
	private String id;
    @SerializedName("users")
	private String users; // sf id
    @SerializedName("total")
	private int total;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}

package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jboss.logging.Field;

@Entity
@Table(name = "Attendance")
public class Attendance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4925154373656969833L;

	@Transient	private String path;
	
	@Column @Id 
	private String id;
	
	@Column
	String users; // sf id
	
	@Column int total;
	
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

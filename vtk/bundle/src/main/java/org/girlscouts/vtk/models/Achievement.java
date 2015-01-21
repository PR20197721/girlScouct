package org.girlscouts.vtk.models;
import java.io.Serializable;
import java.util.List;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.AssetComponentType;

@Node(jcrMixinTypes="mix:lockable" )
public class Achievement  implements Serializable{

	@Field (path=true) private String path;
	@Field (id=true) private String  id;
	@Collection List<String> users; //sf id
	
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
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}

	
}

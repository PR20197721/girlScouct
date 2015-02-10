package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.AssetComponentType;

@Node(jcrMixinTypes = "mix:lockable")
public class Asset implements Serializable {

	public Asset() {
		this.uid = "A" + new java.util.Date().getTime() + "_" + Math.random();
		this.isCachable = false;
		this.type = "AID";
	}

	public Asset(String path) {
		this.path = path;
		this.uid = "A" + new java.util.Date().getTime() + "_" + Math.random();
		this.isCachable = false;
		this.type = "AID";
	}

	@Field	private String type, description, title, docType, refId;
	@Field(path = true) private String path;
	@Field Boolean isCachable;
	@Field(id = true) private String uid;
	private boolean isDbUpdate=false;
	
	
	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		if( (docType !=null && this.docType!=null && !this.docType.equals(docType)  )	||
				(docType!=null && this.docType==null) )
			isDbUpdate=true;
		this.docType = docType;
		System.err.println("tata "+1);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if( (title !=null && this.title!=null && !this.title.equals(title)  )	||
				(title!=null && this.title==null) )
			isDbUpdate=true;
		this.title = title;
		System.err.println("tata "+2);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if( (description !=null && this.description!=null && !this.description.equals(description)  )	||
				(description!=null && this.description==null) )
			isDbUpdate=true;
		this.description = description;
		System.err.println("tata "+3);
	}

	public Boolean getIsCachable() {
		return isCachable;
	}

	public void setIsCachable(Boolean isCachable) {
		if( (isCachable !=null && this.isCachable!=null && this.isCachable.booleanValue() !=isCachable.booleanValue()  )	||
				(isCachable!=null && this.isCachable==null) )
			isDbUpdate=true;
		this.isCachable = isCachable;
		System.err.println("tata "+4);
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		if( (refId !=null && this.refId!=null && !this.refId.equals(refId)  )	||
				(refId!=null && this.refId==null) )
			isDbUpdate=true;
		this.refId = refId;
		System.err.println("tata "+5);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if( (type !=null && this.type!=null && !this.type.equals(type)  )	||
				(type!=null && this.type==null) )
			isDbUpdate=true;
		this.type = type;
		System.err.println("tata "+6);
	}

	public AssetComponentType getType(boolean nothing) {
		return AssetComponentType.valueOf(this.getType());
	}

	public void setType(AssetComponentType act) {
		setType(act.toString());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if( (path !=null && this.path!=null && !this.path.equals(path)  )	||
				(path!=null && this.path==null) )
			isDbUpdate=true;
		this.path = path;
		System.err.println("tata "+7);
	}

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}

}

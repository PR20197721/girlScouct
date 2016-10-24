package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.girlscouts.vtk.dao.AssetComponentType;

public class Search implements Serializable {

	private String path, content, type, desc, subTitle;
	private AssetComponentType assetType;

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public AssetComponentType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetComponentType assetType) {
		this.assetType = assetType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}

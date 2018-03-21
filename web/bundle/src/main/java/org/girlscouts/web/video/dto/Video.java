package org.girlscouts.web.video.dto;

import org.girlscouts.web.video.util.VIDEO_TYPE;

public class Video {
	
	private String size = "";
	private String sourceUrl = "";
	private String playerId = "";
	private String thumbnail = "";
	private String iFrameUrl = "";
	private String title = "";
	private VIDEO_TYPE type = VIDEO_TYPE.NONE;

	public Video() {}
	
	public Video(String imageSize, String sourceUrl, String playerId, String thumbnail, String iFrameUrl, String title, VIDEO_TYPE type) {
		this.size = imageSize;
		this.sourceUrl = sourceUrl;
		this.playerId = playerId;
		this.thumbnail = thumbnail;
		this.iFrameUrl = iFrameUrl;
		this.title = title;
		this.type = type;
	}
	
	public VIDEO_TYPE getType() {
		return type;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getiFrameUrl() {
		return iFrameUrl;
	}
	public void setiFrameUrl(String iFrameUrl) {
		this.iFrameUrl = iFrameUrl;
	}
	public void setType(VIDEO_TYPE type) {
		this.type = type;
	}
	public String getThumbnail() {
		return thumbnail;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
}

package org.girlscouts.common.components;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
public class GSEmailAttachment {

	private String baseName;
	private byte[] fileData;
	private String description;
	private MimeType fileType;

	public GSEmailAttachment(String fileName, String fileData, String description, MimeType fileType) {
		this.baseName = fileName;
		try{
			this.fileData = fileData.getBytes(StandardCharsets.UTF_8);
		}catch (Exception e){
			this.fileData = fileData.getBytes();
		}

		this.fileType = fileType;
		if (description != null) {
			this.description = description;
		} else {
			this.description = baseName;
		}
	}
    public GSEmailAttachment(String fileName, byte[] fileData, String description, MimeType fileType) {
        this.baseName = fileName;
        this.fileData = fileData;
        this.fileType = fileType;
        if (description != null) {
            this.description = description;
        } else {
            this.description = baseName;
        }
    }

	public String getBaseName() {
		return baseName;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public MimeType getFileType() {
		return fileType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return this.getBaseName() + "." + this.getFileType().getFileExt();
	}

	public DataHandler getDataHandler() throws IOException {
		return new DataHandler(new ByteArrayDataSource(this.getFileData(), this.getFileType().getMimeType()));
	}

	public DataSource getDataSource() throws IOException {
		return new ByteArrayDataSource(this.getFileData(), this.getFileType().getMimeType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseName == null) ? 0 : baseName.hashCode());
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GSEmailAttachment other = (GSEmailAttachment) obj;
		if (baseName == null) {
			if (other.baseName != null)
				return false;
		} else if (!baseName.equals(other.baseName))
			return false;
		if (fileType != other.fileType)
			return false;
		return true;
	}

	public enum MimeType {

		TEXT_PLAIN("text/plain", "txt"), TEXT_CSV("text/csv", "csv"), TEXT_HTML("text/html", "html"), VIDEO_MP4("video/mp4", ".mp4"),
		APPLICATION_PDF("application/pdf", "pdf"), APPLICATION_ZIP("application/zip", "zip"), IMAGE_PNG("image/png", "png"), IMAGE_JPEG("image/jpeg", "jpg");


		private String mimeType;
		private String fileExt;

		MimeType(String mimeType, String fileExt) {
			this.mimeType = mimeType;
			this.fileExt = fileExt;
		}

		public String getMimeType() {
			return this.mimeType;
		}

		public String getFileExt() {
			return fileExt;
		}
	}
}

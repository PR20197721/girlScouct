package org.girlscouts.web.components;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
public class GSEmailAttachment {

	private String baseName;
	private String fileData;
	private MimeType fileType;

	public GSEmailAttachment(String fileName, String fileData, MimeType fileType) {
		this.baseName = fileName;
		this.fileData = fileData;
		this.fileType = fileType;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}
	public String getFileData() {
		return fileData;
	}
	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public MimeType getFileType() {
		return fileType;
	}

	public void setFileType(MimeType fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return this.getBaseName() + "." + this.getFileType().getFileExt();
	}

	public DataHandler getDataHandler() throws IOException {
		return new DataHandler(new ByteArrayDataSource(this.getFileData(), this.getFileType().getMimeType()));
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

		TEXT_PLAIN("text/plain", "txt");

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

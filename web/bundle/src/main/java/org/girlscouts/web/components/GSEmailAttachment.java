package org.girlscouts.web.components;

public class GSEmailAttachment {

	private String fileName;
	private String fileData;
	private MimeType fileType;

	public GSEmailAttachment(String fileName, String fileData, MimeType fileType) {
		this.fileName = fileName;
		this.fileData = fileData;
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
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

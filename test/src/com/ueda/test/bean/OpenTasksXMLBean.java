package com.ueda.test.bean;

public class OpenTasksXMLBean {

	private int buildNum;
	private String priority;
	private LineRangesBean lineRangesBean;
	private int primaryLineNumber;
	private String fileName;
	private String packageName;
	private String type;
	private long contextHashCode;

	public int getBuildNum() {
		return buildNum;
	}

	public void setBuildNum(int buildNum) {
		this.buildNum = buildNum;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public LineRangesBean getLineRangesBean() {
		return lineRangesBean;
	}

	public void setLineRangesBean(LineRangesBean lineRangesBean) {
		this.lineRangesBean = lineRangesBean;
	}

	public int getPrimaryLineNumber() {
		return primaryLineNumber;
	}

	public void setPrimaryLineNumber(int primaryLineNumber) {
		this.primaryLineNumber = primaryLineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getContextHashCode() {
		return contextHashCode;
	}

	public void setContextHashCode(long contextHashCode) {
		this.contextHashCode = contextHashCode;
	}

	public String toString() {
		return "OpenTasksXMLBean [buildNum=" + buildNum + ", priority="
				+ priority + ", lineRangesBean=" + lineRangesBean
				+ ", primaryLineNumber=" + primaryLineNumber + ", fileName="
				+ fileName + ", packageName=" + packageName + ", type=" + type
				+ ", contextHashCode=" + contextHashCode + "]";
	}

}

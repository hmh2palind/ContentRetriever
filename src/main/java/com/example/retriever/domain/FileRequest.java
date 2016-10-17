package com.directv.edm.domain;

public class FileRequest {
	private String tmsId;
	private boolean isOtt;
	private boolean isStreaming;
	private boolean isNonlinear;
	private boolean isLinear;
	private boolean isDeltaFile;
	
	
	public FileRequest(){
		
	}
	public FileRequest(String tmsId, boolean isOtt, boolean isStreaming,
			boolean isNonlinear, boolean isLinear, boolean isDeltaFile) {
		this.tmsId = tmsId;
		this.isOtt = isOtt;
		this.isStreaming = isStreaming;
		this.isNonlinear = isNonlinear;
		this.isLinear = isLinear;
		this.isDeltaFile = isDeltaFile;
	}
	public String getTmsId() {
		return tmsId;
	}
	public void setTmsId(String tmsId) {
		this.tmsId = tmsId;
	}
	public boolean isOtt() {
		return isOtt;
	}
	public void setOtt(boolean isOtt) {
		this.isOtt = isOtt;
	}
	public boolean isStreaming() {
		return isStreaming;
	}
	public void setStreaming(boolean isStreaming) {
		this.isStreaming = isStreaming;
	}
	public boolean isNonlinear() {
		return isNonlinear;
	}
	public void setNonlinear(boolean isNonlinear) {
		this.isNonlinear = isNonlinear;
	}
	public boolean isLinear() {
		return isLinear;
	}
	public void setLinear(boolean isLinear) {
		this.isLinear = isLinear;
	}
	public boolean isDeltaFile() {
		return isDeltaFile;
	}
	public void setDeltaFile(boolean isDeltaFile) {
		this.isDeltaFile = isDeltaFile;
	}
}

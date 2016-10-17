package com.directv.edm.domain;

import java.util.Iterator;
import java.util.Set;

public class FileResponse {
	private String fullFile;
	
	private String channel;
	private String program;
	private Set<String> provider;
	private Set<String> material;
	private String contentDetails;
	
	public String getFullFile() {
		return fullFile;
	}
	public void setFullFile(String fullFile) {
		this.fullFile = fullFile;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public Set<String> getProvider() {
		return provider;
	}
	
	public String getStrProvider() {
		if (provider == null || provider.isEmpty())
			return null;
		
		Iterator<String> it = provider.iterator();
		String str = "";
		while (it.hasNext()) {
			str += it.next() + "\n"; 
		}
		return str;
	}
	
	public void setProvider(Set<String> provider) {
		this.provider = provider;
	}
	public Set<String> getMaterial() {
		return material;
	}
	
	public String getStrMaterial() {
		if (material == null || material.isEmpty())
			return null;
		
		Iterator<String> it = material.iterator();
		String str = "";
		while (it.hasNext()) {
			str += it.next() + "\n"; 
		}
		return str;
	}
	
	public void setMaterial(Set<String> material) {
		this.material = material;
	}
	public String getContentDetails() {
		return contentDetails;
	}
	public void setContentDetails(String contentDetails) {
		this.contentDetails = contentDetails;
	}
}

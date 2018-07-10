package com.javaveda.model;

import java.util.Map;

public class Configuration {
	private String version;
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, String> getExpertise() {
		return expertise;
	}

	public void setExpertise(Map<String, String> expertise) {
		this.expertise = expertise;
	}

	private Map<String,String> expertise=null;
}

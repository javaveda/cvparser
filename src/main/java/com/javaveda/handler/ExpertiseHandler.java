package com.javaveda.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpertiseHandler {
	
	private HashMap<String, Object> context;

	public ExpertiseHandler(HashMap<String, Object> context){
		this.context = context;
	}
	
	public void extractExpertise(String str,  Map<String, String> expertiseMap) {
		for(String key:expertiseMap.keySet()){
			String languages[] = expertiseMap.get(key).split(",");
			Set<String> langs = context.containsKey(key) ? (Set<String>) context.get(key) : new HashSet<>();
			for (String language : languages) {
				if (str.equalsIgnoreCase(language)) {
					langs.add(language);
				}
			}
			context.put(key, langs);
		}
	}

}

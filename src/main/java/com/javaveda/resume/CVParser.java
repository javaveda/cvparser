package com.javaveda.resume;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.javaveda.handler.ExpertiseHandler;
import com.javaveda.model.Configuration;

public class CVParser {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	public static final Pattern VALID_PHONE_NUMBER_REGEX = Pattern.compile("^\\+?[0-9. ()-]{10,25}$",
			Pattern.CASE_INSENSITIVE);
	private String cvPath;
	Configuration conf = null;
	
	private HashMap<String, Object> dataOut = new HashMap<>();


	public CVParser(String cvPath) {
		this.cvPath = cvPath;
		init();
	}

	private void init() {
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			try {
				conf = mapper.readValue(CVParser.class.getClassLoader().getResourceAsStream("conf.yaml"),
						Configuration.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String extractJson() {
		Gson gson = new Gson(); 
		String json = gson.toJson(extract()); 
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<String, Object> extract() {
		XWPFDocument xdoc = null;
		try (FileInputStream fis = new FileInputStream(cvPath)) {
			xdoc = new XWPFDocument(OPCPackage.open(fis));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try (XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc)) {
			splitBySpace(extractor.getText(), conf);
			return dataOut;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static void main(String[] args) {
		try {
			CVParser cv = new CVParser("Resume.docx");
		//	System.out.println(cv.extract());
			System.out.println(cv.extractJson());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String lastString = "";

	private void splitBySpace(String content, Configuration conf) {
		content = content.replaceAll("\n", " ");
		content = content.replaceAll("\t", " ");
		content = content.replaceAll(",", " ");
		String[] words = content.split(" ");
		ExpertiseHandler expertiseHandler = new ExpertiseHandler(dataOut);
		for (String str : words) {
			str = str.trim();
			extractEmail(str);
			extractPhone(str);
			extractGender(str);
			expertiseHandler.extractExpertise(str, conf.getExpertise());
			extractLinkedIn(str);
			extractStackOverflow(str);
			lastString = str;
		}
	}

	private void extractPhone(String str) {
		if (validate(str, VALID_PHONE_NUMBER_REGEX)) {
			if (!dataOut.containsKey("phoneNumber")) {
				if (lastString.startsWith("+") || lastString.startsWith("0")) {
					str = lastString + " " + str;
				}
				if (str.startsWith("+91") || str.startsWith("091")) {
					dataOut.put("country", "India");
				}
				dataOut.put("phoneNumber", str);
			}
		}
	}

	private void extractEmail(String inString) {
		if (validate(inString, VALID_EMAIL_ADDRESS_REGEX))
			updateList(inString, "emails");
	}

	private void extractStackOverflow(String inString) {
		if (inString.contains("stackoverflow.com")) {
			dataOut.put("stackoverflow", inString);
		}
	}

	private void extractGender(String str) {
		if (str.equalsIgnoreCase("male")) {
			dataOut.put("gender", "Male");
		}
		if (str.equalsIgnoreCase("female")) {
			dataOut.put("gender", "Female");
		}
	}

	private void extractLinkedIn(String inString) {
		if (inString.contains("linkedin.com")) {
			dataOut.put("linkedin", inString);
		}
	}

	private void updateList(String str, String key) {
		Set<String> langs = dataOut.containsKey(key) ? (Set<String>) dataOut.get(key) : new HashSet<>();
		langs.add(str);
		dataOut.put(key, langs);
	}

	private void extractMultiString(String str, String languages[], String key) {
		Set<String> langs = dataOut.containsKey(key) ? (Set<String>) dataOut.get(key) : new HashSet<>();
		for (String language : languages) {
			if (str.equalsIgnoreCase(language)) {
				langs.add(language);
			}
		}
		dataOut.put(key, langs);
	}

	public boolean validate(String string, Pattern pattern) {
		Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}

}

package com.directv.edm.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.directv.edm.constant.FullTags.*;

public class FullHandler extends DefaultHandler {
	private boolean desiredElementFound = false;

	private StringBuilder finalBuilder = new StringBuilder();
	private StringBuilder channelBuilder = new StringBuilder();
	private StringBuilder tempBuilder = null;
	
	private String tmsId = ">#<";
	private String parentTmsId = ">#<";
	private Set<String> providerIdSet;
	private Set<String> programIdSet;
	private Set<String> channelIdSet;
	private boolean isCheckProviderTag = false;
	private boolean start = true;
	
	private int channelCount = 0;
	
	private int materialCount = 0;
	private int count = 0;
	
	public FullHandler() {	
		channelBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>").append("\n");
		channelBuilder.append("<PGWSFullListing>").append("\n");
		channelBuilder.append("	<fullListing>").append("\n");
	}
	
	public FullHandler(String tmsId) {
		if (tmsId != null && !tmsId.isEmpty())
			this.tmsId = ">" + tmsId + "<";
		
		channelBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>").append("\n");
		channelBuilder.append("<PGWSFullListing>").append("\n");
		channelBuilder.append("	<fullListing>").append("\n");
	}

	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

		if (!start)
			return;
		
		if (CHANNEL.equals(qName) || ASTC_CHANNEL.equals(qName)) {
			if (!isCheckProviderTag) {				
				tempBuilder = new StringBuilder("		");
				desiredElementFound = true;
			}
		}
		
		else if (PROGRAM.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			desiredElementFound = true;
		}
		
		else if (SCHEDULE.equals(qName) || ASTC_SCHEDULE.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			desiredElementFound = true;
		}
		
		else if (CONTENT.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			desiredElementFound = true;
		}

		else if (PROVIDER.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			desiredElementFound = true;
			isCheckProviderTag = true; //providers tag has channels tags
		}
		
		else if (MATERIAL.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			desiredElementFound = true;
		}
		
		if (desiredElementFound == true) {
			tempBuilder.append("<").append(qName).append(">");
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (!start)
			return;
		
		if (CHANNEL.equals(qName) || ASTC_CHANNEL.equals(qName)) {
			if (!isCheckProviderTag) {
				tempBuilder.append("</").append(qName).append(">");
				desiredElementFound = false;
				
				if (checkContain(tempBuilder, channelIdSet)) {
					channelBuilder.append(tempBuilder).append("\n");
					
					if (++channelCount == channelIdSet.size()) {
						start = false;
					}
					tempBuilder = null;
				}
			}
		}
		
		else if (PROGRAM.equals(qName)) {
			tempBuilder.append("</").append(qName).append(">");
			desiredElementFound = false;
			
			if (tempBuilder.indexOf(tmsId) > -1 || tempBuilder.indexOf(parentTmsId) > -1) {
				finalBuilder.append(tempBuilder).append("\n");
				
				if (programIdSet == null)
					programIdSet = calculateSetValue(tempBuilder, OPEN_PROGRAM_ID, CLOSE_PROGRAM_ID);
				else 
					programIdSet.addAll(calculateSetValue(tempBuilder, OPEN_PROGRAM_ID, CLOSE_PROGRAM_ID));
				
				tempBuilder = null;
			}
		}

		else if (SCHEDULE.equals(qName) || ASTC_SCHEDULE.equals(qName)) {
			tempBuilder.append("</").append(qName).append(">");
			desiredElementFound = false;
			
			if (checkContain(tempBuilder, programIdSet)) {
				finalBuilder.append(tempBuilder).append("\n");
				
				if (channelIdSet == null) {
					channelIdSet = calculateSetValue(tempBuilder, OPEN_CHANNEL_ID, CLOSE_CHANNEL_ID);
				}
				else {
					channelIdSet.addAll(calculateSetValue(tempBuilder, OPEN_CHANNEL_ID, CLOSE_CHANNEL_ID));
				}
				tempBuilder = null;
			}
		}
		
		else if (CONTENT.equals(qName)) {
			tempBuilder.append("</").append(qName).append(">");
			desiredElementFound = false;
			
			if (tempBuilder.indexOf(tmsId) > -1 || tempBuilder.indexOf(parentTmsId) > -1) {
				finalBuilder.append(tempBuilder).append("\n");
				
				providerIdSet = calculateSetValue(tempBuilder, OPEN_PROVIDER_ID, CLOSE_PROVIDER_ID);
				if (providerIdSet == null || providerIdSet.isEmpty()) //content is Linear or Upcoming
					start = false;
				else
					materialCount = calculateSetValue(tempBuilder, OPEN_MATERIAL_ID, CLOSE_MATERIAL_ID).size();
				
				tempBuilder = null;
			}
		}
		
		else if (PROVIDER.equals(qName)) {
			tempBuilder.append("</").append(qName).append(">");
			desiredElementFound = false;
			isCheckProviderTag = false;
			
			if (providerIdSet != null && !providerIdSet.isEmpty()) {
				if (checkContain(tempBuilder,providerIdSet)) {
					finalBuilder.append(tempBuilder).append("\n");
					tempBuilder = null;
				}
			}
		}
		
		else if (MATERIAL.equals(qName)) {
			tempBuilder.append("</").append(qName).append(">");
			desiredElementFound = false;
			
			if (tempBuilder.indexOf(tmsId) > -1 || tempBuilder.indexOf(parentTmsId) > -1) {
				finalBuilder.append(tempBuilder).append("\n");
				
				if (++count == materialCount) {
					start = false;
				}
				
				tempBuilder = null;
			}
		}
		
		if (desiredElementFound == true) {
			tempBuilder.append("</").append(qName).append(">");
		}
		
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (desiredElementFound && this.start) {
			String temp = new String(ch, start, length).replace("&", "&amp;");
			tempBuilder = tempBuilder.append(temp);
		}
	}
	
	public String getFinalOutput() {
		finalBuilder.append("	</fullListing>").append("\n");
		finalBuilder.append("</PGWSFullListing>");
		
		return channelBuilder.append(finalBuilder).toString();
	}
	
	/**
	 * This method can using regular expression
	 * @return
	 */
	private Set<String> calculateSetValue(StringBuilder strBuilder, String beginStr, String endStr) {
		Set<String> values = new HashSet<String>();
		
		int endIndex = 0;
		int startIndex = strBuilder.indexOf(beginStr, endIndex);
		
		while (startIndex > -1){
			endIndex = strBuilder.indexOf(endStr, startIndex);
			values.add(strBuilder.substring(startIndex + beginStr.length(), endIndex));
			startIndex = strBuilder.indexOf(beginStr, endIndex);
		}
		
		return values;
	}
	
	private boolean checkContain(StringBuilder strBuilder,
			Set<String> strSet) {
		if (strSet == null || strSet.isEmpty() || strBuilder == null || strBuilder.length() == 0)
			return false;
		
		Iterator<String> it = strSet.iterator();
		while (it.hasNext()) {
			if (strBuilder.indexOf(it.next()) > -1)
				return true;
		}
		
		return false;
	}

	public Set<String> getChannelIdSet() {
		return channelIdSet;
	}
	
	public void setChannelIdSet(Set<String> channelIdSet) {
		this.channelIdSet = channelIdSet;
	}

	
	public void setStart(boolean start) {
		this.start = start;
	}

	public void setTmsId(String tmsId) {
		if (tmsId != null && !tmsId.isEmpty())
			this.tmsId = ">" + tmsId + "<";
	}
}

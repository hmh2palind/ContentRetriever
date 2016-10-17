package com.directv.edm.dao;

import static com.directv.edm.constant.DeltaTags.*;
import static com.directv.edm.constant.FullTags.*;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeltaHandler extends DefaultHandler {
	private StringBuilder channelBuilder 	= new StringBuilder();
	private StringBuilder programBuilder 	= new StringBuilder();
	private StringBuilder scheduleBuilder 	= new StringBuilder();
	private StringBuilder contentBuilder 	= new StringBuilder();
	private StringBuilder providerBuilder 	= new StringBuilder();
	private StringBuilder materialBuilder	= new StringBuilder();
	
	private Set<StringBuilder> materialSet	= new HashSet<StringBuilder>();
	private Set<StringBuilder> providerSet	= new HashSet<StringBuilder>();
	
	private StringBuilder tempBuilder = null;
	
	boolean desiredElementFound = false;
	private boolean isCheckProviderTag = false;

	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		
		if (CHANNEL.equals(qName)) {
			if (!isCheckProviderTag) {				
				tempBuilder = new StringBuilder("		");
				tempBuilder.append(OPEN_CHANNEL_ARRAY);
				desiredElementFound = true;
			}
			else {
				tempBuilder.append("<").append(qName).append(">");
			}
		}
		
		else if (PROGRAM.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			tempBuilder.append(OPEN_PROGRAM_ARRAY);
			desiredElementFound = true;
		}
		
		else if (SCHEDULE.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			tempBuilder.append(OPEN_SCHEDULE_ARRAY);
			desiredElementFound = true;
		}
		
		else if (CONTENT.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			tempBuilder.append(OPEN_CONTENT_ARRAY);
			desiredElementFound = true;
		}

		else if (PROVIDER.equals(qName)) {
			tempBuilder = new StringBuilder("		");
			tempBuilder.append(OPEN_NLPGWS_PROVIDER);
			
			desiredElementFound = true;
			isCheckProviderTag = true; //providers tag has channels tags
		}
		
		else if (MATERIAL.equals(qName)) {
			tempBuilder = new StringBuilder("	");
			tempBuilder.append(OPEN_NLPGWS_TITLE);
			desiredElementFound = true;
		} 
		
		else if (desiredElementFound == true) {
			tempBuilder.append("<").append(qName).append(">");
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (CHANNEL.equals(qName)) {
			if (!isCheckProviderTag) {
				tempBuilder.append(CLOSE_CHANNEL_ARRAY);
				channelBuilder.append(tempBuilder).append("\n");
				
				desiredElementFound = false;
				tempBuilder = null;
			}
			else {
				tempBuilder.append("</").append(qName).append(">");
			}
		}
		
		else if (PROGRAM.equals(qName)) {
			tempBuilder.append(CLOSE_PROGRAM_ARRAY);
			programBuilder.append(tempBuilder).append("\n");
			
			desiredElementFound = false;
			tempBuilder = null;
		}

		else if (SCHEDULE.equals(qName)) {
			tempBuilder.append(CLOSE_SCHEDULE_ARRAY);
			scheduleBuilder.append(tempBuilder).append("\n");
			
			desiredElementFound = false;
			tempBuilder = null;
			
		}
		
		else if (CONTENT.equals(qName)) {
			tempBuilder.append(CLOSE_CONTENT_ARRAY);
			contentBuilder.append(tempBuilder).append("\n");
			
			desiredElementFound = false;
			tempBuilder = null;
			
		}
		
		else if (PROVIDER.equals(qName)) {
			tempBuilder.append(CLOSE_NLPGWS_PROVIDER);
			providerBuilder.append(tempBuilder).append("\n");
			
			providerSet.add(providerBuilder);
			providerBuilder = new StringBuilder();
			
			isCheckProviderTag = false;
			
			desiredElementFound = false;
			tempBuilder = null;
		}
		
		else if (MATERIAL.equals(qName)) {
			tempBuilder.append(CLOSE_NLPGWS_TITLE);
			materialBuilder.append(tempBuilder).append("\n");
			
			materialSet.add(materialBuilder);
			materialBuilder = new StringBuilder();
			
			desiredElementFound = false;
			tempBuilder = null;
		}
		
		else if (desiredElementFound == true) {
			tempBuilder.append("</").append(qName).append(">");
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (desiredElementFound) {
			String temp = new String(ch, start, length).replace("&", "&amp;");
			tempBuilder = tempBuilder.append(temp);
		}
	}

	public StringBuilder getChannelBuilder() {
		return channelBuilder;
	}

	public StringBuilder getProgramBuilder() {
		return programBuilder;
	}

	public StringBuilder getScheduleBuilder() {
		return scheduleBuilder;
	}

	public StringBuilder getContentBuilder() {
		return contentBuilder;
	}

	public StringBuilder getProviderBuilder() {
		return providerBuilder;
	}

	public StringBuilder getMaterialBuilder() {
		return materialBuilder;
	}
	
	public StringBuilder getProgramScheduleBuilder() {
		return new StringBuilder().append(scheduleBuilder).append(programBuilder);
	}

	public Set<StringBuilder> getMaterialSet() {
		return materialSet;
	}

	public Set<StringBuilder> getProviderSet() {
		return providerSet;
	}
}

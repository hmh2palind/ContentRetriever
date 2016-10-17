package com.directv.edm.bussiness;

import static com.directv.edm.constant.FullTags.*;
import static com.directv.edm.util.Utils.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.directv.edm.dao.DeltaHandler;
import com.directv.edm.dao.FileProcesser;
import com.directv.edm.dao.FullHandler;
import com.directv.edm.domain.FileRequest;
import com.directv.edm.domain.FileResponse;

public class ContentRetrieverImpl implements ContentRetriever{
	final Logger logger = LoggerFactory.getLogger(ContentRetrieverImpl.class);
	
	private SAXParser saxParser;
	private FullHandler fullHandler;
	private DeltaHandler deltaHandler;
	private FileProcesser fileProcesser;
	
	public ContentRetrieverImpl(){
		this.fullHandler = new FullHandler();
		this.fileProcesser = new FileProcesser();
	}
	
	public FileResponse retrive(FileRequest request) {
		FileResponse response = new FileResponse();
		
		String tmsId = request.getTmsId();
		
		if (tmsId == null || tmsId.isEmpty()) {
			//#1. Generate random delta file
			byte typeContent = cacluateTypeContent(request);
			String filePath = fileProcesser.getFilePath(typeContent);
			if (filePath == null || filePath.isEmpty())
				return response;
			
			generateDeltaFile(response, filePath);
			return response;
		}
		
		//#2. Generate full & delta file from DLS file
		fullHandler.setTmsId(request.getTmsId());
		
		// 1. Read xml file
		File file = fileProcesser.getFullFile();
		String fullFile = null;
		try {
			// 2. retrieve Full File
			fullFile = retrieveFullFile(file);
			response.setFullFile(fullFile);
			
			// 3. generate full file
			fileProcesser.writeFullFile(fullFile);
			
			// 4. generate delta file
			if (request.isDeltaFile()) {
				generateDeltaFile(response, fileProcesser.getFilePath());
			}
			
		} catch (IOException e) {
			System.out.format("IO Exception: %s", e.getMessage());
		}
		
		return response;
	}

	private byte cacluateTypeContent(FileRequest request) {
		byte linear 	= request.isLinear() 	? (byte)0x8 : 0x0;
		byte nonLinear 	= request.isNonlinear() ? (byte)0x4 : 0x0;
		byte streaming 	= request.isStreaming() ? (byte)0x2 : 0x0;
		byte ott 		= request.isOtt() 		? (byte)0x1 : 0x0;

		return (byte)(linear ^ nonLinear ^ streaming ^ ott);
	}

	protected String retrieveFullFile(File file) {
		saxParser = getSAXParser();
		
		try {
			// 2. parse file
			saxParser.parse(file, fullHandler);

			// 2.1 Parse channel
			if (fullHandler.getChannelIdSet() != null && !fullHandler.getChannelIdSet().isEmpty()) {
				fullHandler.setStart(true);
				
				Set<String> tempChannelSet = new HashSet<String>();
				Iterator<String> it = fullHandler.getChannelIdSet().iterator();
				while (it.hasNext()) {
					tempChannelSet.add(OPEN_CHANNEL_ID + it.next() + CLOSE_CHANNEL_ID);
				}
				
				fullHandler.setChannelIdSet(tempChannelSet);
				saxParser.parse(file, fullHandler);
			}			
		} catch (SAXException e) {
			System.out.format("SAX Exception: %s", e.getMessage());
		} catch (IOException e) {
			System.out.format("IO Exception: %s", e.getMessage());
		}
		
		// 3. get final output
		return fullHandler.getFinalOutput();
	}

	protected void generateDeltaFile(FileResponse response, String filePath) {
		StringBuilder tempBuilder = null;
		Set<StringBuilder> builderSet = null;
		
		deltaHandler = new DeltaHandler();
		try {
			saxParser = getSAXParser();
			saxParser.parse(getInputsource(filePath), deltaHandler);
		} catch (SAXException e) {
			System.out.format("SAX Exception: %s", e.getMessage());
		} catch (IOException e) {
			System.out.format("IO Exception: %s", e.getMessage());
		}
		
		//1. create delta file for channel
		tempBuilder = deltaHandler.getChannelBuilder();
		String str;
		Set<String> strSet;
		if (isNotEmpty(tempBuilder)) 
		{
			str = fileProcesser.writeDeltaFile(CHANNEL_WRAPPER, tempBuilder, "100.xml");
			response.setChannel(str);
		}

		//2. create delta file for program
		tempBuilder = deltaHandler.getProgramScheduleBuilder(); 
		if (isNotEmpty(tempBuilder))
		{
			str = fileProcesser.writeDeltaFile(SCHEDULE_WRAPPER, tempBuilder, "300.xml");
			response.setProgram(str);
		}
		
		//3. create delta file for provider set
		builderSet = deltaHandler.getProviderSet(); 
		if (isNotEmpty(builderSet)) 
		{
			strSet = new HashSet<String>();
			Iterator<StringBuilder> it = builderSet.iterator();
			int i = 0;
			while (it.hasNext()) {
				tempBuilder = it.next();
				if (isNotEmpty(tempBuilder)) {
					str = fileProcesser.writeDeltaFile(NLPGWS_PROVIDER, tempBuilder, "20" + i + ".xml");
					strSet.add(str);
					i++;
				}
			}
			response.setProvider(strSet);
		}
		
		//4. create delta file for material set
		builderSet = deltaHandler.getMaterialSet(); 
		if (isNotEmpty(builderSet)) 
		{
			strSet = new HashSet<String>();
			Iterator<StringBuilder> it = builderSet.iterator();
			int i = 0;
			while (it.hasNext()) {
				tempBuilder = it.next();
				if (isNotEmpty(tempBuilder)) {
					str = fileProcesser.writeDeltaFile(NLPGWS_TITLE_INFO, tempBuilder, "60" + i + ".xml");
					strSet.add(str);
					i++;
				}
			}
			response.setMaterial(strSet);
		}
		
		//5. create delta file for content
		tempBuilder = deltaHandler.getContentBuilder(); 
		if (isNotEmpty(tempBuilder))
		{
			str = fileProcesser.writeDeltaFile(CONTENT_DETAIL_WRAPPER, tempBuilder, "500.xml");
			response.setContentDetails(str);
		}
	}

}
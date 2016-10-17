package com.directv.edm.rest;

import static com.directv.edm.constant.ContentConstants.FULL_PRFIX;
import static com.directv.edm.constant.ContentConstants.INVALID_PARAMETER;
import static com.directv.edm.util.Utils.getDateFormat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.directv.edm.bussiness.ContentRetriever;
import com.directv.edm.bussiness.ContentRetrieverImpl;
import com.directv.edm.domain.FileRequest;
import com.directv.edm.domain.FileResponse;

@SpringBootApplication
@RestController
@Configuration
@PropertySource("classpath:content-retriever.properties")
public class Rest extends SpringBootServletInitializer {

	@RequestMapping(name = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody
	FileResponse index(@RequestParam("contentid") String contentId) {
		FileResponse response = new FileResponse();

		response.setProgram(contentId);

		return response;
	}
	
	@RequestMapping("/retrieve")
	public /*String */ ResponseEntity<Object> hello(
			@RequestParam(value = "contentid", required = false) 	String tmsId,
			@RequestParam(value = "isott", required = false) 		boolean isOtt,
			@RequestParam(value = "isstreaming", required = false) 	boolean isStreaming,
			@RequestParam(value = "isnonlinear", required = false) 	boolean isNonlinear,
			@RequestParam(value = "islinear", required = false) 	boolean isLinear,
			@RequestParam(value = "isdeltafile", required = false) 	boolean isDeltaFile) 
	{
		String str = "";
		
		if (tmsId == null || tmsId.isEmpty() || isOtt || isStreaming || isNonlinear || isLinear)
		{
			FileRequest request = new FileRequest(tmsId, isOtt, isStreaming, isNonlinear, isLinear, isDeltaFile);
			ContentRetriever retriever = new ContentRetrieverImpl();
			FileResponse response = retriever.retrive(request);
		
			// full + delta
			if (request.getTmsId() != null && !request.getTmsId().isEmpty()) {
				str += response.getFullFile() + "\n";
				
				if (request.isDeltaFile()) {
					if (response.getChannel() != null && !response.getChannel().isEmpty()) {
						str += response.getChannel() + "\n";
						str += response.getProgram() + "\n";
					}
					
					if (response.getStrProvider() != null && !response.getStrProvider().isEmpty()) {
						str += response.getStrProvider() +  "\n";
						str += response.getStrMaterial() +  "\n";
					}
					
					str += response.getContentDetails() +  "\n";
				}
			}
			else {
				if (response.getChannel() != null && !response.getChannel().isEmpty()) {
					str += response.getChannel() + "\n";
					str += response.getProgram() + "\n";
				}
				
				if (response.getStrProvider() != null && !response.getStrProvider().isEmpty()) {
					str += response.getStrProvider() +  "\n";
					str += response.getStrMaterial() +  "\n";
				}			
				str += response.getContentDetails() +  "\n";
			}
		}
		
		else {
			str += INVALID_PARAMETER;
		}
		String fileName = FULL_PRFIX + getDateFormat() + "xxx.xml";
		HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("name", fileName);
        
        return new ResponseEntity<Object>(str, headers, HttpStatus.ACCEPTED);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Rest.class, args);
	}
}

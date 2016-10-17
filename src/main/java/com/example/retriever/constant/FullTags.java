package com.directv.edm.constant;

public class FullTags {
	// ------------------------------- Full File Tags -------------------------------
	public static final String CHANNEL		     = "channels";
	public static final String ASTC_CHANNEL	     = "atscChannels";
	public static final String PROGRAM 		     = "programs";
	public static final String SCHEDULE 	     = "schedules";
	public static final String ASTC_SCHEDULE     = "atscSchedules";
	public static final String CONTENT		     = "contentDetails";
	public static final String PROVIDER 	     = "providers";
	public static final String MATERIAL 	     = "publishedTitles";
	
	// open tags
	public static final String OPEN_CHANNEL_ID	 = "<channelID>";
	public static final String OPEN_PROGRAM_ID	 = "<programID>";
	public static final String OPEN_PROVIDER_ID  = "<providerNumericId>";
	public static final String OPEN_MATERIAL_ID  = "<programId>";
	
	// close tags
	public static final String CLOSE_CHANNEL_ID	 = "</channelID>";
	public static final String CLOSE_PROGRAM_ID	 = "</programID>";
	public static final String CLOSE_PROVIDER_ID = "</providerNumericId>";
	public static final String CLOSE_MATERIAL_ID = "</programId>";
	
	// ------------------------------- Data Set -------------------------------
	public static final String DATA_SET_FOLDER			= "data_set";
	public static final String CHANNEL_WRAPPER 			= "channelsWrapper.xml";
	public static final String SCHEDULE_WRAPPER 		= "schedulesWrapper.xml";
	public static final String CONTENT_DETAIL_WRAPPER 	= "contentDetailWrapper.xml";
	public static final String NLPGWS_PROVIDER 			= "nlpgwsProvider.xml";
	public static final String NLPGWS_TITLE_INFO		= "nlpgwsTitleInfo.xml";
}

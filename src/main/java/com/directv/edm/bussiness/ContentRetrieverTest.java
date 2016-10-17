package com.directv.edm.bussiness;

import com.directv.edm.domain.FileRequest;
import com.directv.edm.domain.FileResponse;

public class ContentRetrieverTest {
	private static ContentRetriever retriever;
	
	public static void main(String[] args) {
//		Set<String> tmsIdSet = new HashSet<String>();
		
		retriever = new ContentRetrieverImpl();
		
		FileRequest reqest = new FileRequest();
		reqest.setTmsId("EP001538810023");
		reqest.setDeltaFile(true);
		reqest.setLinear(true);
//		reqest.setNonlinear(true);
		
		FileResponse response = retriever.retrive(reqest);
		
		System.out.println(response.getFullFile());
		System.out.println(response.getContentDetails());
		
		long startTime = System.currentTimeMillis();
//		//linear
//		tmsIdSet = FileUtils.getLineSet(new File("data_set\\tmsid_linear.txt"));
//		Iterator<String> it = tmsIdSet.iterator();
//		while (it.hasNext()) {
//			String id = it.next();
//			hander = new FullFileHandler();
//			hander.setTmsId(id);
//			retriever = new ContentRetriever(hander);
//			retriever.retrive("linear\\" + id);
//		}
//		
//		//nonlinear
//		tmsIdSet = FileUtils.getLineSet(new File("data_set\\tmsid_nonlinear.txt"));
//		it = tmsIdSet.iterator();
//		while (it.hasNext()) {
//			String id = it.next();
//			hander = new FullFileHandler(id);
//			hander.setTmsId(id);
//			retriever = new ContentRetriever(hander);
//			retriever.retrive("nonlinear\\" + id);
//		}
//		
//		//streaming
//		tmsIdSet = FileUtils.getLineSet(new File("data_set\\tmsid_streaming.txt"));
//		it = tmsIdSet.iterator();
//		while (it.hasNext()) {
//			String id = it.next();
//			hander = new FullFileHandler();
//			hander.setTmsId(id);
//			retriever = new ContentRetriever(hander);
//			retriever.retrive("streaming\\" + id);
//		}
		
//		FullFileHandler hander = new FullFileHandler(tmsId);
//		ContentRetriever retriever = new ContentRetriever(hander, true);
		
//		retriever.retrive();
		System.out.printf("Time toal %d(s)", (System.currentTimeMillis()-startTime)/1000);
	}
}

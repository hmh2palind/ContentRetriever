package com.directv.edm.bussiness;

import com.directv.edm.domain.FileRequest;
import com.directv.edm.domain.FileResponse;

public interface ContentRetriever {
	public FileResponse retrive(FileRequest request);
}

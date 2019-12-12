package com.f2m.aquarius.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.f2m.aquarius.parameters.BatchParams;
import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FileService {
	
	private GeneralUtils gutils = new GeneralUtils();
    private static final Long MAX_FILE_SIZE = 10485760L; //10MB
    private ObjectMapper mapper = new ObjectMapper();
    private BatchService batch = new BatchService();
    
    public File download(String path) throws AquariusException {
    	File file = new File(path);
		if(!file.exists()){
			throw gutils.throwException(418, "");
        }
		return file;
    }
    
    private ObjectNode setDocumentVersion(String mimeType, String extension, long size) throws AquariusException {
		if (mimeType == null || mimeType.length() == 0
				|| extension == null) {
			throw gutils.throwException(404, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("mimeType", mimeType);
		newObject.put("extension", extension);
		newObject.put("size", size);
		return newObject;
	}
    
    public ObjectNode upload(MultipartFile file, String path) throws AquariusException {
        if (!file.isEmpty()) {
            if (belowMaxFileSize(file.getSize())) {
                try {
                    file.transferTo(new File(path));
                    String contentType = file.getContentType().toString().toLowerCase();
                    String fileName = file.getOriginalFilename();
                    String extension = "";
                    int indexOf = fileName.indexOf(".");
                    if (indexOf > -1) {
                    	extension = fileName.substring(indexOf + 1);
                    }
                    long size = file.getSize();
                    return setDocumentVersion(contentType, extension, size);
                } catch (IllegalStateException e) {
                	throw gutils.throwException(407, "");
                } catch (IOException e) {
                	throw gutils.throwException(407, "");
                }
            } else {
            	throw gutils.throwException(416, Long.toString(file.getSize()));
            }
        } else {
        	throw gutils.throwException(406, "");
        }
    }
    
    private Boolean belowMaxFileSize(Long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            return false;
        }
        
        return true;
    }
    
    public void setOCRBatch(String idDocument, String idVersion, String userId) throws AquariusException {
    	if (idDocument == null || idDocument.length() == 0
    			|| idVersion == null || idVersion.length() == 0
    			|| userId == null || userId.length() == 0) {
			throw gutils.throwException(1313, "");
		}
    	ObjectNode newObject = mapper.createObjectNode();
		newObject.put("nodeType", ECMParameters.N_TYPE_DOCUMENT);
		newObject.put("id", idDocument);
		newObject.put("versionId", idVersion);
		batch.setBatch(null, BatchParams.TYPE_OCR, 0, newObject, userId);
    }
}
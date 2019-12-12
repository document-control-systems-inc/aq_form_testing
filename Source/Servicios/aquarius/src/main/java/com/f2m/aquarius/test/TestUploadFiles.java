package com.f2m.aquarius.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;

import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.service.CmisService;
import com.f2m.aquarius.utils.AquariusException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestUploadFiles {

	private CmisService cmis = new CmisService();
	
	public void uploadFile(String prefix) throws AquariusException, IOException {
		File file = new File("/Users/gomado/Desktop/notas_bpm.txt");
	    FileInputStream input = new FileInputStream(file);
	    MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "text/plain", IOUtils.toByteArray(input));
	    
		long time_start, time_end;
		time_start = System.currentTimeMillis();
    	String idDoc = cmis.setDocument(null, "/Aquarius/Repository/carpeta 2", null, null, "1", "{\"title\": \"" + prefix + ".txt\"}", multipartFile, ECMParameters.TEST_SYSTEM);
    	time_end = System.currentTimeMillis();
		System.out.println("Tiempo: " + (time_end - time_start) + " milliseconds. ID: " + idDoc);
	}
	
	public void uploadNewContentVersion (String idDoc) throws AquariusException, IOException {
		File file = new File("/Users/gomado/Desktop/notas_bpm.txt");
	    FileInputStream input = new FileInputStream(file);
	    MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "text/plain", IOUtils.toByteArray(input));
	    long time_start, time_end;
		time_start = System.currentTimeMillis();
		idDoc = cmis.setDocument(idDoc, null, null, null, null, null, multipartFile, ECMParameters.TEST_SYSTEM);
		time_end = System.currentTimeMillis();
		System.out.println("Tiempo: " + (time_end - time_start) + " milliseconds. ID: " + idDoc);
	}
	
	public void uploadaLotofFiles(String prefix, int max) throws AquariusException, IOException {
		long time_startTotal, time_endTotal;
		time_startTotal = System.currentTimeMillis();
	    for (int i = 0; i < max; i++) {
			uploadFile(prefix + i);
	    }
	    time_endTotal = System.currentTimeMillis();
	    System.out.println("---------------------------------------------------------------");
	    System.out.println("Tiempo: " + (time_endTotal - time_startTotal) + " milliseconds.");
	}
	
	public void uploadNewMetadataVersion(String idDoc, String documentCassId, String metadata) throws AquariusException {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		idDoc = cmis.setDocument(idDoc, null, null, null, documentCassId, metadata, null, ECMParameters.TEST_SYSTEM);
		time_end = System.currentTimeMillis();
		System.out.println("Tiempo: " + (time_end - time_start) + " milliseconds. ID: " + idDoc);
	}
	
	public void listFolderContent(String idFolder, String userId) throws AquariusException {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		ObjectNode list = cmis.getFolderList(idFolder, userId);
		if (list != null ) {
			System.out.println("Se encontraron " + list.size() + " renglones");
		}
		time_end = System.currentTimeMillis();
		System.out.println("Tiempo: " + (time_end - time_start) + " milliseconds. ID: " + idFolder);
	}
	
	public void setStamp() throws AquariusException, IOException {
		File file = new File("/Users/gomado/Documents/F2M/Aquarius/Repositorio F2M/aquarius/Vagrant/provisioning/services/stamp/default_recibido");
	    FileInputStream input = new FileInputStream(file);
	    MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "image/png", IOUtils.toByteArray(input));
		String id = cmis.setStamp("default_recibido", "Default Recibido", multipartFile, "ecm");
		System.out.println("Se guardó el sello: " + id);
	}
	
	public static void main (String[] args) {
		
		try {
			TestUploadFiles test = new TestUploadFiles();
		    //test.uploadNewMetadataVersion("6b476549-2b4d-4566-937b-01accef8e0f4", "1", "{\"title\": \"testV231001.txt\"}");
			//test.uploadFile("prueba01");
			//test.uploadNewContentVersion("6b476549-2b4d-4566-937b-01accef8e0f4");
			//test.listFolderContent("3b7de7f5-3a96-4238-baca-081187c9ae23", "ecm");
			test.setStamp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

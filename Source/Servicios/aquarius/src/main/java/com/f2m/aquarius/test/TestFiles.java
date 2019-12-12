package com.f2m.aquarius.test;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import com.f2m.aquarius.service.CmisService;
import com.f2m.aquarius.utils.AquariusException;

public class TestFiles {

	private CmisService cmis = new CmisService();
	
	public void testFolderList(String id, String userId) throws AquariusException {
		System.out.println(cmis.getFolderList(id, userId));
	}
	
	public void testMultiSelectTree(String userId) throws AquariusException {
		System.out.println(cmis.getMultiSelectTree(userId));
	}
	
	public void testGetMetadata(String id, String idMetadata, int version, boolean lastVersion, String userId) throws AquariusException {
		System.out.println(cmis.getDocumentMetadata(id, idMetadata, version, lastVersion, userId));
	}
	
	public void testGetVersions(String id, String versionId, int numVersion, boolean lastVersion, String userId) throws AquariusException {
		System.out.println(cmis.getDocumentContent(id, versionId, numVersion, lastVersion, userId));
	}
	
	public ImageIcon getThumbnail(String fileName) throws AquariusException {
		ImageIcon imageIcon = new ImageIcon(fileName);
		System.out.println(imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());
		
		Image image = imageIcon.getImage().getScaledInstance(10,10,Image.SCALE_SMOOTH);
		
		
		return imageIcon;
	}
	
	public static void main(String[] args) {
		TestFiles test = new TestFiles();
		try {
			//test.testFolderList("13aff519-5576-495a-bffa-edc1fb16bbc4");
			//test.testMultiSelectTree();
			//"100aefae-d481-4b2a-88f7-02b538c6d07c"
			//"97aafe33-1c4b-4e47-9ed4-72e3787bb721"
			//"03e83a44-6989-4906-b02d-42ee9b8c5899"
			//test.testGetVersions(null, "03e83a44-6989-4906-b02d-42ee9b8c5899", 0, true);
			test.getThumbnail("/opt/ecm/sample/sample.jpg");
		} catch (AquariusException e) {
			e.printStackTrace();
		}

	}

}

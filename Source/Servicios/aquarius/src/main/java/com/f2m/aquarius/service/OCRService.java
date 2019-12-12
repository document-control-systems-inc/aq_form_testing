package com.f2m.aquarius.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;

import com.f2m.aquarius.parameters.BatchParams;
import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.PDFBoxThumbnailer;
//TODO:
//import de.uni_siegen.wineme.come_in.thumbnailer.util.mime.MimeTypeDetector;

public class OCRService {
	
	private ObjectMapper mapper = new ObjectMapper();
	private CmisService cmis = new CmisService();
	private GeneralUtils gutils = new GeneralUtils();
	private DBService bd = new DBService();
	private BatchService batch = new BatchService();
	//TODO: Regresar
	//private MimeTypeDetector mimeTypeDetector = new MimeTypeDetector();
	
	
	private ArrayNode readFileForOCR(String filePath) throws AquariusException {
		ArrayNode response = mapper.createArrayNode();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			try {
			    String line = br.readLine();
			    while (line != null) {
			    	ArrayNode newObject = processOCRLine(line);
			    	if (newObject.size() > 0) {
			    		response.addAll(newObject);
			    	}
			        line = br.readLine();
			    }
			} finally {
			    br.close();
			}
		} catch (Exception e) {
			throw gutils.throwException(1309, filePath);
		}
		return response;
	}
	
	private boolean writeFileForOCR(String filePath, String content) throws AquariusException {
		boolean response = false;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			try {
				bw.write(content);
				response = true;
			} finally {
				bw.close();
			}
		} catch(Exception e) {
			throw gutils.throwException(1315, filePath);
		}
		return response;
	}
	
	private ArrayNode processOCRLine(String line) {
		ArrayNode newObject = mapper.createArrayNode();
		String[] parsed = line.split("[ ]+");
		for (String word : parsed) {
			if (word.length() > 1) {
				word = word.toLowerCase();
				//TODO: Validar si es email o url
				String cadenaNormalize = Normalizer.normalize(word, Normalizer.Form.NFD);
				String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
				String cadenaSinPuntuacion = cadenaSinAcentos.replaceAll("\\p{Punct}", "");
				if (cadenaSinPuntuacion.length() > 1) {
					newObject.add(cadenaSinPuntuacion);
				}
			}
		}
		return newObject;
	}
	
	private String executeCommand(String[] command, String path) throws AquariusException {
		StringBuffer output = new StringBuffer();
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
			if (path != null) {
				File directory = new File(path);
				if (directory.isDirectory() && directory.exists()) {
					builder.directory(directory);
				}
			}
			builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            //System.out.println(line);
	            output.append(line + "\n");
	        }
	        int returnCode = p.waitFor();
	        output.append("[Utils - ExecuteCode]:" + returnCode);
		} catch (Exception e) {
			throw gutils.throwException(1310, "");
		}
        return output.toString();
		
	}
	
	private ArrayNode executeTesseract(String imagePath, String txtPath) throws AquariusException {
		ArrayNode response = mapper.createArrayNode();
		String cmdResponse = executeCommand(new String[] {"tesseract",  imagePath,  txtPath, "-l", "spa+eng", "-psm", "3"}, null);
		String fileTxtPath = txtPath + ".txt";
		int indexOf = cmdResponse.indexOf(BatchParams.RESULT_CODE);
		if (indexOf > -1) {
			String code = cmdResponse.substring(indexOf + BatchParams.RESULT_CODE.length());
			int codeNumber = Integer.parseInt(code);
			if (codeNumber == 0) {
				response = readFileForOCR(fileTxtPath);
			}
		}
		File file = new File(fileTxtPath);
		file.delete();
		return response;
	}
	
	private ArrayNode getFileForOCR(String idVersion, String userId) throws AquariusException {
		if (idVersion == null || idVersion.length() == 0) {
			throw gutils.throwException(1308, "");
		}
		JsonNode cmisFile = cmis.getDocumentContentPath(null, idVersion, 0, true, userId);
		if (cmisFile != null && cmisFile.findValue("path") != null) {
			return processSingleFiles(cmisFile.findValue("path").asText());
		} else {
			throw gutils.throwException(1308, "File Path missing");
		}
	}
	
	private boolean getDocumentOCR(String id, String idVersion) throws AquariusException {
		List<JsonNode> versiones = cmis.getDocumentContent(id, idVersion, -1, false, ECMParameters.ECM_SYSTEM);
		if (versiones != null) {
			for (JsonNode version:versiones) {
				//System.out.println("Procesando: " + version.path("versionId").asText() + "\" + version.path("versionId").asText());
				ArrayNode ocr = getFileForOCR(version.path("versionId").asText(), ECMParameters.ECM_SYSTEM);
				ObjectNode newObject = mapper.createObjectNode();
				newObject.put("id", version.path("id").asText());
				newObject.put("versionId", version.path("versionId").asText());
				newObject.putPOJO("ocr", ocr);
				deleteOCR(version.path("id").asText(), version.path("versionId").asText());
				return bd.insertJson("dococr", newObject);
			}
		}
		return false;
	}

	private boolean deleteOCR(String idDoc, String idVersion) throws AquariusException {
		if (idDoc == null || idDoc.length() == 0) {
			throw gutils.throwException(1311, "");
		}
		String where = "data->>'id' = '" + idDoc + "' ";
		if (idVersion != null && idVersion.length() > 0) {
			where += " and data->>'versionId' = '" + idVersion + "' ";
		}
		return bd.deleteJson("dococr", where);
	}
	
	private boolean processPropertiesOCR(JsonNode properties) throws AquariusException {
		if (properties == null) {
			throw gutils.throwException(1312, "");
		}
		switch(properties.path("nodeType").asText()) {
			case ECMParameters.N_TYPE_DOCUMENT:
				return getDocumentOCR(properties.path("id").asText(), properties.path("versionId").asText());
			case ECMParameters.N_TYPE_FOLDER:
				System.out.println("Se procesa folder...");
				//TODO Falta hacer el procesamiento del folder...
				return true;
		}
		return false;
	}
	
	public void processOCRByBatch() throws AquariusException {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		List<JsonNode> batchPendientes = batch.getBatch(null, BatchParams.TYPE_OCR);
		int numProcesados = 0;
		int numExitosos = 0;
		int numErrores = 0;
		if (batchPendientes != null) {
			numProcesados = batchPendientes.size();
			for (JsonNode batchProcess:batchPendientes) {
				switch(batchProcess.path("periodicity").asInt()) {
					case 0:
						if (processPropertiesOCR(batchProcess.path("properties"))) {
							if (batch.deleteBatch(batchProcess.path("id").asText(), ECMParameters.ECM_SYSTEM)) {
								numExitosos++;
							} else {
								numErrores++;
							}
						} else {
							numErrores++;
						}
						break;
					default:
						numErrores++;
				}
			}
		}
		time_end = System.currentTimeMillis();
		if (numProcesados > 0) {
			System.out.println("[Proceso OCR] " + gutils.getTimeString(gutils.getTime()) + "\tTiempo de Ejecución: " + ((time_end - time_start)/1000) + " segundos.\tProcesados: " + numProcesados + "\tExitosos: " + numExitosos + "\tErrores: " + numErrores);
		}
	}
	
	private String getMimeType(String path) {
		File in = new File(path);
		//TODO:
		//return mimeTypeDetector.getMimeType(in);
		return "image/png";
	}
	
	private ArrayNode getPDFOCR(String path) throws AquariusException {
		ArrayNode response = mapper.createArrayNode();
		try {
			PDDocument document = PDDocument.load(new File(path));
			if (!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(false);
				PDFTextStripper tStripper = new PDFTextStripper();
				String pdfFileInText = tStripper.getText(document);
				String auxCompare = pdfFileInText.trim();
				auxCompare = auxCompare.replaceAll("\\r?\\n", " ");
				if (auxCompare.length() == 0) {
					//TODO convertir a imagen y procesar
					List<?> pages = document.getDocumentCatalog().getAllPages();
					for (int i = 0; i < pages.size(); i++) {
						PDPage page = (PDPage)pages.get(i);
				    	PDFBoxThumbnailer thumb = new PDFBoxThumbnailer();
				    	BufferedImage tmpImage = thumb.convertToImage(page, BufferedImage.TYPE_INT_RGB, 1500,1500);
				    	File output = new File(path + "_" + i + ".png");
				    	ImageIO.write(tmpImage, "PNG", output);
				    	response.addAll(processSingleFiles(output.getAbsolutePath()));
				    	output.delete();
					}
			    	
				} else {
					if (writeFileForOCR(path + ".txt", pdfFileInText)) {
						response = readFileForOCR(path + ".txt");
						File output = new File(path + ".txt");
						output.delete();
					} else {
						throw gutils.throwException(1314, "txt file");
					}
				}
			} else {
				throw gutils.throwException(1314, "PDF is Encrypted");
			}
		} catch (IOException e) {
			throw gutils.throwException(1314, e.getMessage());
		}
		return response;
	}
	
	public ArrayNode processSingleFiles(String path) throws AquariusException {
		String mimeType = getMimeType(path);
		if (mimeType != null) {
			if (mimeType.startsWith("image/")) {
				return executeTesseract(path, path);
			} else {
				if (mimeType.contains("pdf")) {
					return getPDFOCR(path);
				} else if (mimeType.startsWith("text/")) {
					return readFileForOCR(path);
				} else if (mimeType.startsWith("application/")) {
					System.out.println("Se distingue si es word o excel");
				}
				ArrayNode response = mapper.createArrayNode();
				return response;
			}
		} else {
			return mapper.createArrayNode();
		}
	}
	
	public static void main (String[] args) {
		OCRService ocr = new OCRService();
		try {
			//ocr.processOCRByBatch();
			//ocr.processSingleFiles("C:/Users/gomado/Documents/F2M/Aquarius/Repositorio F2M/aquarius/Source/Servicios/aquarius/thumbs/pdf-2hojas_pdf.png");
			//ocr.processSingleFiles("/opt/ecm/sample/pdf-2hojas.pdf");
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/Acta de Entrega_CFN_MediosInstalacion.docx"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/Customizing_and_Extending_IBM_Content_Navigator.pdf"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/pdf-2hojas.pdf"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/Importe.pdf"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/ExITAM.tif"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.xml"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.zip"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.txt"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/20170701_171248.jpg"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.gif"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.png"));
			System.out.println("--");
			System.out.println(ocr.processSingleFiles("/opt/ecm/sample/sample.doc"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.f2m.aquarius.service;

import java.io.FileOutputStream;
import java.io.IOException;

import com.f2m.aquarius.utils.AquariusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFService {

	public String createPDFForm(JsonNode template, JsonNode values, boolean landscape) throws AquariusException {
		String fileName = "/IBM/WebSphere/AppServer/ADN/tmp/PDF_" + values.findValue("docName").asText() + ".pdf";
		try {
			Document document = null;
			if (landscape) {
				document = new Document(PageSize.A4.rotate());
			} else {
				document = new Document();
			}
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			addMetaData(document, "Aquarius PDF Forms");
			for (JsonNode pageTemplate:template.findValue("form")) {
				addPage(writer, document, pageTemplate, values);
			}
			document.close();
			return fileName;
		} catch (Exception e) {
			throw new AquariusException("Error al generar el archivo temporal PDF " + e.getMessage());
		}
	}
	
	private void addMetaData(Document document, String title) {
		document.addTitle(title);
		document.addSubject("");
		document.addKeywords("");
		document.addAuthor("Aquarius Magic Folder");
		document.addCreator("Aquarius Magic Folder");
	}
	
	private BaseFont getFont(String fontName) throws DocumentException, IOException {
		String font;
		switch (fontName) {
			case BaseFont.COURIER:
			case BaseFont.COURIER_BOLD:
			case BaseFont.HELVETICA:
			case BaseFont.HELVETICA_BOLD:
			case BaseFont.TIMES_BOLD:
			case BaseFont.TIMES_BOLDITALIC:
			case BaseFont.TIMES_ITALIC:
			case BaseFont.TIMES_ROMAN:
				font = fontName;
				break;
			default:
				font = BaseFont.TIMES_ROMAN;
		}
		return BaseFont.createFont(font, 
				BaseFont.CP1257, BaseFont.EMBEDDED);
		
	}
	
	private void addText(String value, JsonNode textParams, PdfWriter writer, Document document) throws DocumentException, IOException {
		PdfContentByte over = writer.getDirectContent();
		over.beginText();
		over.setFontAndSize(getFont(textParams.findValue("font").asText()), textParams.findValue("fontSize").asInt());
		over.setTextMatrix(textParams.findValue("xcoord").asInt(), document.getPageSize().getHeight() - textParams.findValue("ycoord").asInt());
		over.showText(value);
		over.endText();
	}
	
	private void addImage(String imagePath, JsonNode imageParams, Document document) throws DocumentException, IOException {
		Image img_sign = null;
		if (imagePath.startsWith("data:")) {
			String base64Image = imagePath.split(",")[1];
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			img_sign = Image.getInstance(imageBytes);
		} else {
			img_sign = Image.getInstance(imagePath);
		}
		
		if (imageParams.findValue("scaleX").asInt() > 0) {
			img_sign.scaleToFit(imageParams.findValue("scaleX").asInt(), imageParams.findValue("scaleY").asInt());
		}
		img_sign.setAbsolutePosition(imageParams.findValue("xcoord").asInt(), document.getPageSize().getHeight() - imageParams.findValue("ycoord").asInt());
		document.add(img_sign);
	}
	
	private void addBackgroundImage(String imagePath, Document document) throws IOException, DocumentException {
		System.out.println("x:" + document.getPageSize().getWidth() + ",y: " + document.getPageSize().getHeight());
		float documentWidth = document.getPageSize().getWidth();
		float documentHeight = document.getPageSize().getHeight();
		Image img_header = Image.getInstance(imagePath);
		img_header.scaleToFit(documentWidth, documentHeight);
		img_header.setAbsolutePosition(0, 0);
		document.add(img_header);
	}
	
	private String findParam(JsonNode values, String paramName) {
		for (JsonNode value:values) {
			if (value.findValue("param").asText().equals(paramName)) {
				return value.findValue("value").asText();
			}
		}
		return null;
	}
	
	private void addPage(PdfWriter writer, Document document, JsonNode pageTemplate, JsonNode values) throws DocumentException, IOException {
		document.newPage();
		addBackgroundImage(pageTemplate.findValue("image").asText(), document);
		for(JsonNode params:pageTemplate.findValue("params")) {
			String paramName = params.findValue("name").asText();
			String value = findParam(values.findValue("values"), paramName);
			if (value != null) {
				switch (params.findValue("type").asText()) {
					case "text":
						addText(value, params, writer, document);
						break;
					case "image":
						addImage(value, params, document);
						break;
				}
			}
		}
	}
}

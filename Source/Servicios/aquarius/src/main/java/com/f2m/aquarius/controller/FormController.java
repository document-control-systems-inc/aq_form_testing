package com.f2m.aquarius.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.service.PDFService;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class FormController {

	private GeneralUtils gutils = new GeneralUtils();
	private ObjectMapper mapper = new ObjectMapper();
	private PDFService pdfService = new PDFService();
	
	@RequestMapping(value = "/form", method = RequestMethod.GET, headers = "Accept=application/json", produces = "text/html")
	public String getFormHtml(HttpServletResponse httpResponse) {
		//response.setContentType("text/html");
		httpResponse.setCharacterEncoding("UTF-8");
		
		return "<html><head></head><body><img src=\"http://aquarius.f2m.com.mx/form1.jpg\" alt=\"Form01\" width=\"595\" height=\"842\" border=\"0\" usemap=\"#form01\"><form action=\"http://localhost:9090/getFormPdf\" method=\"post\"><input type=\"hidden\" name=\"template\" value=\"template01\"><input type=\"hidden\" name=\"docName\" value=\"ejemplo001\"><input style=\"position:absolute;top:91px;left:468px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_1\"><input style=\"position:absolute;top:91px;left:482px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_2\"><input style=\"position:absolute;top:91px;left:496px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_3\"><input style=\"position:absolute;top:91px;left:510px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_4\"><input style=\"position:absolute;top:91px;left:524px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_5\"><input style=\"position:absolute;top:91px;left:538px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_6\"><input style=\"position:absolute;top:91px;left:552px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_7\"><input style=\"position:absolute;top:91px;left:566px;width:13px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"codigo_cea_8\"><input style=\"position:absolute;top:117px;left:162px;width:185px;height:14px;background-color:#efe4b0;font-size:7pt;border:1px solid #ff0000\" type=\"text\" name=\"nombre_CEA\"><input style=\"position:absolute;top:137px;left:162px;width:185px;height:14px;background-color:#efe4b0;font-size:7pt;border:1px solid #ff0000\" type=\"text\" name=\"nombre_subcentro\"><input style=\"position:absolute;top:156px;left:162px;width:185px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"direccion_cea\"><input style=\"position:absolute;top:175px;left:162px;width:185px;height:14px;background-color:#efe4b0;font-size:7pt\" type=\"text\" name=\"distrito_educativo\"><input style=\"position:absolute;top:130px;left:391px;width:14px;height:14px;background-color:#efe4b0;cursor:pointer;\" type=\"radio\" name=\"dependencia\" value=\"fiscal\"><input style=\"position:absolute;top:130px;left:482px;width:14px;height:14px;background-color:#efe4b0;cursor:pointer;\" type=\"radio\" name=\"dependencia\" value=\"convenio\"><input style=\"position:absolute;top:130px;left:550px;width:14px;height:14px;background-color:#efe4b0;cursor:pointer;\" type=\"radio\" name=\"dependencia\" value=\"privada\"><input style=\"position:absolute;top:171px;left:391px;width:14px;height:14px;background-color:#efe4b0;cursor:pointer;\" type=\"radio\" name=\"areafuncionamiento\" value=\"rural\"><input style=\"position:absolute;top:171px;left:482px;width:14px;height:14px;background-color:#efe4b0;cursor:pointer;\" type=\"radio\" name=\"areafuncionamiento\" value=\"urbano\"><input style=\"position:absolute;top:535px;left:580px;width:18px;height:18px;background-color:#efe4b0;cursor:pointer;\" type=\"checkbox\" name=\"requisito_matrimonio\"><input style=\"position:absolute;top:555px;left:580px;width:18px;height:18px;background-color:#efe4b0;cursor:pointer;\" type=\"checkbox\" name=\"requisito_nacimiento\"><input style=\"position:absolute;top:575px;left:580px;width:18px;height:18px;background-color:#efe4b0;cursor:pointer;\" type=\"checkbox\" name=\"requisito_militar\"><input style=\"position:absolute;top:596px;left:580px;width:18px;height:18px;background-color:#efe4b0;cursor:pointer;\" type=\"checkbox\" name=\"requisito_testigos\"><select style=\"position:absolute;top:316px;left:370px;width:27px;height:16px;background-color:#efe4b0;font-size:7pt;border:1px solid #ff0000;-webkit-appearance: none; -moz-appearance: none; text-indent: 1px;text-overflow: '';\" name=\"nacimiento_dia\"><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select><select style=\"border:1px solid #ff0000;position:absolute;top:316px;left:402px;width:27px;height:16px;background-color:#efe4b0;font-size:7pt;-webkit-appearance: none; -moz-appearance: none; text-indent: 1px;text-overflow: '';\" name=\"nacimiento_mes\"><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select><select style=\"border:1px solid #ff0000;position:absolute;top:316px;left:433px;width:54px;height:16px;background-color:#efe4b0;font-size:7pt;-webkit-appearance: none; -moz-appearance: none; text-indent: 1px;text-overflow: '';\" name=\"nacimiento_año\"><option>2000</option><option>2001</option><option>2002</option><option>2003</option><option>2004</option></select><input type=\"submit\" value=\"Enviar\"></form></body></html>";
	}
	
	
	@RequestMapping(value = "/getFormPdfClaro")
	public void getFormDataClaro(HttpSession session, HttpServletRequest request, HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			String docName = "";
			Map<String,String[]> parameters = request.getParameterMap();
			ObjectNode newData = mapper.createObjectNode();
			ArrayNode values = newData.putArray("values");
			for(String key:parameters.keySet()) {
				if (request.getParameter(key) != null) {
					if (key.equals("template")) {
						newData.put("template", request.getParameter(key).toString());
					} else if (key.equals("docName")) {
						newData.put("docName", request.getParameter(key).toString());
						docName = request.getParameter(key).toString();
					} else {
						ObjectNode newObject = mapper.createObjectNode();
						newObject.put("param", key);
						newObject.put("value", request.getParameter(key).toString());
						System.out.println(newObject);
						values.add(newObject);
					}
				}
			}
			System.out.println(newData.toString());
			String txtTemplate = "{\"name\": \"sampleTemplate\",\"form\": "
					+ "[{\"image\": \"/IBM/WebSphere/AppServer/ADN/files/formClaro.jpg\",\"params\": "
					
					
					+ "[{\"type\": \"text\",\"name\": \"contratoCredito\",\"xcoord\": 42,\"ycoord\": 167,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"claroUpdate\",\"xcoord\": 42,\"ycoord\": 183,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"claroFinanciamiento\",\"xcoord\": 42,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"bringYourOwnPhone\",\"xcoord\": 42,\"ycoord\": 216,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"garantia07\",\"xcoord\": 42,\"ycoord\": 229,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"garantiaEquipo\",\"xcoord\": 42,\"ycoord\": 241,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"compraAccesorios\",\"xcoord\": 42,\"ycoord\": 254,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"orientacion\",\"xcoord\": 42,\"ycoord\": 267,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"pagoPenalidad\",\"xcoord\": 42,\"ycoord\": 279,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"servicioFWA\",\"xcoord\": 42,\"ycoord\": 295,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"equipoPreOwned\",\"xcoord\": 42,\"ycoord\": 315,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"proteccionMovil\",\"xcoord\": 42,\"ycoord\": 328,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"orientacionFactura\",\"xcoord\": 42,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"email\",\"xcoord\": 335,\"ycoord\": 339,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"miClaro\",\"xcoord\": 42,\"ycoord\": 352,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"nombreCliente\",\"xcoord\": 280,\"ycoord\": 702,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"fechaDocumento\",\"xcoord\": 280,\"ycoord\": 740,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"numeroCelular\",\"xcoord\": 280,\"ycoord\": 760,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"text\",\"name\": \"numeroEmpleado\",\"xcoord\": 280,\"ycoord\": 800,\"font\": \"Courier\",\"fontSize\": 10}"
					+ ",{\"type\": \"image\",\"name\": \"firmaCliente\",\"xcoord\": 350,\"ycoord\": 726,\"scaleX\": 600,\"scaleY\": 25}"
					+ "]}]}";    
			JsonNode template = mapper.readTree(txtTemplate);
			File file = new File(pdfService.createPDFForm(template, newData, false));
			if (file.exists()) {
				//InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        //httpResponse.setContentLength((int)file.length());
		        //String mimeType= "application/pdf";
		        //httpResponse.setContentType(mimeType);
		        //FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        httpResponse.setContentLength((int)file.length());
		        String mimeType= "application/pdf";
		        httpResponse.setContentType(mimeType);
		        httpResponse.setHeader("Content-Disposition","inline; filename=\"" + docName + ".pdf" + "\"");
		        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
		        inputStream.close();
		        httpResponse.flushBuffer();
			} else {
				response.setStatus(1);
				response.setExito("Error al generar el PDF");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			response.setStatus(1);
			response.setExito("Error al generar el PDF");
		//} catch (AquariusException e) {
		//	response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
	}
	
	
	@RequestMapping(value = "/getFormPdfPrime")
	public void getFormDataPrime(HttpSession session, HttpServletRequest request, HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			String docName = "";
			Map<String,String[]> parameters = request.getParameterMap();
			ObjectNode newData = mapper.createObjectNode();
			ArrayNode values = newData.putArray("values");
			String templateKey = "";
			for(String key:parameters.keySet()) {
				if (request.getParameter(key) != null) {
					if (key.equals("template")) {
						newData.put("template", request.getParameter(key).toString());
						templateKey = request.getParameter(key).toString();
					} else if (key.equals("docName")) {
						newData.put("docName", request.getParameter(key).toString());
						docName = request.getParameter(key).toString();
					} else {
						ObjectNode newObject = mapper.createObjectNode();
						newObject.put("param", key);
						newObject.put("value", request.getParameter(key).toString());
						System.out.println(newObject);
						values.add(newObject);
					}
				}
			}
			
			String txtTemplate = "";
			String txt_image ="";
			if (templateKey.equals("inspeccion")) {
				txt_image = "_inspeccion";
				txtTemplate = "{\"name\": \"sampleTemplate\",\"form\": "
						+ "[{\"image\": \"/IBM/WebSphere/AppServer/ADN/files/frmJanitorial" + txt_image + "1.jpg\",\"params\": "
						
						+ "[{\"type\": \"text\",\"name\": \"apariencia_1\",\"xcoord\": 192,\"ycoord\": 148,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"alfombra_1\",\"xcoord\": 192,\"ycoord\": 159,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"puntos_1\",\"xcoord\": 192,\"ycoord\": 375,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"tareas_1\",\"xcoord\": 192,\"ycoord\": 386,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"eficiencia_1\",\"xcoord\": 192,\"ycoord\": 397,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"total_puntos\",\"xcoord\": 560,\"ycoord\": 375,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"total_tareas\",\"xcoord\": 560,\"ycoord\": 386,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"total_eficiencia\",\"xcoord\": 560,\"ycoord\": 397,\"font\": \"Courier\",\"fontSize\": 6}"
						
						//+ ",{\"type\": \"image\",\"name\": \"firmaCliente\",\"xcoord\": 350,\"ycoord\": 726,\"scaleX\": 600,\"scaleY\": 25}"
						+ "]}"
						+ "]}";  
			} else {
				if (templateKey.equals("requisicion")) {
					txt_image = "_requisicion";
				} else if (templateKey.equals("devolucion")) {
					txt_image = "_devolucion";
				} 
				txtTemplate = "{\"name\": \"sampleTemplate\",\"form\": "
						+ "[{\"image\": \"/IBM/WebSphere/AppServer/ADN/files/frmJanitorial" + txt_image + "1.jpg\",\"params\": "
						
						+ "[{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 97,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 97,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_ea\",\"xcoord\": 320,\"ycoord\": 105,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_cs\",\"xcoord\": 355,\"ycoord\": 105,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 320,\"ycoord\": 112,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_cs\",\"xcoord\": 355,\"ycoord\": 112,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80070_ea\",\"xcoord\": 320,\"ycoord\": 119,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80070_cs\",\"xcoord\": 355,\"ycoord\": 119,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_ea\",\"xcoord\": 320,\"ycoord\": 126,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_cs\",\"xcoord\": 355,\"ycoord\": 126,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_ea\",\"xcoord\": 320,\"ycoord\": 134,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_cs\",\"xcoord\": 355,\"ycoord\": 134,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_ea\",\"xcoord\": 320,\"ycoord\": 142,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_cs\",\"xcoord\": 355,\"ycoord\": 142,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80135_ea\",\"xcoord\": 320,\"ycoord\": 149,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80135_cs\",\"xcoord\": 355,\"ycoord\": 149,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_ea\",\"xcoord\": 320,\"ycoord\": 157,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_cs\",\"xcoord\": 355,\"ycoord\": 157,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_ea\",\"xcoord\": 320,\"ycoord\": 164,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_cs\",\"xcoord\": 355,\"ycoord\": 164,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_ea\",\"xcoord\": 320,\"ycoord\": 172,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_cs\",\"xcoord\": 355,\"ycoord\": 172,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80085_ea\",\"xcoord\": 320,\"ycoord\": 179,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80085_cs\",\"xcoord\": 355,\"ycoord\": 179,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 187,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 187,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 211,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 211,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 218,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 218,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 226,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 226,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 234,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 234,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 242,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 242,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 249,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 249,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 256,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 256,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 264,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 264,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 279,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 279,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 287,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 287,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 295,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 295,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 302,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 302,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 310,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 310,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 317,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 317,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 325,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 325,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 332,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 332,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 340,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 340,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 355,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 355,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 363,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 363,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 370,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 370,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 378,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 378,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 393,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 393,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 400,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 400,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 415,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 415,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 430,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 430,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 438,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 438,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 445,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 445,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 453,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 453,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 460,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 460,\"font\": \"Courier\",\"fontSize\": 6}"

						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 320,\"ycoord\": 475,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 475,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 320,\"ycoord\": 483,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"t1_subtotal_ea\",\"xcoord\": 320,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"t1_subtotal_cs\",\"xcoord\": 355,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						
						// columna 2
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 97,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 97,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_ea\",\"xcoord\": 655,\"ycoord\": 105,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_cs\",\"xcoord\": 690,\"ycoord\": 105,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 655,\"ycoord\": 112,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_cs\",\"xcoord\": 690,\"ycoord\": 112,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80070_ea\",\"xcoord\": 655,\"ycoord\": 119,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80070_cs\",\"xcoord\": 690,\"ycoord\": 119,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_ea\",\"xcoord\": 655,\"ycoord\": 126,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_cs\",\"xcoord\": 690,\"ycoord\": 126,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_ea\",\"xcoord\": 655,\"ycoord\": 134,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_cs\",\"xcoord\": 690,\"ycoord\": 134,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_ea\",\"xcoord\": 655,\"ycoord\": 142,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_cs\",\"xcoord\": 690,\"ycoord\": 142,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_ea\",\"xcoord\": 655,\"ycoord\": 157,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_cs\",\"xcoord\": 690,\"ycoord\": 157,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_ea\",\"xcoord\": 655,\"ycoord\": 164,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_cs\",\"xcoord\": 690,\"ycoord\": 164,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_ea\",\"xcoord\": 655,\"ycoord\": 172,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_cs\",\"xcoord\": 690,\"ycoord\": 172,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80085_ea\",\"xcoord\": 655,\"ycoord\": 179,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 187,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 187,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 211,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 211,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 218,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 218,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 234,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 234,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 242,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 242,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 249,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 249,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 256,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 256,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 264,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 264,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 272,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 272,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 279,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 279,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 287,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 287,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 302,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 302,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 310,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 310,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 317,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 325,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 325,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 332,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 340,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 340,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 355,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 355,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 363,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 363,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 370,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 370,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 378,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 378,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 385,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 385,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 393,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 393,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 407,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 407,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 415,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 415,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 430,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 430,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 438,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 438,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 445,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 445,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 453,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 453,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 460,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 460,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 468,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 690,\"ycoord\": 468,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 655,\"ycoord\": 475,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 475,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 655,\"ycoord\": 483,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 483,\"font\": \"Courier\",\"fontSize\": 6}"
						
						
						+ ",{\"type\": \"text\",\"name\": \"t2_subtotal_ea\",\"xcoord\": 655,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"t2_subtotal_cs\",\"xcoord\": 690,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						
						
						//+ ",{\"type\": \"image\",\"name\": \"firmaCliente\",\"xcoord\": 350,\"ycoord\": 726,\"scaleX\": 600,\"scaleY\": 25}"
						+ "]},"
						// Página 2
						+ "{\"image\": \"/IBM/WebSphere/AppServer/ADN/files/frmJanitorial" + txt_image + "2.jpg\",\"params\": "
						+ "[{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 61,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 61,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 68,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 68,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 75,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 75,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 83,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 83,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 90,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 90,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 320,\"ycoord\": 98,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 355,\"ycoord\": 98,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_ea\",\"xcoord\": 320,\"ycoord\": 106,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_cs\",\"xcoord\": 355,\"ycoord\": 106,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 320,\"ycoord\": 113,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_ea\",\"xcoord\": 320,\"ycoord\": 128,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_ea\",\"xcoord\": 320,\"ycoord\": 136,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_ea\",\"xcoord\": 320,\"ycoord\": 144,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80135_ea\",\"xcoord\": 320,\"ycoord\": 151,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_ea\",\"xcoord\": 320,\"ycoord\": 159,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_ea\",\"xcoord\": 320,\"ycoord\": 166,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_ea\",\"xcoord\": 320,\"ycoord\": 174,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 189,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 189,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 196,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 196,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 219,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 219,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 227,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 227,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 235,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_cs\",\"xcoord\": 355,\"ycoord\": 235,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 250,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 257,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 265,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 272,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 280,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 288,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 303,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 311,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 318,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 326,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 333,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 348,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 356,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 364,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 371,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 379,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 387,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 394,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 401,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 408,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 416,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 424,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 431,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 431,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 439,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 439,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 447,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 447,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 455,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 463,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 470,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 477,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 485,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 500,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 320,\"ycoord\": 508,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 508,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 516,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 355,\"ycoord\": 524,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"t3_subtotal_ea\",\"xcoord\": 320,\"ycoord\": 539,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"t3_subtotal_cs\",\"xcoord\": 355,\"ycoord\": 539,\"font\": \"Courier\",\"fontSize\": 6}"
						
						// columna 2
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 61,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 61,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 68,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 68,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 75,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 75,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 83,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 83,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 90,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 90,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_ea\",\"xcoord\": 655,\"ycoord\": 98,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80035_cs\",\"xcoord\": 690,\"ycoord\": 98,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_ea\",\"xcoord\": 655,\"ycoord\": 106,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80060_cs\",\"xcoord\": 690,\"ycoord\": 106,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 655,\"ycoord\": 113,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 690,\"ycoord\": 113,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 655,\"ycoord\": 120,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80020_ea\",\"xcoord\": 690,\"ycoord\": 120,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_ea\",\"xcoord\": 655,\"ycoord\": 128,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80030_ea\",\"xcoord\": 690,\"ycoord\": 128,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_ea\",\"xcoord\": 655,\"ycoord\": 136,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80100_ea\",\"xcoord\": 690,\"ycoord\": 136,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_ea\",\"xcoord\": 655,\"ycoord\": 144,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80095_ea\",\"xcoord\": 690,\"ycoord\": 144,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80135_ea\",\"xcoord\": 655,\"ycoord\": 151,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80135_ea\",\"xcoord\": 690,\"ycoord\": 151,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_ea\",\"xcoord\": 655,\"ycoord\": 159,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80045_ea\",\"xcoord\": 690,\"ycoord\": 159,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_ea\",\"xcoord\": 655,\"ycoord\": 166,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80050_ea\",\"xcoord\": 690,\"ycoord\": 166,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_ea\",\"xcoord\": 655,\"ycoord\": 174,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80065_ea\",\"xcoord\": 690,\"ycoord\": 174,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 189,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 196,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 203,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 219,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 227,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 235,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 243,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 257,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 257,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 265,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 265,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 272,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 272,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 280,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 280,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 288,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 288,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 296,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 296,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 303,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 303,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 318,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 318,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 326,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 326,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 333,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 333,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 348,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 690,\"ycoord\": 348,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 356,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"paper_80145_ea\",\"xcoord\": 655,\"ycoord\": 364,\"font\": \"Courier\",\"fontSize\": 6}"

						
						
						+ ",{\"type\": \"text\",\"name\": \"t4_subtotal_ea\",\"xcoord\": 655,\"ycoord\": 478,\"font\": \"Courier\",\"fontSize\": 6}"
						+ ",{\"type\": \"text\",\"name\": \"t4_subtotal_cs\",\"xcoord\": 690,\"ycoord\": 478,\"font\": \"Courier\",\"fontSize\": 6}"
						
						+ ",{\"type\": \"text\",\"name\": \"gran_total\",\"xcoord\": 680,\"ycoord\": 493,\"font\": \"Courier\",\"fontSize\": 6}"
						
						
						//+ ",{\"type\": \"image\",\"name\": \"firmaCliente\",\"xcoord\": 350,\"ycoord\": 726,\"scaleX\": 600,\"scaleY\": 25}"
						+ "]}"
						+ "]}";  
				
			}
			  
			JsonNode template = mapper.readTree(txtTemplate);
			File file = new File(pdfService.createPDFForm(template, newData, true));
			if (file.exists()) {
				//InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        //httpResponse.setContentLength((int)file.length());
		        //String mimeType= "application/pdf";
		        //httpResponse.setContentType(mimeType);
		        //FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        httpResponse.setContentLength((int)file.length());
		        String mimeType= "application/pdf";
		        httpResponse.setContentType(mimeType);
		        httpResponse.setHeader("Content-Disposition","inline; filename=\"" + docName + ".pdf" + "\"");
		        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
		        inputStream.close();
		        httpResponse.flushBuffer();
			} else {
				response.setStatus(1);
				response.setExito("Error al generar el PDF");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			response.setStatus(1);
			response.setExito("Error al generar el PDF");
		//} catch (AquariusException e) {
		//	response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
	}
	
	
	@RequestMapping(value = "/getFormPdf")
	public void getFormData(HttpSession session, HttpServletRequest request, HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			String docName = "";
			Map<String,String[]> parameters = request.getParameterMap();
			ObjectNode newData = mapper.createObjectNode();
			ArrayNode values = newData.putArray("values");
			for(String key:parameters.keySet()) {
				if (request.getParameter(key) != null) {
					if (key.equals("template")) {
						newData.put("template", request.getParameter(key).toString());
					} else if (key.equals("docName")) {
						newData.put("docName", request.getParameter(key).toString());
						docName = request.getParameter(key).toString();
					} else {
						ObjectNode newObject = mapper.createObjectNode();
						newObject.put("param", key);
						newObject.put("value", request.getParameter(key).toString());
						System.out.println(newObject);
						values.add(newObject);
					}
				}
			}
			System.out.println(newData.toString());
			String txtTemplate = "{\"name\": \"sampleTemplate\",\"form\": "
					+ "[{\"image\": \"/IBM/WebSphere/AppServer/ADN/files/formUniversal.jpg\",\"params\": "
					
					
					+ "[{\"type\": \"text\",\"name\": \"numero_contrato\",\"xcoord\": 500,\"ycoord\": 165,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"nombre_comprador\",\"xcoord\": 74,\"ycoord\": 192,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"fecha_mes\",\"xcoord\": 450,\"ycoord\": 192,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"fecha_dia\",\"xcoord\": 490,\"ycoord\": 192,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"fecha_anio\",\"xcoord\": 520,\"ycoord\": 192,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					
					+ "{\"type\": \"text\",\"name\": \"direccion_numero\",\"xcoord\": 74,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"direccion_calle\",\"xcoord\": 185,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"direccion_urbanizacion\",\"xcoord\": 350,\"ycoord\": 212,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					+ "{\"type\": \"text\",\"name\": \"direccion_ciudad\",\"xcoord\": 30,\"ycoord\": 230,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"direccion_estado\",\"xcoord\": 310,\"ycoord\": 230,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"direccion_zip\",\"xcoord\": 450,\"ycoord\": 230,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					+ "{\"type\": \"text\",\"name\": \"direccion_postal\",\"xcoord\": 74,\"ycoord\": 248,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					+ "{\"type\": \"text\",\"name\": \"telefono_residencial_lada\",\"xcoord\": 74,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"telefono_residencial_numero\",\"xcoord\": 95,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"telefono_trabajo_lada\",\"xcoord\": 280,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"telefono_trabajo_numero\",\"xcoord\": 301,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10},"
					+ " {\"type\": \"text\",\"name\": \"telefono_celular_lada\",\"xcoord\": 445,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10}, "
					+ "{\"type\": \"text\",\"name\": \"telefono_celular_numero\",\"xcoord\": 466,\"ycoord\": 263,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					+ "{\"type\": \"text\",\"name\": \"email\",\"xcoord\": 74,\"ycoord\": 278,\"font\": \"Courier\",\"fontSize\": 10}, "
					
					
					+ "{\"type\": \"text\",\"name\": \"calentador_cantidad\",\"xcoord\": 30,\"ycoord\": 329,\"font\": \"Courier\",\"fontSize\": 12},"
					+ " {\"type\": \"text\",\"name\": \"calentador_adicional\",\"xcoord\": 280,\"ycoord\": 329,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"calentador_importe\",\"xcoord\": 490,\"ycoord\": 329,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"calentador_centavos\",\"xcoord\": 545,\"ycoord\": 329,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"tanque_cantidad\",\"xcoord\": 30,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"tanque_adicional\",\"xcoord\": 180,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"tanque_importe\",\"xcoord\": 490,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"tanque_centavos\",\"xcoord\": 545,\"ycoord\": 341,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"colectores_cantidad\",\"xcoord\": 30,\"ycoord\": 353,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"colectores_importe\",\"xcoord\": 490,\"ycoord\": 353,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"colectores_centavos\",\"xcoord\": 545,\"ycoord\": 353,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"calentador_piscina_cantidad\",\"xcoord\": 30,\"ycoord\": 365,\"font\": \"Courier\",\"fontSize\": 12},"
					+ " {\"type\": \"text\",\"name\": \"calentador_piscina_importe\",\"xcoord\": 490,\"ycoord\": 365,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"calentador_piscina_centavos\",\"xcoord\": 545,\"ycoord\": 365,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"caja_cantidad\",\"xcoord\": 30,\"ycoord\": 377,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"caja_importe\",\"xcoord\": 490,\"ycoord\": 377,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"caja_centavos\",\"xcoord\": 545,\"ycoord\": 377,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"bomba_cantidad\",\"xcoord\": 30,\"ycoord\": 388,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"bomba_importe\",\"xcoord\": 490,\"ycoord\": 388,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"bomba_centavos\",\"xcoord\": 545,\"ycoord\": 388,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"osmosis_cantidad\",\"xcoord\": 30,\"ycoord\": 400,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"osmosis_importe\",\"xcoord\": 490,\"ycoord\": 400,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"osmosis_centavos\",\"xcoord\": 545,\"ycoord\": 400,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"suavizador_cantidad\",\"xcoord\": 30,\"ycoord\": 412,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"suavizador_importe\",\"xcoord\": 490,\"ycoord\": 412,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"suavizador_centavos\",\"xcoord\": 545,\"ycoord\": 412,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"fotovoltica_cantidad\",\"xcoord\": 30,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"fotovoltica_importe\",\"xcoord\": 490,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"fotovoltica_centavos\",\"xcoord\": 545,\"ycoord\": 423,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"panel_cantidad\",\"xcoord\": 30,\"ycoord\": 434,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"panel_importe\",\"xcoord\": 490,\"ycoord\": 434,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"panel_centavos\",\"xcoord\": 545,\"ycoord\": 434,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"inversores_cantidad\",\"xcoord\": 30,\"ycoord\": 446,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"inversores_importe\",\"xcoord\": 490,\"ycoord\": 446,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"inversores_centavos\",\"xcoord\": 545,\"ycoord\": 446,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"bateria_cantidad\",\"xcoord\": 30,\"ycoord\": 458,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"bateria_importe\",\"xcoord\": 490,\"ycoord\": 458,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"bateria_centavos\",\"xcoord\": 545,\"ycoord\": 458,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"controlador_cantidad\",\"xcoord\": 30,\"ycoord\": 470,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"controlador_importe\",\"xcoord\": 490,\"ycoord\": 470,\"font\": \"Courier\",\"fontSize\": 12},"
					+ " {\"type\": \"text\",\"name\": \"controlador_centavos\",\"xcoord\": 545,\"ycoord\": 470,\"font\": \"Courier\",\"fontSize\": 12},"
					
					+ " {\"type\": \"text\",\"name\": \"adicional_cantidad\",\"xcoord\": 30,\"ycoord\": 480,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional_texto\",\"xcoord\": 100,\"ycoord\": 480,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional_importe\",\"xcoord\": 490,\"ycoord\": 480,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional_centavos\",\"xcoord\": 545,\"ycoord\": 480,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"adicional2_cantidad\",\"xcoord\": 30,\"ycoord\": 492,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional2_texto\",\"xcoord\": 100,\"ycoord\": 492,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional2_importe\",\"xcoord\": 490,\"ycoord\": 492,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"adicional2_centavos\",\"xcoord\": 545,\"ycoord\": 492,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"precio_total_importe\",\"xcoord\": 490,\"ycoord\": 508,\"font\": \"Courier\",\"fontSize\": 13}, "
					+ "{\"type\": \"text\",\"name\": \"precio_total_centavos\",\"xcoord\": 545,\"ycoord\": 508,\"font\": \"Courier\",\"fontSize\": 13}, "
					
					+ "{\"type\": \"text\",\"name\": \"impuesto_importe\",\"xcoord\": 490,\"ycoord\": 530,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"impuesto_centavos\",\"xcoord\": 545,\"ycoord\": 530,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"total_venta_importe\",\"xcoord\": 490,\"ycoord\": 552,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"total_venta_centavos\",\"xcoord\": 545,\"ycoord\": 552,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"pronto_pago_importe\",\"xcoord\": 490,\"ycoord\": 574,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"pronto_pago_centavos\",\"xcoord\": 545,\"ycoord\": 574,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"balance_importe\",\"xcoord\": 490,\"ycoord\": 596,\"font\": \"Courier\",\"fontSize\": 12}, "
					+ "{\"type\": \"text\",\"name\": \"balance_centavos\",\"xcoord\": 545,\"ycoord\": 596,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"text\",\"name\": \"nombre_vendedor\",\"xcoord\": 30,\"ycoord\": 760,\"font\": \"Courier\",\"fontSize\": 12}, "
					
					+ "{\"type\": \"image\",\"name\": \"firma_conforme\",\"xcoord\": 470,\"ycoord\": 760,\"scaleX\": 200,\"scaleY\": 30}]}]}";    
			JsonNode template = mapper.readTree(txtTemplate);
			File file = new File(pdfService.createPDFForm(template, newData, false));
			if (file.exists()) {
				//InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        //httpResponse.setContentLength((int)file.length());
		        //String mimeType= "application/pdf";
		        //httpResponse.setContentType(mimeType);
		        //FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        httpResponse.setContentLength((int)file.length());
		        String mimeType= "application/pdf";
		        httpResponse.setContentType(mimeType);
		        httpResponse.setHeader("Content-Disposition","inline; filename=\"" + docName + ".pdf" + "\"");
		        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        //file.delete();
		        inputStream.close();
		        httpResponse.flushBuffer();
			} else {
				response.setStatus(1);
				response.setExito("Error al generar el PDF");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			response.setStatus(1);
			response.setExito("Error al generar el PDF");
		//} catch (AquariusException e) {
		//	response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
	}
}

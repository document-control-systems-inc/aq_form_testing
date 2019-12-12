package com.f2m.aquarius.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AquariusServiceFile {

	private final static Logger logger = Logger.getLogger(AquariusServiceFile.class.getName());
	private final String confFileName = ".aquariusService";
	private ObjectMapper mapper = new ObjectMapper();
	
	public boolean writeConf(JsonNode conf) {
		BufferedWriter bw = null;
		File file = new File(confFileName);
		boolean response = false;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(conf.toString());
			response = true;
			logger.log(Level.INFO, "Se guardó archivo de configuración");
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Error al escribir el archivo de configuración de Servicios - " + e.getMessage(), e);
			response = false;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error al escribir el archivo de configuración de Servicios - " + e.getMessage(), e);
			response = false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) { }
		}
		return response;
	}
	
	public JsonNode readConfFile() throws Exception {
		JsonNode response = null;
		BufferedReader br = null;
		File file = new File(confFileName);
		if (file.exists()) {
			try {
				String strFile = "";
				br = new BufferedReader(new FileReader(file));
				String st;
				while ((st = br.readLine()) != null) {
					strFile += st;
				}
				response = parse(strFile);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "Error al leer el archivo Service File - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo Service File");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error al leer el archivo Service File - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo Service File");
			} finally {
				if(br != null) {
					br.close();
				}
			}
		} 
		return response;
	}
	
	private JsonNode parse(String obj) {
		if (obj != null) {
			try {
				return mapper.readTree(obj);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error al parsear el objeto Service File - " + e.getMessage(), e);
			}
		}
		return null;
	}
	
	public JsonNode defaultConf() {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("threadServiceSleep", 10);
		newObject.put("threadServiceExecute", true);
		writeConf(newObject);
		return newObject;
	}
}

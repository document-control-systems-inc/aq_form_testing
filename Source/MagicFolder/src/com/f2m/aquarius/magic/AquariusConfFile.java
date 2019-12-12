package com.f2m.aquarius.magic;

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

public class AquariusConfFile {

	private final static Logger logger = Logger.getLogger(AquariusConfFile.class.getName());
	private final String confFileName = ".aquariusConf";
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
			logger.log(Level.SEVERE, "Error al escribir el archivo de configuración - " + e.getMessage(), e);
			response = false;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error al escribir el archivo de configuración - " + e.getMessage(), e);
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
				logger.log(Level.SEVERE, "Error al leer el archivo Config File - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo Config File");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error al leer el archivo Config File - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo Config File");
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
				logger.log(Level.SEVERE, "Error al parsear el objeto Config File - " + e.getMessage(), e);
			}
		}
		return null;
	}
}

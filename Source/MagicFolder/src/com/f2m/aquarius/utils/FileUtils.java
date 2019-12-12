package com.f2m.aquarius.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FileUtils {
	
	private ObjectMapper mapper = new ObjectMapper();
	private GeneralUtils gutils = new GeneralUtils();
	private final static Logger logger = Logger.getLogger(GeneralUtils.class.getName());

	private JsonNode getJsonFile(File f) {
		if (f.exists()) {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("name", f.getName());
			newObject.put("localPath", f.getAbsolutePath());
			try {
				Path path = Paths.get(f.getAbsolutePath());
				BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
				newObject.putPOJO("createdOn", gutils.getTime(attr.creationTime().toMillis()));
				newObject.putPOJO("modifiedOn", gutils.getTime(attr.lastModifiedTime().toMillis()));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error al obtener las propiedades del archivo " + f.getName(), e);
			}
			if (f.isDirectory()) {
				newObject.put("type", 0);
				ArrayNode childrens = newObject.putArray("childs");
				ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
				for (File file:files) {
					JsonNode children = getJsonFile(file);
					if (children != null) {
						childrens.add(children);
					}
				}
			} else {
				newObject.put("type", 1);
			}
			return newObject;
		} else {
			logger.log(Level.SEVERE, "El archivo no existe");
			return null;
		}
		
	}
	
	public JsonNode getFileInfo(String path) {
		try {
			File rootPath = new File(path);
			if (rootPath.exists()) {
				return getJsonFile(rootPath);
			} else {
				logger.log(Level.SEVERE, "El archivo no existe en la ruta " + path);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al obtener la información del archivo " + path, e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		FileUtils futils = new FileUtils();
		JsonNode fileInfo = futils.getFileInfo("C:\\Users\\gomado\\Documents\\F2M\\Aquarius\\Repositorio F2M\\aquarius\\Test\\MagicFolder");
		if (fileInfo != null) {
			System.out.println(fileInfo);
		} else {
			System.out.println("No se pudo obtener la información del archivo");
		}
	}
}

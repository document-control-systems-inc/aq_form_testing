package com.f2m.aquarius.magic;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AquariusWindowsContextMenu {

	private final static Logger logger = Logger.getLogger(AquariusMagicFolder.class.getName());
	
	private final String directoryRootPath = "SOFTWARE\\Classes\\Directory";
	private final String shellKey = "\\shell\\";
	private final String commandKey = "\\command\\";
	private final String parentKey = "Aquarius";
	
	private boolean deleteKey(String key) {
		try {
			List<String> lista = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, key);
			if (lista != null) {
				if (lista.size() == 0) {
					WinRegistry.deleteKey(WinRegistry.HKEY_LOCAL_MACHINE, key);
				} else {
					for (String str:lista) {
						String subKey = key + "\\" + str;
						if (!deleteKey(subKey)) {
							return false;
						}
					}
					WinRegistry.deleteKey(WinRegistry.HKEY_LOCAL_MACHINE, key);
				}
			}
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al eliminar la llave: " + key + " - " + e.getMessage(), e);
			e.printStackTrace();
			return false;
		}
	}
	
	private String createKey(String path, String key, String icon, String command) {
		String subPath = path + shellKey + key;
		try {
			if (deleteKey(subPath)) {
				logger.log(Level.INFO, "Creando LLave: " + key); 
				WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, subPath);
				if (command != null && command.length() > 0) {
					WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, subPath + commandKey);
					WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, subPath + commandKey, "", command);
				} else {
					WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, subPath, "subcommands", "");
					WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, subPath + shellKey);
				}
				if (icon != null && icon.length() > 0) {
					WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, subPath, "Icon", System.getProperty("user.dir") + "\\icons\\" + icon);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al crear la llave: " + key + " - " + e.getMessage(), e);
			return null;
		}
		return subPath;
	}
	
	public boolean createTree() {
		String parent = createKey(directoryRootPath,parentKey,"Aquarius.ico", null);
		String pathApp = System.getProperty("user.dir");
		pathApp = System.getProperty("user.dir").substring(0, pathApp.length() - 3) + "AquariusMagicFolder.exe";
		
		if (parent != null) {
			String distribution = createKey(parent, "Distribución", null, null);
			if (distribution != null) {
				String send = createKey(distribution, "Enviar", null,  pathApp + " -send \"%v\"");
				String sendWizard = createKey(distribution, "Enviar ...", null, pathApp + " -send \"%v\" -w");
			}
			String workflow = createKey(parent, "Workflow", null, null);
			if (workflow != null) {
				String bandeja = createKey(workflow, "Bandeja de Tareas", null,  pathApp + " -workflow \"%v\"");
				String tareas = createKey(workflow, "Asignar Tarea", null,  pathApp + " -workflow \"%v\" -task");
				String proceso = createKey(workflow, "Asignar Proceso", null,  pathApp + " -workflow \"%v\" -workflow");
				//String sendWizard = createKey(distribution, "Enviar ...", null, pathApp + " -send \"%v\" -w");
			}
			String configuration = createKey(parent, "Configuración", null, null);
			if (configuration != null) {
				String confConexion = createKey(configuration, "Conexión", null,  pathApp + " -conf -conn");
				//String sendWizard = createKey(distribution, "Enviar ...", null, pathApp + " -send \"%v\" -w");
			}
		}
		return true;
	}
	
	public boolean removeTree() {
		try {
			String subPath = directoryRootPath + shellKey + parentKey;
			if (deleteKey(subPath)) {
				logger.log(Level.INFO, "Se eliminó la LLave: " + parentKey);
			} else {
				logger.log(Level.INFO, "No se pudo elminar la LLave: " + parentKey);
			}
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al eliminar árbol - " + e.getMessage(), e);
			return false;
		}
	}
}

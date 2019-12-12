package com.f2m.aquarius.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WinService {

	private final static Logger logger = Logger.getLogger(WinService.class.getName());
	
	public String getAppPath() {
		String pathApp = System.getProperty("user.dir");
		pathApp = System.getProperty("user.dir").substring(0, pathApp.length() - 3);
		return pathApp;
	}
	
	private boolean executeCommand(String command) {
		boolean success = true;
		try {
			if (command.contains("%pathApp%")) {
				command = command.replace("%pathApp%", getAppPath() + "AquariusMagicFolder.exe");
			}
			logger.log(Level.INFO, "Ejecutando comando: " + command);
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            // Read command standard output
            String s;
            logger.log(Level.INFO, "Command Output:");
            while ((s = stdInput.readLine()) != null) {
                logger.log(Level.INFO, s);
                if (s.contains("SUCCESS")) {
                	success = true;
                }
            }
            String errorOutput = "";
            while ((s = stdError.readLine()) != null) {
            	errorOutput += s + "\n";
            }
            if (errorOutput.length() > 0) {
            	logger.log(Level.INFO, "Error al ejecutar el servicio: \n" + errorOutput);
            	success = false;
            }
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	private boolean executeCommandThread(String path, String command) {
		boolean success = true;
		try {
			if (command.contains("%pathApp%")) {
				command = command.replace("%pathApp%", getAppPath());
			}
			File file = new File(path);
			if (file.exists()) {
				logger.log(Level.INFO, "Ejecutando comando: " + command);
				ProcessBuilder  processBuilder = new ProcessBuilder();
				
				processBuilder.command("cmd.exe", command, "-service");
				processBuilder.directory(file);
				Process process = processBuilder.start();
				
				BufferedReader reader =
	                    new BufferedReader(new InputStreamReader(process.getInputStream()));

	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }

	            int exitCode = process.waitFor();
	            System.out.println("\nExited with error code : " + exitCode);
				
			} else {
				logger.log(Level.INFO, "La ruta no existe: " + path);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	public boolean createWindowsService(String serviceName, String displayName, String path, String parameter) {
		if (serviceName == null || serviceName.length() == 0
				|| displayName == null || displayName.length() == 0) {
			logger.log(Level.INFO, "Parámetros mal formado: falta de parámetros");
			return false;
		}
		String command = "sc create \"" + serviceName + "\" binPath=\"%pathApp%%parameter%\" start=auto DisplayName=\"" + displayName + "\"";
		if (path != null && path.length() > 0) {
			command = command.replace("%pathApp%", path);
		}
		if (parameter != null && parameter.length() > 0) {
			command = command.replace("%parameter%", parameter);
		} else {
			command = command.replace("%parameter%", "");
		}
		return executeCommand(command);
	}
	
	public boolean deleteWindowsService(String serviceName) {
		if (serviceName == null || serviceName.length() == 0) {
			logger.log(Level.INFO, "Parámetros mal formado: falta de parámetros");
			return false;
		}
		String command = "sc delete \"" + serviceName + "\"";
		return executeCommand(command);
	}
	
	public boolean startService(String path, String command) {
		if (command == null || command.length() == 0) {
			command = "AquariusMagicFolder.exe";
		}
		return executeCommandThread(path, command);
	}
	
	public static void main(String[] args) {
		String path = "C:\\Users\\gomado\\AppData\\Local\\AquariusMagicFolder";
		WinService winService = new WinService();
		System.out.println("Iniciando servicio");
		//winService.startService(path, "");
		System.out.println("Se ha finalizado el servicio");
		/*
		boolean create = winService.createWindowsService("AquariusMagicFolder", "Aquarius Magic Folder", path, " -startService");
		if (create) {
			System.out.println("Se ha creado el servicio");
			//winService.deleteWindowsService("AquariusMagicFolder");
		}
		*/
	}
}

package com.f2m.aquarius.deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Utils {
	
	private Properties prop = null;
	
	public Utils() {
		if (prop == null) {
			prop = new Properties();
			try {
				prop.load(new FileInputStream("config.properties"));
			} catch (Exception e) {
				System.out.println("Error al cargar las propiedades");
				prop = null;
			}
		}
	}

	public void deleteDirectory(String path) {
		File directory = new File(path);
		if (directory.exists()) {
			deleteDir(directory);
		}
	}
	
	public void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	
	private void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	public String getProperty(String property) {
		return prop.getProperty(property);
	}
	
	public String executeCommand(String[] command, String path) {
		try {
			StringBuffer output = new StringBuffer();
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
	            System.out.println(line);
	            output.append(line + "\n");
	        }
	        int returnCode = p.waitFor();
	        output.append("[Utils - ExecuteCode]:" + returnCode);
	        return output.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

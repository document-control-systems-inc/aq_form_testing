package com.f2m.aquarius.magic.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

import com.f2m.aquarius.magic.WinService;

public class TestRunnable {

	public static void main(String[] args) {
		try {
			
			String pathApp = "C:\\Users\\gomado\\AppData\\Local\\AquariusMagicFolder\\app";
			pathApp = pathApp.substring(0, pathApp.length() - 3);
			//String path = System.getProperty("user.home");
			System.out.println(pathApp);
			String command = "AquariusMagicFolder.exe";
			String params = "-service";
			
			ProcessBuilder processBuilder = new ProcessBuilder();

	        processBuilder.command("cmd.exe", "/k", command, params);
	        processBuilder.directory(new File(pathApp));

	        // can also run the java file like this
	        // processBuilder.command("java", "Hello");

	            Process process = processBuilder.start();
	            /*
	            BufferedReader reader =
	                    new BufferedReader(new InputStreamReader(process.getInputStream()));

	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	             */
	            //int exitCode = process.waitFor();
	            //System.out.println("\nExited with error code : " + exitCode);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

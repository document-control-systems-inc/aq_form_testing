package com.f2m.aquarius.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UtilsF2m {
	private Properties prop = null;
	private String name = new String();
	
	public UtilsF2m() {}
	
	public UtilsF2m(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue(String key) {
		if (prop == null) {
			openProperty();
		}
		if (prop != null) {
			return prop.getProperty(key);
		}
		return null;
	}
	
	private boolean openProperty() {
		InputStream input = null;
		boolean response = false;
		try {
			String currentDir = System.getProperty("user.dir");
			System.out.println("Buscando: " + currentDir);
			input = getClass().getClassLoader().getResourceAsStream(name + ".properties");
			prop = new Properties();
			prop.load(input);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			prop = null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

}

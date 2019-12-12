package com.f2m.aquarius.service;

import java.util.List;

import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class ErrorService {

	private GeneralUtils gutils = new GeneralUtils();
	private DBService bd = new DBService();

	public List<JsonNode> getErrors(String locale) throws AquariusException {
		if (locale == null || locale.length() == 0) {
			throw gutils.throwException(701, "");
		}
		return bd.selectJson("error", "data->>'locale' = '" + locale + "'", "data->>'id'");
	}
	
	public JsonNode getErrorById(int id, String locale) throws AquariusException {
		if (locale == null || locale.length() == 0) {
			throw gutils.throwException(701, "");
		}
		List<JsonNode> resp = bd.selectJson("error", "data->>'id' = '" + id + "' and data->>'locale' = '" + locale + "'", null);
		if (resp.isEmpty()) {
			throw gutils.throwException(702, "");
		} else {
			return resp.get(0);
		}
		
	}
	
	public String validateLocale(String locale) throws AquariusException {
		if (locale != null && locale.length() > 0) {
			List<JsonNode> resultados = bd.selectColumnsJson("distinct(data->'locale') as locale", "error", null, null);
			boolean findIt = false;
			if (!resultados.isEmpty()) {
				for (JsonNode locales:resultados) {
					if (locales.findValue("locale").asText().equals(locale)) {
						findIt = true;
						break;
					}
				}
				if (!findIt) {
					int indexOf = locale.indexOf("_");
					if (indexOf > -1) {
						locale = locale.substring(0, indexOf);
						for (JsonNode locales:resultados) {
							if (locales.findValue("locale").asText().equals(locale)) {
								findIt = true;
								break;
							}
						}
					}
				}
				if (findIt) {
					return locale;
				}
			}
		}
		return "es";
	}
	
	public static void main(String[] args) {
		ErrorService error = new ErrorService();
		try {
			System.out.println("Locale: " + error.validateLocale("en_us"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

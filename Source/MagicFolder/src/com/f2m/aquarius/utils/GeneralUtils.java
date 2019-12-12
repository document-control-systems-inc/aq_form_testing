package com.f2m.aquarius.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class GeneralUtils {
	
	private final static Logger logger = Logger.getLogger(GeneralUtils.class.getName());
	private ObjectMapper mapper = new ObjectMapper();
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
	
	public int getErrorCode(String exception) {
		int response = -1;
		try {
			response = Integer.parseInt(exception);
		} catch (Exception e) { }
		return response;
	}

	public AquariusException throwException(int errorCode, String msg) {
		System.out.println("Error [" + errorCode + "]: " + msg);
		logger.log(Level.SEVERE, "Error [" + errorCode + "]: " + msg);
		return new AquariusException(Integer.toString(errorCode));
	}
	
	public ObjectNode getTime() {
		ObjectNode time = mapper.createObjectNode();
		Calendar calendar = Calendar.getInstance();
		time.put("time", calendar.getTimeInMillis());
		time.put("timezone", calendar.getTimeZone().getID());
		return time;
	}
	
	public ObjectNode getTime(long timeInMilis) {
		ObjectNode time = mapper.createObjectNode();
		Calendar calendar = Calendar.getInstance();
		time.put("time", timeInMilis);
		time.put("timezone", calendar.getTimeZone().getID());
		return time;
	}
	
	public String getTimeString(JsonNode time) {
		if (time != null && time.size() > 0) {
			if (!time.isObject()) {
				try {
					time = mapper.readTree(time.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String timeZone = time.findValue("timezone").asText();
			long actualTime = time.findValue("time").asLong();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(actualTime);
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			return dateFormat.format(calendar.getTime());
		} else {
			return "No Disponible";
		}
	}
	
	public String getDateTimeString(String time) {
		long actualTime = Long.parseLong(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(actualTime);
		return dateFormat.format(calendar.getTime());
	}
	
	public String rawString(String string) {
		string = string.replaceAll("[^a-zA-Z0-9]+","_");
		string = string.toLowerCase();
		
		return string;
	}

}

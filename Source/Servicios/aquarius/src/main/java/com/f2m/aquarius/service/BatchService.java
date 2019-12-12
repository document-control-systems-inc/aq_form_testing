package com.f2m.aquarius.service;

import java.util.List;
import java.util.UUID;

import com.f2m.aquarius.parameters.BatchParams;
import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BatchService {
	
	private GeneralUtils gutils = new GeneralUtils();
	private ObjectMapper mapper = new ObjectMapper();
	private DBService bd = new DBService();
	
	public boolean setOCRTime(long time, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1302, "");
		}
		if (time > 0 && time < 86401) {
			BatchParams.threadOCRSleep = time;
			return true;
		} else {
			throw gutils.throwException(1301, "");
		}
	}
	
	public boolean setOCRExecution(boolean execute, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1303, "");
		}
		BatchParams.threadOCRExecute = execute;
		return true;
	}
	
	public List<JsonNode> getBatch(String id, String type) throws AquariusException {
		String where = "";
		
		if (id != null && id.length() > 0) {
			where += "data->>'id' = '" + id + "'";
		}
		if (type != null && type.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->>'type' = '" + type + "' ";
		}
		String orderBy = "data->'createdOn'->>'time' desc";
		List<JsonNode> batches = bd.selectJson("batch", where, orderBy);
		if (batches != null) {
			return batches;
		} else {
			throw gutils.throwException(1304, "");
		}
	}
	
	public String setBatch(String id, String type, int periodicity, JsonNode properties, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| type == null || type.length() == 0) {
			throw gutils.throwException(1305, "");
		}
		switch(type) {
			case BatchParams.TYPE_OCR:
				
				break;
			case BatchParams.TYPE_THUMB:
				break;
			default:
				throw gutils.throwException(1305, "");
		}
		boolean update = false;
		ObjectNode newObject = mapper.createObjectNode();
		if (id == null || id.length() == 0) {
			newObject.put("id", UUID.randomUUID().toString());
		} else {
			newObject.put("id", id);
			update = !getBatch(id, "").isEmpty();
		}
		newObject.put("type", type);
		newObject.put("createdBy", userId);
		newObject.putPOJO("createdOn", gutils.getTime());
		newObject.put("periodicity", periodicity);
		newObject.putPOJO("properties", properties);
		if (update) {
			if (!deleteBatch(id, userId)) {
				throw gutils.throwException(1306, "");
			}
		}
		if (bd.insertJson("batch", newObject)) {
			return newObject.path("id").asText();
		} else {
			throw gutils.throwException(1306, "");
		}
	}
	
	public boolean deleteBatch(String id, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0
				|| id == null || id.length() == 0) {
			throw gutils.throwException(1307, "");
		}
		String where = "data->>'id' = '" + id + "' ";
		return bd.deleteJson("batch", where);
	}
	
	
	
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		BatchService batch = new BatchService();
		try {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("nodeType", ECMParameters.N_TYPE_DOCUMENT);
			newObject.put("id", "fee19b00-c07e-41c7-90c0-3844687aac1d");
			newObject.put("versionId", "925dd037-8d90-4d44-a50f-80543c86a586");
			String idBatch = batch.setBatch(null, BatchParams.TYPE_OCR, 0, newObject, "demo");
			if (idBatch != null && idBatch.length() > 0) {
				System.out.println("Se ha insertado el batch con id: " + idBatch);
			} else {
				System.out.println("Error al insertar el proceso batch");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

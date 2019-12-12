package com.f2m.aquarius.service;

import java.util.List;
import java.util.UUID;

import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ScanService {
	
	private GeneralUtils gutils = new GeneralUtils();
	private ObjectMapper mapper = new ObjectMapper();
	private DBService bd = new DBService();

	
	public String setScanProfile(String idUser, String profile) throws AquariusException {
		if (idUser == null || idUser.length() == 0 
				|| profile == null || profile.length() == 0) {
			throw gutils.throwException(1002, "");
		}
		JsonNode profileJson = null;
		try {
			profileJson = mapper.readTree(profile);
		} catch (Exception e) {
			throw gutils.throwException(1002, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		String profileName = null;
		if (profileJson.findValue("AquariusProfile") != null) {
			profileName = profileJson.findValue("AquariusProfile").asText();
			List<JsonNode> profileExists = getScanProfile(idUser, null, profileName);
			if (profileExists != null && profileExists.size() > 0) {
				String id = profileExists.get(0).findValue("id").asText();
				if (deleteScanProfile(idUser, id, profileName)) {
					newObject.put("id", id);
				} else {
					newObject.put("id", UUID.randomUUID().toString());
				}
			} else {
				newObject.put("id", UUID.randomUUID().toString());
			}
			
			newObject.put("profileName", profileName);
			newObject.put("user", idUser);
			newObject.put("createdBy", idUser);
			newObject.putPOJO("createdOn", gutils.getTime());
			newObject.putPOJO("profile", profileJson);
			if (bd.insertJson("scanprofile", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(1003, "");
			}
		} else {
			throw gutils.throwException(1002, "AquariusProfile");
		}
		
	}
	
	public List<JsonNode> getScanProfile(String idUser, String idProfile, String profileName) throws AquariusException {
		if (idUser == null || idUser.length() == 0) {
			throw gutils.throwException(901, "");
		}
		
		String where = "data->>'user' = '" + idUser + "' ";
		if (idProfile != null && idProfile.length() > 0) {
			where += " and data->>'id' = '" + idProfile + "' ";
		}
		if (profileName != null && profileName.length() > 0) {
			where += " and data->>'profileName' = '" + profileName + "' ";
		}
		String orderBy = "data->'createdOn'->>'time' desc";
		List<JsonNode> profiles = bd.selectJson("scanprofile", where, orderBy);
		if (profiles != null) {
			return profiles;
		} else {
			throw gutils.throwException(1004, idUser);
		}
	}
	
	public boolean deleteScanProfile(String idUser, String idProfile, String profileName) throws AquariusException {
		if (idUser == null || idUser.length() == 0) {
			throw gutils.throwException(901, "");
		}
		
		String where = "data->>'user' = '" + idUser + "' ";
		boolean isOk = false;
		if (idProfile != null && idProfile.length() > 0) {
			where += " and data->>'id' = '" + idProfile + "' ";
			isOk = true;
		} else {
			if (profileName != null && profileName.length() > 0) {
				where += " and data->>'profileName' = '" + profileName + "' ";
				isOk = true;
			}
		}
		if (isOk) {
			return bd.deleteJson("scanprofile", where);
		} else {
			throw gutils.throwException(1005, idUser);
		}
	}
}

package com.f2m.aquarius.service;

import java.util.List;
import java.util.UUID;

import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SocialService {
	
	private GeneralUtils gutils = new GeneralUtils();
	private ObjectMapper mapper = new ObjectMapper();
	private DBService bd = new DBService();
	private CmisService cmis = new CmisService();

	
	public JsonNode getCommentById(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(901, "");
		}
		
		String where = "data->>'objectId' = '" + id + "' ";
		String orderBy = "data->'createdOn'->>'time' desc";
		List<JsonNode> comments = bd.selectJson("comment", where, orderBy);
		if (comments != null) {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("numComments", comments.size());
			newObject.putPOJO("comments", comments);
			return newObject;
		} else {
			throw gutils.throwException(905, id);
		}
	}
	
	
	public String setCommentByIdObject(String idObject, String userId, String comment) throws AquariusException {
		if (idObject == null || idObject.length() == 0 
				|| userId == null || userId.length() == 0
				|| comment == null || comment.length() == 0) {
			throw gutils.throwException(902, "");
		}
		
		JsonNode object = cmis.getObjectById(idObject, userId);
		if (object == null || object.size() == 0) {
			throw gutils.throwException(902, "Objeto no encontrado");
		}
		if (!object.findValue("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
			throw gutils.throwException(903, "Objeto no encontrado");
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("objectId", idObject);
		newObject.put("createdBy", userId);
		newObject.putPOJO("createdOn", gutils.getTime());
		newObject.put("comment", comment);
		if (bd.insertJson("comment", newObject)) {
			return newObject.findValue("id").asText();
		} else {
			throw gutils.throwException(904, "");
		}
		
	}
	
	public JsonNode getLikeById(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(906, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		String where = "data->>'id' = '" + id + "' ";
		List<JsonNode> likes = bd.selectColumnsJson("count(*) as num", "likes", where, "");
		if (likes.size() > 0) {
			newObject.put("numLikes", likes.get(0).findValue("num").asInt());
		}
		where = "data->>'id' = '" + id + "' and data->>'createdBy' = '" + userId + "' ";
		likes = bd.selectJson("likes", where, "");
		if (likes != null) {
			if (likes.size() > 0) {
				newObject.put("myLike", true);
			} else {
				newObject.put("myLike", false);
			}
		}
		return newObject;
	}
	
	public String setLikeByIdObject(String idObject, String userId) throws AquariusException {
		if (idObject == null || idObject.length() == 0 
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(902, "");
		}
		JsonNode object = cmis.getObjectById(idObject, userId);
		if (object == null || object.size() == 0) {
			throw gutils.throwException(902, "Objeto no encontrado");
		}
		if (!object.findValue("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
			throw gutils.throwException(903, "");
		}
		//TODO: Validar que no haya dado like anteriormente...
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("objectId", idObject);
		newObject.put("createdBy", userId);
		newObject.putPOJO("createdOn", gutils.getTime());
		if (bd.insertJson("likes", newObject)) {
			return newObject.findValue("id").asText();
		} else {
			throw gutils.throwException(907, "");
		}
		
	}
}

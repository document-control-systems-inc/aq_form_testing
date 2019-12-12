package com.f2m.aquarius.service;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.f2m.aquarius.beans.User;
import com.f2m.aquarius.configuration.AppConf;
import com.f2m.aquarius.parameters.SessionParams;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserService {
	private GeneralUtils gutils = new GeneralUtils();
	private DBService bd = new DBService();
	private ObjectMapper mapper = new ObjectMapper();
	private SecurityService securityService = new SecurityService();
	private SessionParams sessionParams;
	
	public UserService() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(AppConf.class);
		ctx.refresh();
		sessionParams = ctx.getBean(SessionParams.class);
		ctx.close();
	}
	
	
	public User getUserAditionalInfo(User user) throws AquariusException {
		if (user == null) {
			throw gutils.throwException(120, "");
		}
		boolean searchCriteria = false;
		String where = "";
		if (user.getUid() != null && user.getUid().length() > 0) {
			where += "data->>'uid' = '" + user.getUid() + "' ";
			searchCriteria = true;
		}
		if (user.getDn() != null && user.getDn().length() > 0) {
			if (searchCriteria) {
				where += " and ";
			}
			where += "data->>'dn' = '" + user.getDn() + "' ";
		}
		
		List<JsonNode> userJson = bd.selectJson("users", where, null);
		if (userJson != null) {
			if (userJson.size() > 1) {
				throw gutils.throwException(102, user.getUid());
			} else if (userJson.size() == 0) {
				throw gutils.throwException(121, user.getUid());
			} else {
				user.setPhoto(userJson.get(0).findValue("photo").asText());
				user.setSessionTime(userJson.get(0).findValue("sessionTime").asInt());
				user.setMaxConcurrent(userJson.get(0).findValue("maxConcurrent").asInt());
				JsonNode securityProfiles = userJson.get(0).findValue("securityProfile");
				if (securityProfiles.isArray()) {
					for (JsonNode profile:securityProfiles) {
						List<JsonNode> pageProfile = securityService.getPageProfileById(profile.asText());
						if (pageProfile != null) {
							if (pageProfile.size() == 1) {
								user.addSecurityProfile(profile.asText());
								JsonNode profileElements = pageProfile.get(0).findValue("elements");
								if (profileElements.isArray()) {
									for (JsonNode profileElement:profileElements) {
										user.addSecurityIdComponent(profileElement.findValue("pageId").asText());
										for (JsonNode profileElementComponent:profileElement.findValue("componentsId")) {
											user.addSecurityIdComponent(profileElementComponent.asText());
										}
									}
								}
							}
						}
					}
				}
				return user;
			}
		} else {
			throw gutils.throwException(101, user.getUid());
		}
	}
	
	public boolean setUserAditionalInfo(User user) throws AquariusException {
		if (user == null || user.getUid() == null || user.getUid().length() == 0) {
			throw gutils.throwException(116, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("uid", user.getUid());
		newObject.put("dn", user.getDn());
		newObject.put("photo", user.getPhoto());
		newObject.put("sessionTime", user.getSessionTime());
		newObject.put("maxConcurrent", user.getMaxConcurrent());
		ArrayNode securityProfile = newObject.putArray("securityProfile");
		if (user.getSecurityProfiles() != null) {
			for (String profile:user.getSecurityProfiles()) {
				securityProfile.add(profile);
			}
		}
		if (bd.insertJson("users", newObject)) {
			return true;
		} else {
			return false;
		}
	}
	
	public User getDefaultValues(User user) {
		if (user == null) {
			user = new User();
		} 
		user.setSessionTime(sessionParams.getMinutes());
		user.setPhoto("idFoto");
		user.setMaxConcurrent(sessionParams.getMaxConcurrent());
		user.addSecurityProfile("default");
		return user;
	}

	public boolean addSecurityProfile(String profile, String uid) throws AquariusException {
		if (profile == null || profile.length() == 0
				|| uid == null || uid.length() == 0) {
			throw gutils.throwException(1107, profile);
		}
		List<JsonNode> profileJson = securityService.getPageProfileById(profile);
		if (profileJson != null) {
			if (profileJson.size() > 1 || profileJson.size() == 0) {
				throw gutils.throwException(1107, profile);
			} else {
				User user = new User();
				user.setUid(uid);
				try {
					user = getUserAditionalInfo(user);
				} catch (AquariusException e) {
					if (gutils.getErrorCode(e.getMessage()) == 121) {
						user = getDefaultValues(user);
						if (!setUserAditionalInfo(user)) {
							throw gutils.throwException(101, user.getUid());
						}
					} else {
						throw e;
					}
				}
				if (user.getSecurityProfiles() != null) {
					if (!user.getSecurityProfiles().contains(profile)) {
						user.addSecurityProfile(profile);
						if (bd.deleteJson("users", "data->>'uid' = '" + uid + "' ")) {
							if (setUserAditionalInfo(user)) {
								return true;
							}
						}
					}
				}
			}
		} else {
			throw gutils.throwException(119, uid);
		}
		return false;
	}
	
	public static void main(String[] args) {
		UserService userService = new UserService();
		User user = userService.getDefaultValues(null);
		user.setUid("user");
		try {
			//if (userService.setUserAditionalInfo(user)) {
				user = userService.getUserAditionalInfo(user);
				System.out.println(user.toString());
			//}
		} catch (AquariusException e) {
			e.printStackTrace();
		}
		
	}

}

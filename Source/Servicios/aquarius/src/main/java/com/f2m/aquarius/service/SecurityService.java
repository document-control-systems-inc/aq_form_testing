package com.f2m.aquarius.service;

import java.util.List;
import java.util.UUID;

import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SecurityService {

	private GeneralUtils gutils = new GeneralUtils();
	private ObjectMapper mapper = new ObjectMapper();
	private DBService bd = new DBService();
	
	public String setPageProfile(String pageProfileName, String id, String elements, String createdBy) throws AquariusException {
		if (pageProfileName == null || pageProfileName.length() == 0 
				|| createdBy == null || createdBy.length() == 0) {
			throw gutils.throwException(1102, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		boolean existe = false;
		if (id != null && id.length() > 0) {
			List<JsonNode> pageProfile = getPageProfileById(id);
			if (pageProfile != null && pageProfile.size() == 1) {
				existe = true;
			}
		}
		if (id == null || id.length() == 0) {
			id = UUID.randomUUID().toString();
		}
		newObject.put("id", id);
		newObject.put("createdBy", createdBy);
		newObject.putPOJO("createdOn", gutils.getTime());
		newObject.put("name", pageProfileName);
		ArrayNode elementsNode = newObject.putArray("elements");
		if (elements != null && elements.length() > 0) {
			try {
				ArrayNode actualElements = mapper.readValue(elements, ArrayNode.class);
				//elementsNode.addAll(actualElements);
				for (JsonNode element: actualElements) {
					String pageId = element.findValue("pageId").asText();
					if (pageId != null && pageId.length() > 0) {
						try {
							JsonNode pages = getPageById(pageId);
							if (pages != null) {
								ArrayNode actualcomponentsIds = (ArrayNode)pages.path("components");
								ObjectNode newObjectElement = mapper.createObjectNode();
								newObjectElement.put("pageId", pageId);
								ArrayNode newObjectElementArray = newObjectElement.putArray("componentsId");
								ArrayNode componentsIds = (ArrayNode)element.path("componentsId");
								for (JsonNode componentId: componentsIds) {
									for (JsonNode component:actualcomponentsIds) {
										if (component.findValue("id").asText().equals(componentId.asText())) {
											newObjectElementArray.add(componentId.asText());
											break;
										}
									}
								}
								elementsNode.add(newObjectElement);
							}
						} catch (Exception e) {}
					}
				}
			} catch (Exception e) { }
		}
		if (existe) {
			if (bd.deleteJson("pageprofile", "data->>'id' = '" + id + "' ")) {
				if (bd.insertJson("pageprofile", newObject)) {
					return id;
				} else {
					throw gutils.throwException(1105, pageProfileName);
				} 
			} else {
				throw gutils.throwException(1105, pageProfileName);
			} 
		} else {
			if (bd.insertJson("pageprofile", newObject)) {
				return id;
			} else {
				throw gutils.throwException(1105, pageProfileName);
			}
		}
	}
	
	public List<JsonNode> getPageProfileById(String id) throws AquariusException {
		String where = "";
		if (id != null && id.length() > 0) {
			where += "data->>'id' = '" + id + "' "; 
		}
		String orderBy = "data->'createdOn'->>'time' desc";
		return bd.selectJson("pageProfile", where, orderBy);
	}
	
	public String setPage(String pageName, String id, String createdBy) throws AquariusException {
		if (pageName == null || pageName.length() == 0 
				|| createdBy == null || createdBy.length() == 0) {
			throw gutils.throwException(1102, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		boolean existe = false;
		if (id != null && id.length() > 0) {
			try {
				newObject = (ObjectNode) getPageById(id);
				existe = true;
			} catch (Exception e) {
				newObject = mapper.createObjectNode();
			}
		}
		
		if (existe) {
			newObject.put("name", pageName);
			if (bd.deleteJson("page", "data->>'id' = '" + id + "' ")) {
				if (bd.insertJson("page", newObject)) {
					return newObject.findValue("id").asText();
				} else {
					throw gutils.throwException(1103, "");
				} 
			} else {
				throw gutils.throwException(1103, "");
			}
		} else {
			if (id == null || id.length() == 0) {
				boolean newId = false;
				int intento = 0;
				while(!newId) {
					String auxId = pageName.toLowerCase();
					try {
						auxId = auxId.replaceAll("\\s+","");
						if (intento > 0) {
							auxId = auxId + intento;
						}
						getPageById(auxId);
						intento++;
					} catch (Exception e) {
						if (gutils.getErrorCode(e.getMessage()) == 1101) {
							newId = true;
							id = auxId;
						}
					}
				}
			}
			newObject.put("id", id);
			newObject.put("name", pageName);
			newObject.putArray("components");
			
			newObject.put("createdBy", createdBy);
			newObject.putPOJO("createdOn", gutils.getTime());
			if (bd.insertJson("page", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(1103, "");
			}
		}
	}
	
	public JsonNode getPageById(String id) throws AquariusException {
		if (id == null || id.length() == 0) {
			throw gutils.throwException(1101, "");
		}
		String where =  "data->>'id' = '" + id + "' ";
		String orderBy = "data->'createdOn'->>'time' desc";
		List<JsonNode> resp = bd.selectJson("page", where, orderBy);
		if (resp.isEmpty()) {
			throw gutils.throwException(1101, "");
		} else {
			return resp.get(0);
		}
	}
	
	public List<JsonNode> getPage(String id) throws AquariusException {
		String where = "";
		if (id != null && id.length() > 0) {
			where += "data->>'id' = '" + id + "' "; 
		}
		String orderBy = "data->'createdOn'->>'time' desc";
		return bd.selectJson("page", where, orderBy);
	}
	
	public JsonNode getPageComponentById(String idPage, String idComponent) throws AquariusException {
		
		JsonNode components = getPageComponent(idPage);
		if (idComponent != null && idComponent.length() > 0) {
			for (JsonNode component:components) {
				if (component.findValue("id").asText().equals(idComponent)) {
					return component;
				}
			}
			throw gutils.throwException(1106, "");
		} else {
			return components;
		}
	}
	
	public JsonNode getPageComponent(String idPage) throws AquariusException {
		if (idPage == null || idPage.length() == 0) {
			throw gutils.throwException(1106, "");
		}
		String select = "page.data->'components'";
		String where = "data->>'id' = '" + idPage + "' "; 
		String orderBy = "data->'createdOn'->>'time' desc";
		
		List<JsonNode> resp = bd.selectJson1(select, "page", where, orderBy);
		if (resp.isEmpty()) {
			throw gutils.throwException(1106, "");
		} else {
			return resp.get(0);
		}
	}
	
	public String setPageComponent(String idPage, String componentName, String idComponent, String createdBy) throws AquariusException {
		if (idPage == null || idPage.length() == 0 
				|| componentName == null || componentName.length() == 0 
				|| createdBy == null || createdBy.length() == 0) {
			throw gutils.throwException(1102, "");
		}
		
		ObjectNode page = (ObjectNode)getPageById(idPage);
		if (page != null) {
			ArrayNode components = null;
			try {
				components = (ArrayNode)page.path("components");
			} catch (Exception e) { }
			if (components == null) {
				components = mapper.createArrayNode();
			}
			
			if (idComponent != null && idComponent.length() > 0) {
				boolean findIdComponent = false;
				int index = -1;
				for (JsonNode component:components) {
					index++;
					if (component.findValue("id").asText().equals(idComponent)) {
						findIdComponent = true;
						break;
					}
				}
				if (findIdComponent) {
					components.remove(index);
				}
			} else {
				boolean newId = false;
				int intento = 0;
				while(!newId) {
					String auxId = idPage + "." + componentName.toLowerCase();
					auxId = auxId.replaceAll("\\s+","");
					if (intento > 0) {
						auxId = auxId + intento;
					}
					boolean findIdComponent = false;
					for (JsonNode component:components) {
						if (component.findValue("id").asText().equals(auxId)) {
							findIdComponent = true;
							break;
						}
					}
					if (!findIdComponent) {
						newId = true;
						idComponent = auxId;
					}
					intento++;
				}
				
			}
			ObjectNode component2 = mapper.createObjectNode();
			component2.put("id", idComponent);
			component2.put("name", componentName);
			components.add(component2);
			if (bd.deleteJson("page", "data->>'id' = '" + idPage + "' ")) {
				if (bd.insertJson("page", page)) {
					return idComponent;
				} else {
					throw gutils.throwException(1104, componentName);
				}
			} else {
				throw gutils.throwException(1104, componentName);
			}
		} else {
			throw gutils.throwException(1101, idPage);
		}
		
	}
	
	public static void main (String[] args) {
		SecurityService security = new SecurityService();
		try {
			//security.setPageProfile("test", null, "[{\"pageId\": \"scan\", \"componentsId\": [\"scan.e1\", \"scan.e2\"]}]", "TEST");
			//security.setPage("Scan", null, "TEST");
			//JsonNode components = security.getPageComponentById("scan","scan.e4");
			//System.out.println(components.toString());
			System.out.println(security.setPageComponent("scan", "e", null, "TEST")); 
		} catch (AquariusException e) {
			e.printStackTrace();
		}
	}
}

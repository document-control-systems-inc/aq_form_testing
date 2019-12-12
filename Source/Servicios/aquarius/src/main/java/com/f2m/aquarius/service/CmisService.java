package com.f2m.aquarius.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CmisService {

	private GeneralUtils gutils = new GeneralUtils();
	private DBService bd = new DBService();
	private FileService fileService = new FileService();
	private ConfigurationService conf = new ConfigurationService();
	private ObjectMapper mapper = new ObjectMapper();
	private String defaultOrderBy = "data->'properties'->>'name'";
	
	public List<JsonNode> getDomains(String idDomain, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(446, "");
		}
		String where = "data->>'type' = '" + ECMParameters.N_TYPE_DOMAIN + "' ";
		if (idDomain == null || idDomain.length() == 0) {
			idDomain = "";
		}
		where += "and data->>'domain' = '" + idDomain + "'";
		return bd.selectJson("node", where, defaultOrderBy);
	}
	
	public List<JsonNode> getStorageAreas(String idDomain, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(447, "");
		}
		String where = null;
		if (idDomain != null && idDomain.length() > 0) {
			where = "data->>'domain' = '" + idDomain + "'";
		}
		return bd.selectJson("storagearea", where, defaultOrderBy);
	}
	
	public List<JsonNode> getStoragePolicy(String idDomain, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(448, "");
		}
		String where = null;
		if (idDomain != null && idDomain.length() > 0) {
			where = "data->>'domain' = '" + idDomain + "'";
		}
		return bd.selectJson("storagepolicy", where, defaultOrderBy);
	}
	
	public List<JsonNode> getStoragePolicyByDomain(String idDomain, String userId) throws AquariusException {
		if (idDomain == null || idDomain.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(404, "");
		}
		String where = "data->>'domain' = '" + idDomain + "' and data->>'type' = '" + ECMParameters.SP_TYPE_DOMAIN + "' ";
		return bd.selectJson("storagepolicy", where, null);
	}
	
	public JsonNode getSelectedStorageArea(String idDomain, String idClass, String idFolder, String userId) throws AquariusException {
		if (idDomain == null || idDomain.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(417, idDomain);
		}
		// en este caso, se ignora si es por clase o folder:
		List<JsonNode> domainPolicy = getStoragePolicyByDomain(idDomain, userId);
		if (domainPolicy == null || domainPolicy.size() == 0) {
			throw gutils.throwException(417, idDomain);
		}
		List<String> storageIdAreas = new ArrayList<String>();
		for (JsonNode policy:domainPolicy) {
			Iterator<JsonNode> storageIds = policy.findValue("storageId").elements();
			while (storageIds.hasNext()) {
				storageIdAreas.add(storageIds.next().asText());
			}
		}
		ObjectNode newObject = mapper.createObjectNode();
		for (String storageId:storageIdAreas) {
			JsonNode storageArea = getStorageArea(storageId, ECMParameters.SA_STATUS_ACTIVE, userId);
			if (storageArea != null && storageArea.size() > 0) {
				int numFile = storageArea.findValue("file").asInt();
				if (numFile < 99999999) {
					String path = storageArea.findValue("path").asText();
					createDirectorySA(path,numFile);
					path = path + getSubPath(numFile);
					newObject.put("id", storageId);
					newObject.put("path", path);
					newObject.put("file", numFile);
					break;
				} else {
					System.out.println("Cerrar la Storage Area y abrir la siguiente disponible...");
				}
			}
		}
		if (newObject.size() > 0) {
			return newObject;
		} else {
			throw gutils.throwException(409, idDomain);
		}
	}
	
	/**
	 * Crea un nuevo Storage Area
	 * @param name Nombre del Storage Area.
	 * @param path Ruta donde se guardará el storage area.
	 * @throws AquariusException
	 */
	public String setStorageArea(String name, String path, String idDomain, String userId) throws AquariusException {
		if (name == null || name.length() == 0 
				|| path == null || path.length() == 0
				|| idDomain == null || idDomain.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(404, "");
		}
		List<JsonNode> sas = getStorageAreas(idDomain, userId);
		for (JsonNode sa: sas) {
			if (sa.findValue("name").asText().equals(name)) {
				throw gutils.throwException(405, name);
			}
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("name", name);
		newObject.put("path", path);
		newObject.put("domain", idDomain);
		newObject.put("status", ECMParameters.SA_STATUS_ACTIVE);
		newObject.put("file", 0);
		if (createDirectoryStructure(path)) {
			if (bd.insertJson("storagearea", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(410, name + "-" + path);
			}
		} else {
			throw gutils.throwException(408, path);
		}
	}
	
	public String setStoragePolicy(String name, String type, String idDomain, List<String> listIdType, List<String> listIdStorage, String userId) throws AquariusException {
		if (name == null || name.length() == 0 
				|| type == null || type.length() == 0
				|| idDomain == null || idDomain.length() == 0
				|| listIdStorage == null || listIdStorage.size() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(404, "");
		}
		List<JsonNode> domains = getDomains("", userId);
		boolean findDomain = false;
		for (JsonNode domain: domains) {
			if (domain.findValue("id").asText().equals(idDomain)) {
				findDomain = true;
			}
		}
		if (!findDomain) {
			throw gutils.throwException(412, idDomain);
		} 
		List<JsonNode> sps = getStoragePolicy(idDomain, userId);
		for (JsonNode sp: sps) {
			if (sp.findValue("name").asText().equals(name)) {
				throw gutils.throwException(405, name);
			}
		}
		for (String idStorage: listIdStorage) {
			JsonNode storage = getStorageArea(idStorage, "", userId);
			if (storage == null) {
				throw gutils.throwException(413, idStorage);
			}
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("name", name);
		newObject.put("domain", idDomain);
		switch (type) {
			case ECMParameters.SP_TYPE_DOMAIN:
				newObject.put("type", ECMParameters.SP_TYPE_DOMAIN);
				break;
			case ECMParameters.SP_TYPE_CLASS:
				newObject.put("type", ECMParameters.SP_TYPE_CLASS);
				// validar que las clases existan:
				
				// agregar las clases que existan a la política:
				
				break;
			case ECMParameters.SP_TYPE_FOLDER:
				newObject.put("type", ECMParameters.SP_TYPE_FOLDER);
				// validar que los folders existan:
				
				// agregar los folders que existan a la política
				break;
			default:
				throw gutils.throwException(404, "");
		}
		ArrayNode storage_ids = newObject.putArray("storageId");
		for (String idStorage: listIdStorage) {
			storage_ids.add(idStorage);
		}
		if (bd.insertJson("storagepolicy", newObject)) {
			return newObject.findValue("id").asText();
		} else {
			throw gutils.throwException(414, name);
		}
		
	}
	
	public JsonNode getStorageArea(String id, String status, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->>'id' = '" + id + "' ";
		if (status != null && status.length() > 0) {
			where += " and data->>'status' = '" + status + "'";
		}
		List<JsonNode> result = bd.selectJson("storagearea", where, defaultOrderBy);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
		
	}
	
	private boolean updateFileStorageArea(String id, long file) throws AquariusException {
		if (id == null || id.length() == 0 ) {
			throw gutils.throwException(403, "");
		}
		//TODO: Test FileStorage Update:
		ObjectNode updates = mapper.createObjectNode();
		ArrayNode update = updates.putArray("fields");
		ObjectNode field = mapper.createObjectNode();
		field.put("field", "file");
		field.put("value", Long.toString(file));
		update.add(field);
		
		ObjectNode wheres = mapper.createObjectNode();
		ArrayNode where = wheres.putArray("fields");
		ObjectNode fieldWhere = mapper.createObjectNode();
		fieldWhere.put("field", "id");
		fieldWhere.put("operator", "=");
		fieldWhere.put("value", id);
		where.add(fieldWhere);
		return bd.updateJson("storagearea", updates, wheres);
	}
	
	public List<JsonNode> getDocumentClasses() throws AquariusException {
		String orderBy = "data->>'name'";
		return bd.selectJson("documentclass", null, orderBy);
		
	}
	
	public JsonNode getDocumentClass(String id) throws AquariusException {
		if (id == null || id.length() == 0 ) {
			throw gutils.throwException(403, "");
		}
		String orderBy = "data->>'name'";
		String where = "data->>'id' = '" + id + "' ";
		List<JsonNode> result = bd.selectJson("documentclass", where, orderBy);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
		
	}
	
	private String getSubPath(int file) {
		String num = String.format("%08d", file);
		return "/" + num.substring(0,2) + "/" + num.substring(2,4) + "/" + num.substring(4,6);
	}
	
	private boolean createDirectorySA(String path, int file) {
		if (file % 100 == 0) {
			String tempPath = path + getSubPath(file); 
			createDirectory(tempPath);
		}
		return true;
	}
	
	private boolean createDirectory(String path) {
		File newDir = new File(path);
		if (!newDir.exists()) {
			return newDir.mkdirs();
		}
		return false;
	}
	
	private boolean createDirectoryStructure(String path) throws AquariusException {
		if (path == null || path.length() == 0) {
			throw gutils.throwException(409, "");
		}
		File file = new File(path);
		if(file.isDirectory()){
			if(file.list().length == 0){
				return createDirectorySA(path,0);
			} else {
				throw gutils.throwException(408, path);
			}
		} else {
			if (createDirectory(path)) {
				return createDirectoryStructure(path);
			} else {
				throw gutils.throwException(408, path);
			}
		}
	}
	
	/**
	 * Crea un nuevo nodo
	 * @param name Nombre del nodo
	 * @param type tipo del nodo: "domain"|"folder"|"document"
	 * @param idParent id del nodo padre (los repositorios, carpetas default y subdominios no tiene idParent)
	 * @param idDomain id del dominio al que pertence (en caso de un dominio padre, no tiene idDomain)
	 * @param visible si es visible o no el nodo
	 * @return objeto Json conteniendo el nodo
	 * @throws AquariusException
	 */
	private ObjectNode setNode(String name, String type, String idParent, String idDomain, boolean visible, String userId) throws AquariusException {
		if (name == null || name.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(404, "");
		}
		if (idParent != null && idParent.length() > 0) {
			JsonNode parent = getObjectById(idParent, userId); 
			if (parent == null) {
				throw gutils.throwException(403, idParent);
			}
			JsonNode existName = getChildrenByName(idParent, name, userId);
			if (existName != null) {
				throw gutils.throwException(405, name);
			}
			if (idDomain == null || idDomain.length() == 0) {
				throw gutils.throwException(404, "");
			} else {
				if (!parent.findValue(ECMParameters.N_TYPE_DOMAIN).asText().equals(idDomain)) {
					throw gutils.throwException(411, idDomain);
				}
			}
		} else {
			if (idDomain != null && idDomain.length() > 0) {
				JsonNode  existName = getRootByName(idDomain, name, userId);
				if (existName != null) {
					throw gutils.throwException(405, name);
				}
				idParent = "";
			} else {
				if (type.equals(ECMParameters.N_TYPE_DOMAIN)) {
					List<JsonNode> domains = getDomains("", userId);
					for (JsonNode domain: domains) {
						if (domain.findValue("properties").findValue("name").asText().equals(name)) {
							throw gutils.throwException(405, name);
						}
					}
					idParent = "";
					idDomain = "";
				} else {
					throw gutils.throwException(404, type);
				}
				
			}
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("type", type);
		newObject.putArray("parent").add(idParent);
		newObject.put("domain", idDomain);
		newObject.put("visible", visible);
		newObject.put("status", ECMParameters.N_STATUS_ACTIVE);
		ObjectNode properties = mapper.createObjectNode();
		properties.put("name", name);
		properties.put("createdBy", userId);
		properties.putPOJO("createdOn", gutils.getTime());
		newObject.putPOJO("properties", properties);
		return newObject;
	}
	
	/**
	 * Crea un dominio nuevo.
	 * @param domainName nombre del dominio.
	 * @param idParent id del padre (en caso de subdominio). En caso de ser dominio "raíz", puede ser nulo.
	 * @return id del Dominio.
	 * @throws AquariusException
	 */
	public String setDomain(String domainName, String idDomain, String userId) throws AquariusException {
		String response = null;
		ObjectNode newDomain = setNode(domainName, ECMParameters.N_TYPE_DOMAIN, "", idDomain, true, userId);  
		if (bd.insertJson("node", newDomain)) {
			response = newDomain.findValue("id").asText();
			try {
				JsonNode defaultStorageArea = conf.getStorageAreaConf();
				String storageAreaName = defaultStorageArea.findValue(ECMParameters.SA_PREFIX).asText() + domainName;
				String storageAreaId = setStorageArea(storageAreaName,
						defaultStorageArea.findValue(ECMParameters.SA_DEFAULT_PATH).asText() + storageAreaName, response, userId);
				if (storageAreaId != null && storageAreaId.length() > 0) {
					try {
						JsonNode defaultStoragePolicy = conf.getStoragePolicyConf();
						String storagePolicyName = defaultStoragePolicy.findValue(ECMParameters.SP_PREFIX).asText() + domainName;
						List<String> listIdStorage = new ArrayList<String>();
						listIdStorage.add(storageAreaId);
						String storagePolicyId = setStoragePolicy(storagePolicyName, defaultStoragePolicy.findValue(ECMParameters.SP_TYPE).asText(), response, null, listIdStorage, userId);
						if (storagePolicyId != null && storagePolicyId.length() > 0) {
							JsonNode initialConf = conf.getDomainConf();
							if (initialConf != null) {
								for (Iterator<JsonNode> iter = initialConf.findValue(ECMParameters.N_TYPE_FOLDER).elements(); iter.hasNext(); ) {
									JsonNode json = iter.next();
									setFolder(json.findValue("name").asText(), "", response, json.findValue(ECMParameters.FOLDER_VISIBILITY).asBoolean(), userId);
								}
							}
						} else {
							System.out.println("Error al generar la Storage Policy... revertir creación de dominio y Storage Area");
						}
					} catch (AquariusException e) {
						System.out.println("Error al generar la Storage Policy... revertir creación de dominio y Storage Area");
					} 
				} else {
					System.out.println("Error al generar la Storage Area... revertir creación de dominio");
				}
			} catch (AquariusException e) {
				System.out.println("Error al generar la Storage Area... revertir creación de dominio");
			}
			
		}
		return response;
	}
	
	private ObjectNode setDocumentData(String id, String classId, int numVersion, String idUser, JsonNode properties) throws AquariusException {
		if (properties == null || properties.size() == 0) {
			throw gutils.throwException(404, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", id);
		newObject.put("metadataId", UUID.randomUUID().toString());
		newObject.put("numVersion", numVersion);
		newObject.put("status", ECMParameters.D_D_STATUS_ACTIVE);
		newObject.put("createdBy", idUser);
		newObject.putPOJO("createdOn", gutils.getTime());
		newObject.put("documentClass", classId);
		newObject.putPOJO("properties", properties);
		return newObject;
	}
	
	public ObjectNode getDocumentThumbnailPath(String idDoc, String idVersion, int numVersion, boolean lastVersion, String userId) throws AquariusException {
		if (idDoc == null || idDoc.length() == 0) {
			if (idVersion == null || idVersion.length() == 0) {
				throw gutils.throwException(403, "");
			}
		} else {
			if (numVersion == 0 && !lastVersion) {
				throw gutils.throwException(403, "");
			}
		}
		boolean isDefault = false;
		String mimeType = null;
		String storageId = null;
		String thumbnail = null;
		int file = -1;
		List<JsonNode> docContent = getDocumentContent(idDoc, idVersion, numVersion, lastVersion, userId);
		if (docContent == null || docContent.isEmpty()) {
			JsonNode node = getObjectById(idDoc, userId);
			if (node == null || node.size() == 0) {
				throw gutils.throwException(403, "");
			} else {
				isDefault = true;
				if (node.path("type") != null && node.path("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
					//TODO: BUSCAR CLASE DOCUMENTAL
					List<JsonNode> docMetadata = getDocumentMetadata(idDoc, null, 0, lastVersion, userId);
					if (docMetadata == null || docMetadata.isEmpty()) {
						throw gutils.throwException(403, "");
					} else {
						JsonNode doc = docMetadata.get(0);
						if (doc == null || doc.size() == 0) {
							throw gutils.throwException(403, idDoc);
						}
						if (doc.path("documentClass") != null && doc.path("documentClass").asText().length() > 0) {
							switch(doc.path("documentClass").asText()) {
								case "1":
									thumbnail = "document.png";
									break;
								case "2":
									thumbnail = "email.png";
									break;
								case "3":
									thumbnail = "link.png";
									break;
							}
						}
					}
				} else {
					thumbnail = "folder.png";
				}
			}
		} else {
			JsonNode doc = docContent.get(0);
			if (doc == null || doc.size() == 0) {
				throw gutils.throwException(403, idDoc);
			}
			file = doc.findValue("file").asInt();
			storageId = doc.findValue("storageId").asText();
			mimeType = doc.findValue("mimeType").asText();
			thumbnail = doc.findValue("thumbnail").asText();
			if (thumbnail == null || thumbnail.length() == 0) {
				if (mimeType != null && mimeType.length() > 0) {
					if (mimeType.startsWith("image")) {
						if (mimeType.endsWith("jpeg") || mimeType.endsWith("jpg")) {
							thumbnail = "jpg.png";
						} else if (mimeType.endsWith("png")) {
							thumbnail = "png.png";
						} else {
							thumbnail = "image.png";
						}
					} else if (mimeType.startsWith("audio")) {
						thumbnail = "audio.png";
					} else if (mimeType.startsWith("video")) {
						thumbnail = "video.png";
					} else if (mimeType.startsWith("application")) {
						if (mimeType.endsWith("excel") || mimeType.endsWith("spreadsheet")) {
							thumbnail = "excel.png";
						} else if (mimeType.endsWith("pdf")) {
							thumbnail = "pdf.png";
						} else if (mimeType.endsWith("powerpoint") || mimeType.endsWith("presentation")) {
							thumbnail = "powerpoint.png";
						} else if (mimeType.endsWith("msword") || mimeType.endsWith("rtf") || mimeType.endsWith("text")) {
							thumbnail = "word.png";
						} else if (mimeType.endsWith("compressed") || mimeType.endsWith("tar") || mimeType.endsWith("zip")) {
							thumbnail = "zip.png";
						} else if (mimeType.endsWith("xml")) {
							thumbnail = "html.png";
						} else {
							thumbnail = "document.png";
						}
					} else if (mimeType.startsWith("text")) {
						if (mimeType.endsWith("csv")) {
							thumbnail = "excel.png";
						} else if (mimeType.endsWith("html") || mimeType.endsWith("css") ) {
							thumbnail = "html.png";
						} else {
							thumbnail = "text.png";
						}
					}					
				} 
				isDefault = true;
			}
			if (thumbnail == null || thumbnail.length() == 0
					|| storageId == null || storageId.length() == 0
					|| mimeType == null || mimeType.length() == 0) {
				throw gutils.throwException(418, "");
			}
		}
		mimeType = ECMParameters.THUMB_MIME_TYPE;
		ObjectNode newObject = mapper.createObjectNode();
		if (isDefault) {
			if (thumbnail == null || thumbnail.length() == 0) {
				thumbnail = "document.png";
			}
			JsonNode defaultStorageArea = conf.getStorageAreaConf();
			newObject.put("path", defaultStorageArea.findValue(ECMParameters.SA_DEFAULT_PATH).asText() + ECMParameters.THUMB_PATH + "/" + thumbnail);
		} else {
			JsonNode storageArea = getStorageArea(storageId, null, userId);
			if (storageArea == null || storageArea.size() == 0) {
				throw gutils.throwException(413, storageId);
			}
			newObject.put("path", storageArea.findValue("path").asText() + getSubPath(file) + "/" + thumbnail);
		}
		newObject.put("mimeType", mimeType);
		return newObject;
	}
	
	public ObjectNode getDocumentContentPath(String idDoc, String idVersion, int numVersion, boolean lastVersion, String userId) throws AquariusException {
		
		if (idDoc == null || idDoc.length() == 0) {
			if (idVersion == null || idVersion.length() == 0) {
				throw gutils.throwException(403, "");
			}
		} else {
			if (numVersion == 0 && !lastVersion) {
				throw gutils.throwException(403, "");
			}
		}
		
		List<JsonNode> docContent = getDocumentContent(idDoc, idVersion, numVersion, lastVersion, userId);
		if (docContent == null || docContent.isEmpty()) {
			throw gutils.throwException(403, "");
		}
		JsonNode doc = docContent.get(0);
		if (doc == null || doc.size() == 0) {
			throw gutils.throwException(403, idDoc);
		}
		int file = doc.findValue("file").asInt();
		String storageId = doc.findValue("storageId").asText();
		String mimeType = doc.findValue("mimeType").asText();
		idVersion = doc.findValue("versionId").asText();
		if (idVersion == null || idVersion.length() == 0
				|| storageId == null || storageId.length() == 0
				|| mimeType == null || mimeType.length() == 0) {
			throw gutils.throwException(418, "");
		}
		JsonNode storageArea = getStorageArea(storageId, null, userId);
		if (storageArea == null || storageArea.size() == 0) {
			throw gutils.throwException(413, storageId);
		}
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("path", storageArea.findValue("path").asText() + getSubPath(file) + "/" + idVersion);
		newObject.put("mimeType", mimeType);
		return newObject;
	}

	public List<JsonNode> getDocumentMetadata(String id, String metadataId, int numVersion, boolean lastVersion, String userId) throws AquariusException {
		String where = "";
		if (id != null && id.length() > 0) {
			where = "data->>'id' = '" + id + "'";
			if (lastVersion) {
				where += " and data->>'status' = '" + ECMParameters.D_D_STATUS_ACTIVE + "'";
			} else {
				if (numVersion > 0) {
					where += " and data->>'numVersion'='" + numVersion + "'";
				}
			}
		} else {
			if (metadataId != null && metadataId.length() > 0) {
				where += " data->>'metadataId' = '" + metadataId + "'";
			} else {
				throw gutils.throwException(403, id);
			}
		}
		return bd.selectJson("docdata", where, null);
		
	}
	
	public JsonNode getObjectProperties(String id, String metadataId, int numVersionMetadata, String contentId, int numVersionContent, boolean lastVersion, String userId) throws AquariusException {
		if (id != null && id.length() > 0) {
			JsonNode node = getObjectById(id, userId);
			if (node != null) {
				ObjectNode response = mapper.createObjectNode();
				ObjectNode properties = mapper.createObjectNode();
				properties.put("name", node.findValue("properties").findValue("name").asText());
				properties.putPOJO("createdOn", node.findValue("properties").findValue("createdOn"));
				properties.put("createdBy", node.findValue("properties").findValue("createdBy").asText());
				properties.put("id", node.findValue("id").asText());
				properties.putPOJO("location", node.findValue("parent"));
				
				if (node.findValue("type").asText().equals(ECMParameters.N_TYPE_FOLDER)) {
					response.put("type", ECMParameters.N_TYPE_FOLDER);
					List<JsonNode> files =  bd.selectColumnsJson("count(data->>'id') as num", "node", 
							"data->'parent' ? '" + id + "' and data->>'type'='" + ECMParameters.N_TYPE_DOCUMENT + "'", null);
					if (files.size() > 0) {
						properties.put("numDocuments", files.get(0).findValue("num").asInt());
					}
					List<JsonNode> folders =  bd.selectColumnsJson("count(data->>'id') as num", "node", 
							"data->'parent' ? '" + id + "' and data->>'type'='" + ECMParameters.N_TYPE_FOLDER + "'", null);
					if (folders.size() > 0) {
						properties.put("numFolders", folders.get(0).findValue("num").asInt());
					}
				} else if (node.findValue("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
					String modifiedBy = null;
					JsonNode modifiedOn = null;
					response.put("type", ECMParameters.N_TYPE_DOCUMENT);
					if (contentId == null || numVersionContent < 0) {
						lastVersion = true;
					}
					List<JsonNode> contentList = getDocumentContent(id, contentId, numVersionContent, lastVersion, userId);
					if (contentList != null && contentList.size() > 0) {
						properties.put("mimeType", contentList.get(0).findValue("mimeType").asText());
						if (contentList.get(0).findValue("size") != null) {
							long sizeKb = contentList.get(0).findValue("size").asLong();
							long size = (sizeKb/1024);
							properties.put("size", String.format("%,d", size) + " KB");
						} else {
							properties.put("size", "");
						}
						int txtVersion = 0;
						if (contentList.get(0).findValue("numVersion") != null) {
							txtVersion = contentList.get(0).findValue("numVersion").asInt();
						} else {
							txtVersion = 0;
						}
						properties.put("contentVersion", txtVersion);
						properties.put("extension", contentList.get(0).findValue("extension").asText());
						modifiedBy = contentList.get(0).findValue("createdBy").asText();
						modifiedOn = contentList.get(0).findValue("createdOn");
					} else {
						// no hay contenido:
						properties.put("mimeType", "");
						properties.put("size", "");
						properties.put("extension", "");
						properties.put("contentVersion", 0);
					}
					if (metadataId == null || numVersionMetadata < 0) {
						lastVersion = true;
					}
					List<JsonNode> metadataList = getDocumentMetadata(id, metadataId, numVersionMetadata, lastVersion, userId);
					if (metadataList != null && metadataList.size() > 0) {
						properties.put("metadataVersion", (metadataList.get(0).findValue("numVersion").asInt()-1));
						JsonNode tempModifiedOn = metadataList.get(0).findValue("createdOn");
						if (tempModifiedOn.findValue("time").asLong() > modifiedOn.findValue("time").asLong()) {
							modifiedOn = tempModifiedOn;
							modifiedBy = metadataList.get(0).findValue("createdBy").asText();
						}
					}
					properties.putPOJO("modifiedOn", modifiedOn);
					properties.put("modifiedBy", modifiedBy);
				} else if (node.findValue("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
					response.put("type", ECMParameters.N_TYPE_DOMAIN);
				}
				response.putPOJO("properties", properties);
				return response;
			}
		}
		
		//
		return null;
		
	}
	
	public List<JsonNode> getDocumentContent(String id, String versionId, int numVersion, boolean lastVersion, String userId) throws AquariusException {
		String where = "";
		if (id != null && id.length() > 0) {
			where = "data->>'id' = '" + id + "'";
			if (lastVersion) {
				where += " and data->>'status' = '" + ECMParameters.D_V_STATUS_ACTIVE + "'";
			} else {
				if (numVersion > 0) {
					where += " and data->>'numVersion'='" + numVersion + "'";
				}
			}
		} else {
			if (versionId != null && versionId.length() > 0) {
				where += " data->>'versionId' = '" + versionId + "'";
			} else {
				throw gutils.throwException(403, id);
			}
		}
		return bd.selectJson("docversion", where, null);
		
	}

	private int getMaxVersionDocumentMetadata(String id) throws AquariusException {
		int numVersion = 0;
		if (id == null || id.length() == 0) {
			throw gutils.throwException(403, id);
		}
		String where = "data->>'id' = '" + id + "'";
		List<JsonNode> result = bd.selectColumnsJson("max(data->>'numVersion') as numversion", "docdata", where, null);
		if (result == null || result.size() == 0) {
			throw gutils.throwException(401, id);
		}
		for (JsonNode renglon:result) {
			numVersion = renglon.findValue("numversion").asInt();
		}
		return numVersion;
		
	}
	
	private int getMaxVersionDocumentContent(String id) throws AquariusException {
		int numVersion = 0;
		if (id == null || id.length() == 0) {
			throw gutils.throwException(403, id);
		}
		String where = "data->>'id' = '" + id + "'";
		List<JsonNode> result = bd.selectColumnsJson("max(data->>'numVersion') as numversion", "docversion", where, null);
		if (result == null || result.size() == 0) {
			throw gutils.throwException(401, id);
		}
		for (JsonNode renglon:result) {
			numVersion = renglon.findValue("numversion").asInt();
		}
		return numVersion;
		
	}
	
	private Boolean saveDocument(ObjectNode version, ObjectNode metadata) throws AquariusException {
		
		if (version != null && version.size() > 0) {
			if (!bd.insertJson("docversion", version)) {
				throw gutils.throwException(419, version.findValue("id").asText());
			}
			// actualizamos las versiones anteriores:
			ObjectNode updates = mapper.createObjectNode();
			ArrayNode update = updates.putArray("fields");
			ObjectNode field = mapper.createObjectNode();
			field.put("field", "status");
			field.put("value", "\"closed\"");
			update.add(field);
			
			ObjectNode wheres = mapper.createObjectNode();
			ArrayNode where = wheres.putArray("fields");
			ObjectNode fieldWhere1 = mapper.createObjectNode();
			fieldWhere1.put("field", "id");
			fieldWhere1.put("operator", "=");
			fieldWhere1.put("value", version.findValue("id").asText());
			where.add(fieldWhere1);
			ObjectNode fieldWhere2 = mapper.createObjectNode();
			fieldWhere2.put("field", "numVersion");
			fieldWhere2.put("operator", "!=");
			fieldWhere2.put("value", version.findValue("numVersion").asText());
			where.add(fieldWhere2);
			if (!bd.updateJson("docversion", updates, wheres)) {
				throw gutils.throwException(421, version.findValue("id").asText());
			}
		} 
		if (metadata != null && metadata.size() > 0) {
			if (!bd.insertJson("docdata", metadata)) {
				throw gutils.throwException(420, metadata.findValue("id").asText());
			}
			// actualizamos las versiones anteriores:
			ObjectNode updates = mapper.createObjectNode();
			ArrayNode update = updates.putArray("fields");
			ObjectNode field = mapper.createObjectNode();
			field.put("field", "status");
			field.put("value", "\"closed\"");
			update.add(field);
			
			ObjectNode wheres = mapper.createObjectNode();
			ArrayNode where = wheres.putArray("fields");
			ObjectNode fieldWhere1 = mapper.createObjectNode();
			fieldWhere1.put("field", "id");
			fieldWhere1.put("operator", "=");
			fieldWhere1.put("value", metadata.findValue("id").asText());
			where.add(fieldWhere1);
			ObjectNode fieldWhere2 = mapper.createObjectNode();
			fieldWhere2.put("field", "numVersion");
			fieldWhere2.put("operator", "!=");
			fieldWhere2.put("value", metadata.findValue("numVersion").asText());
			where.add(fieldWhere2);
			if (!bd.updateJson("docdata", updates, wheres)) {
				throw gutils.throwException(421, metadata.findValue("id").asText());
			}
		}
		return true;
		
	}
	
	public String setDocument(String id, String path, String idParent, 
			String idDomain, String documentClassId, String properties,
			MultipartFile file, String userId) throws AquariusException {
		JsonNode propertiesJson = null;
		ObjectNode metadata = null;
		if (id == null || id.length() == 0) {
			// nuevo documento:
			if (documentClassId == null || documentClassId.length() == 0
					|| properties == null || properties.length() == 0) {
				throw gutils.throwException(404, "");
			}
			try {
				propertiesJson = mapper.readTree(properties);
			} catch (Exception e) {
				throw gutils.throwException(404, "");
			}
			String docTitle = null;
			try {
				docTitle = propertiesJson.findValue(ECMParameters.D_FIELD_TITLE).asText();
			} catch (Exception e) {
				throw gutils.throwException(404, "");
			}
			if (docTitle == null) {
				throw gutils.throwException(404, "");
			}
			JsonNode documentClassJson = getDocumentClass(documentClassId);
			if (documentClassJson == null) {
				throw gutils.throwException(404, "");
			}
			//TODO: Debería sacar los campos de la clase documental y mapearlos con los del properties que me enviaron.
			if (path == null || path.length() == 0) {
				// se busca por Id de la carpeta padre:
				if (idParent == null || idParent.length() == 0
						|| idDomain == null || idDomain.length() == 0) {
					throw gutils.throwException(404, "");
				} else {
					// Verifico que el folder exista:
					JsonNode folder = getObjectById(idParent, userId);
					if (!folder.findValue(ECMParameters.N_FIELD_DOMAIN).asText().equals(idDomain)) {
						throw gutils.throwException(411, idDomain);
					}
				}
			} else {
				// se busca por el Path:
				JsonNode folder = getObjectByPath(path, userId);
				idParent = folder.findValue(ECMParameters.N_FIELD_ID).asText();
				idDomain = folder.findValue(ECMParameters.N_FIELD_DOMAIN).asText();
			}
			id = setDocumentObject(docTitle, idParent, idDomain, true, userId);
			metadata = setDocumentData(id, documentClassId,  getMaxVersionDocumentMetadata(id) + 1, userId, propertiesJson);
		} else {
			// ya existe el documento...
			JsonNode documentObject = getObjectById(id, userId);
			if (documentObject == null || documentObject.size() == 0) {
				throw gutils.throwException(401, id);
			} else {
				if (!documentObject.findValue("type").asText().equals(ECMParameters.N_TYPE_DOCUMENT)) {
					throw gutils.throwException(401, id);
				} else {
					if (documentClassId != null && documentClassId.length() > 0
							&& properties != null && properties.length() > 0) {
						try {
							propertiesJson = mapper.readTree(properties);
						} catch (Exception e) {
							throw gutils.throwException(404, "");
						}
						String docTitle = null;
						try {
							docTitle = propertiesJson.findValue(ECMParameters.D_FIELD_TITLE).asText();
						} catch (Exception e) {
							throw gutils.throwException(404, "");
						}
						if (docTitle == null) {
							throw gutils.throwException(404, "");
						}
						JsonNode documentClassJson = getDocumentClass(documentClassId);
						if (documentClassJson == null) {
							throw gutils.throwException(404, "");
						}
						metadata = setDocumentData(id, documentClassId,  getMaxVersionDocumentMetadata(id) + 1, userId, propertiesJson);
					}
					idDomain = documentObject.findValue("domain").asText();
					//TODO: Falta obtener el folder...
					idParent = null;
				}
			}
		}
		if (id != null) {
			// se guarda el objeto Document con la metadata:
			ObjectNode docVersion = null;
			if (file != null) {
				JsonNode storage = getSelectedStorageArea(idDomain, documentClassId, idParent, userId);
				String idVersion = UUID.randomUUID().toString();
				docVersion = fileService.upload(file, storage.findValue("path").asText() + "/" + idVersion);
				fileService.setOCRBatch(id, idVersion, userId);
				docVersion.put("id", id);
				docVersion.put("versionId", idVersion);
				docVersion.put("numVersion", getMaxVersionDocumentContent(id) + 1);
				docVersion.put("status", ECMParameters.D_D_STATUS_ACTIVE);
				docVersion.put("createdBy", userId);
				docVersion.putPOJO("createdOn", gutils.getTime());
				docVersion.put("storageId", storage.findValue("id").asText());
				docVersion.put("file", storage.findValue("file").asLong());
				//TODO: Aquí se debe de generar el thumbnail 
				docVersion.put("thumbnail", "");
				// se actualiza el storageArea sumandole 1 a sus files...
				updateFileStorageArea(storage.findValue("id").asText(), storage.findValue("file").asLong() + 1);
				// insertar el objeto de la versión
			}
			if (saveDocument(docVersion, metadata)) {
				return id;
			} else {
				// revertir la creación del nodo... y el almacenaje del documento, en el caso haya sido almacenado...
				throw gutils.throwException(415, "");
			}
		} else {
			throw gutils.throwException(415, "");
		}
	}
	
	public String setDocumentObject(String documentName, String idParent, String idDomain, boolean visible, String userId) throws AquariusException {
		String response = null;
		if (idDomain != null && idDomain.length() > 0) {
			ObjectNode newDocument = setNode(documentName, ECMParameters.N_TYPE_DOCUMENT, idParent, idDomain, visible, userId);
			if (bd.insertJson("node", newDocument)) {
				response = newDocument.findValue("id").asText();
			}
		}
		return response;
	}
	
	public String setFolder(String folderName, String idParent, String idDomain, boolean visible, String userId) throws AquariusException {
		String response = null;
		ObjectNode newFolder = setNode(folderName, ECMParameters.N_TYPE_FOLDER, idParent, idDomain, visible, userId);
		if (bd.insertJson("node", newFolder)) {
			response = newFolder.findValue("id").asText();
		}
		return response;
	}
	
	//TODO: Actualizar Nombre del nodo
	public boolean updateNodeName(String id, String name, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| name == null || name.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(444, "");
		}
		return true;
	}
	
	//TODO: Actualizar Visibilidad nodo
	public boolean updateNodeVisibility(String id, boolean visible, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(445, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Mover Folder
	public boolean moveFolder(String id, String idParent, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(424, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Copiar Folder
	public boolean copyFolder(String id, String idParent, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(425, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Eliminar Folder
	public boolean deleteFolder(String id, boolean trash, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(433, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Descargar Folder
	public String downloadFolder(String id, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT:
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT_METADATA:
			case ECMParameters.DOWNLOAD_FOLDER_LIST:
			case ECMParameters.DOWNLOAD_FOLDER_METADATA:
				return "/opt/ecm/sample/sample.zip";
			default:
				throw gutils.throwException(427, type);
		}
	}
	
	//TODO: Obtener link de Descarga Folder
	public String downloadFolderLink(String id, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT:
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT_METADATA:
			case ECMParameters.DOWNLOAD_FOLDER_LIST:
			case ECMParameters.DOWNLOAD_FOLDER_METADATA:
				return "http://f2m.com.mx/ASDDSSAWED";
			default:
				throw gutils.throwException(427, type);
		}
	}
	
	//TODO: Enviar correo de Descarga Folder
	public boolean downloadFolderEmail(String id, String email, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| email == null || email.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT:
			case ECMParameters.DOWNLOAD_FOLDER_CONTENT_METADATA:
			case ECMParameters.DOWNLOAD_FOLDER_LIST:
			case ECMParameters.DOWNLOAD_FOLDER_METADATA:
				return true;
			default:
				throw gutils.throwException(427, type);
		}
	}
	
	//TODO: Mover Documento
	public boolean moveDocument(String id, String idParent, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(435, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Copiar Documento
	public boolean copyDocument(String id, String idParent, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(436, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Eliminar Documento
	public boolean deleteDocument(String id, boolean trash, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(437, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Descargar Documento
	public String downloadDocument(String id, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
			case ECMParameters.DOWNLOAD_DOCUMENT_ORIGINAL:
			case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT:
			case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT_METADATA:
			case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_CSV:
			case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_PDF:
				return "/opt/ecm/sample/sample.zip";
			default:
				throw gutils.throwException(438, type);
		}
	}
	
	//TODO: Obtener link de Descarga Documento
	public String downloadDocumentLink(String id, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
		case ECMParameters.DOWNLOAD_DOCUMENT_ORIGINAL:
		case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT:
		case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT_METADATA:
		case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_CSV:
		case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_PDF:
				return "http://f2m.com.mx/ASDDSSAWED";
			default:
				throw gutils.throwException(438, type);
		}
	}
	
	//TODO: Enviar correo de Descarga Documento
	public boolean downloadDocumentEmail(String id, String email, String type, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| email == null || email.length() == 0
				|| type == null || type.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(426, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		switch (type) {
		case ECMParameters.DOWNLOAD_DOCUMENT_ORIGINAL:
		case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT:
		case ECMParameters.DOWNLOAD_DOCUMENT_ZIP_CONTENT_METADATA:
		case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_CSV:
		case ECMParameters.DOWNLOAD_DOCUMENT_METADATA_PDF:
				return true;
			default:
				throw gutils.throwException(438, type);
		}
	}
	
	//TODO: Compartir Documento
	public boolean shareDocument(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(439, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Compartir Folder
	public boolean shareFolder(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(439, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Set versión de documento
	public boolean setActualDocumentVersion(String id, String idMetadataVersion, String idContentVersion, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(440, "");
		}
		if (idMetadataVersion != null && idMetadataVersion.length() > 0) {
			return true;
		} else if (idContentVersion != null && idContentVersion.length() > 0) {
			return true;
		}
		return false;
	}
	
	//TODO: Checkin de documento
	public boolean checkInDocument(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(441, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Cancel-CheckIn de documento
	public boolean cancelCheckInDocument(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(442, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: Checkout de documento
	public boolean checkOutDocument(String id, String path, String idParent, 
			String idDomain, String documentClassId, String properties,
			MultipartFile file, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(443, "");
		}
		String[] ids = id.split("[|]+");
		for (String singleId:ids) {
			System.out.println(singleId);
		}
		return true;
	}
	
	//TODO: get template
	public List<JsonNode> getObjectTemplate(String id, String type, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| type == null || type.length() == 0) {
			throw gutils.throwException(429, "");
		}
		List<JsonNode> templates = new ArrayList<JsonNode>();
		for (int i = 0; i < 5; i++) {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("id", "ID" + i);
			newObject.put("type", type);
			newObject.put("name", "Nombre" + i);
			templates.add(newObject);
		}
		return templates;
	}
	
	//TODO: Guardar template
	public String setObjectTemplateById(String name, String idObject, String id, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| name == null || name.length() == 0
				|| idObject == null || idObject.length() == 0) {
			throw gutils.throwException(430, "");
		}
		return "AAA-BBB-CCC";
	}
	
	//TODO: Ejecutar template
	public boolean executeObjectTemplateById(String id, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| id == null || id.length() == 0) {
			throw gutils.throwException(431, "");
		}
		return true;
	}
	
	//TODO: Eliminar Template
	public boolean deleteObjectTemplate(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(432, "");
		}
		return true;
	}
	
	public String setFolderByPath(String folderName, String parentPath, boolean visible, String userId) throws AquariusException {
		JsonNode parent = getObjectByPath(parentPath, userId);
		return setFolder(folderName, parent.findValue("id").asText(), parent.findValue("domain").asText() , visible, userId);
	}
	
	public String setDocumentObjectByPath(String documentName, String parentPath, boolean visible, String userId) throws AquariusException {
		JsonNode object = getObjectByPath(parentPath, userId);
		return setDocumentObject(documentName, object.findValue("id").asText(), object.findValue("domain").asText(), visible, userId);
	}
	
	public List<JsonNode> getChildren(String idParent, String type, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->'parent' ? '" + idParent + "'";
		if (type != null && type.length() > 0) {
			where += " and data->>'type' = '" + type + "'";
		}
		return bd.selectJson("node", where, defaultOrderBy);
	}
	
	public List<JsonNode> getRoot(String idDomain, String type, String userId) throws AquariusException {
		if (idDomain == null || idDomain.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->>'domain' = '" + idDomain + "'";
		if (type != null && type.length() > 0) {
			where += " and data->>'type' = '" + type + "'";
		}
		where += "and data->'parent' ? ''";
		return bd.selectJson("node", where, defaultOrderBy);
	}
	
	public JsonNode getObjectById(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->>'id' = '" + id + "'";
		List<JsonNode> result = bd.selectJson("node", where, null);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public JsonNode getChildrenByName(String idParent, String childrenName, String userId) throws AquariusException {
		if (idParent == null || idParent.length() == 0
				|| childrenName == null || childrenName.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->'parent' ? '" + idParent + "'"
			+ " and data->'properties'->>'name' = '" + childrenName + "'";
		List<JsonNode> result = bd.selectJson("node", where, defaultOrderBy);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public JsonNode getRootByName(String idDomain, String childrenName, String userId) throws AquariusException {
		if (idDomain == null || idDomain.length() == 0
				|| childrenName == null || childrenName.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		String where = "data->>'domain' = '" + idDomain + "'"
			+ " and data->'properties'->>'name' = '" + childrenName + "'";
		List<JsonNode> result = bd.selectJson("node", where, defaultOrderBy);
		if (result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public JsonNode getObjectByPath(String path, String userId) throws AquariusException {
		if (path == null || path.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(401, "");
		}
		JsonNode response = null;
		String idDomain = "";
		String[] items = path.split("/");
		if (items.length >= 3) {
			List<JsonNode> domains = getDomains("", userId);
			for (JsonNode domain:domains) {
				if (domain.findValue("properties").findValue("name").asText().equals(items[1])) {
					response = domain;
					idDomain = domain.findValue("id").asText();
				}
			}
			// Recorrer Root:
			if (response != null) {
				response = getRootByName(idDomain, items[2], userId);		
				if (response != null) {
					for (int i = 3; i < items.length; i++) {
						response = getChildrenByName(response.findValue("id").asText(), items[i], userId);
						if (response == null) {
							throw gutils.throwException(403, items[i]);
						}
					}
				}
			} else {
				throw gutils.throwException(401, path);
			}
		} else {
			throw gutils.throwException(401, path);
		}
		return response;
	}
	
	public void printInfoNode(JsonNode node) {
		System.out.println("Nombre: " + node.findValue("properties").findValue("name").asText() + "\tId: " + node.findValue("id").asText() + "\tTipo: " + node.findValue("type").asText());
	}
	
	public void recorreArbol(JsonNode node, String userId) {
		printInfoNode(node);
		try {
			List<JsonNode> children = getChildren(node.findValue("id").asText(), "folder", userId);
			if (!children.isEmpty()) {
				for (JsonNode nodeChildren:children) {
					System.out.println("--> Hijo de: " + node.findValue("properties").findValue("name"));
					recorreArbol(nodeChildren, userId);
				}
			}
		} catch (AquariusException e) {
			e.printStackTrace();
		}
		
	}

	public ObjectNode getTree(String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		List<JsonNode> domains = getDomains("", userId);
		if (domains.size() > 1) {
			newObject.put("value", "/");
			ArrayNode childrens = newObject.putArray("children");
			for (JsonNode domain:domains) {
				childrens.add(getTreeDomainItem(domain, userId));
			}
		} else {
			newObject = getTreeDomainItem(domains.get(0), userId);
		}
		
		return newObject;
	}
	
	public ArrayNode getMultiSelectTree(String userId) throws AquariusException {
		List<JsonNode> domains = getDomains("", userId);
		ArrayNode childrens = mapper.createArrayNode();
		for (JsonNode domain:domains) {
			childrens.add(getTreeMultiselectDomainItem(domain, userId));
		}
		return childrens;
	}
	
	public ObjectNode getFolderList(String idFolder, String userId) throws AquariusException {
		if (idFolder == null || idFolder.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(403, "");
		}
		ObjectNode folderList = mapper.createObjectNode();
		// encabezado:
		ArrayNode headerRow = folderList.putArray("headerRow");
		headerRow.add("");
		headerRow.add("Nombre");
		headerRow.add("Tamaño");
		headerRow.add("Creador");
		headerRow.add("Fecha");
		headerRow.add("Version");
		
		ArrayNode dataRows = folderList.putArray("dataRows");
		// Documentos y carpetas:
		String selectAll = "node.data->'properties'->'name' as name," +
			"docversion.data->>'size' as size," +
			"node.data->'properties'->'createdBy' as createdBy," +
			"node.data->'properties'->'createdOn' as createdon," +
			"docversion.data->>'numVersion' as version," +
			"docdata.data->>'numVersion' as revision," +
			"node.data->'id' as id," +
			"node.data->'type' as type";
		String fromAll = "node " +
			"full outer join docversion " +
			"on (node.data->>'id' = docversion.data->>'id') " +
			"full outer join docdata " +
			"on (node.data->>'id' = docdata.data->>'id')";
		String whereAll = "node.data->'parent' ? '" + idFolder + "' and (" + 
				"(docdata.data->>'status'='active' and node.data->>'type' = 'document') " +
				"or node.data->>'type' = 'folder' or node.data->>'type' = 'domain')";
		/*
		String selectAll = "node.data->'properties'->'name' as name, " + 
			"(select docversion.data->>'size' from docversion " +
			" where node.data->>'id' = docversion.data->>'id' and docversion.data->>'status'='active') as size, " +
			" node.data->'properties'->'createdBy' as createdBy, " +  
			" node.data->'properties'->'createdOn' as createdon, " + 
			" (select docversion.data->>'numVersion' from docversion " +
			" where node.data->>'id' = docversion.data->>'id' " +
			" and docversion.data->>'status'='active') as version, " +
			" (select docdata.data->>'numVersion' from docdata " +
			" where node.data->>'id' = docdata.data->>'id' " +
			" and docdata.data->>'status'='active') as revision, " +
			" node.data->'id' as id, " + 
			" node.data->'type' as type ";
		String fromAll = "node";
		String whereAll = "node.data->'parent' ? '" + idFolder + "'";
		*/
		String orderByAll = "node.data->'type' desc, node.data->'properties'->>'name' asc, docversion.data->>'numVersion' desc";
		List<JsonNode> result = bd.selectColumnsJson(selectAll, fromAll, whereAll, orderByAll);
		HashSet<String> ids = new HashSet<String>();
		if (result != null) {
			for (JsonNode row:result) {
				String idObj = row.findValue("id").asText();
				if (!ids.contains(idObj)) {
					ids.add(idObj);
					String txtName = "";
					String txtSize = "";
					String txtCreator = "";
					String txtDate = "";
					String txtVersion = "";
					String type = "";
					txtName = row.findValue("name").asText();
					txtCreator = row.findValue("createdby").asText();
					txtDate = gutils.getTimeString(row.findValue("createdon"));
					type = row.findValue("type").asText();
					if (type.equals("document"))
					{
						int contentVersion = 0;
						if (row.findValue("version") != null) {
							contentVersion = row.findValue("version").asInt();
							txtVersion = Integer.toString(contentVersion)  + ".";
						} else {
							txtVersion = "0.";
						}
						txtVersion += Integer.toString(row.findValue("revision").asInt() - 1);
						if (contentVersion > 0) {
							if (row.findValue("size") != null) {
								long sizeKb = row.findValue("size").asLong();
								long size = (sizeKb/1024);
								txtSize = String.format("%,d", size) + " KB";
							}
						}
					} 
					ArrayNode dataRow = mapper.createArrayNode();
					dataRow.add(txtName);
					dataRow.add(txtSize);
					dataRow.add(txtCreator);
					dataRow.add(txtDate);
					dataRow.add(txtVersion);
					dataRow.add(idObj);
					dataRow.add(type);
					dataRows.add(dataRow);
				}
			}
		}
		
		return folderList;
	}
	
	private ObjectNode getTreeMultiselectDomainItem(JsonNode node, String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", node.findValue("id").asText());
		newObject.put("name", node.findValue("properties").findValue("name").asText());
		List<JsonNode> root = getRoot(node.findValue("id").asText(), ECMParameters.SP_TYPE_FOLDER, userId);
		ArrayNode childrens = newObject.putArray("children");
		for (JsonNode folder:root) {
			childrens.add(getTreeMultiselectItem(folder, userId));
		}
		return newObject;
	}
	
	private ObjectNode getTreeMultiselectItem(JsonNode node, String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", node.findValue("id").asText());
		newObject.put("name", node.findValue("properties").findValue("name").asText());
		List<JsonNode> hijos = getChildren(node.findValue("id").asText(), ECMParameters.SP_TYPE_FOLDER, userId);
		ArrayNode childrens = newObject.putArray("children");
		for (JsonNode hijo:hijos) {
			childrens.add(getTreeMultiselectItem(hijo, userId));
		}
		return newObject;
	}

	private ObjectNode getTreeDomainItem(JsonNode node, String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", node.findValue("id").asText());
		newObject.put("value", node.findValue("properties").findValue("name").asText());
		List<JsonNode> root = getRoot(node.findValue("id").asText(), ECMParameters.SP_TYPE_FOLDER, userId);
		ArrayNode childrens = newObject.putArray("children");
		for (JsonNode folder:root) {
			childrens.add(getTreeItem(folder, userId));
		}
		ObjectNode isCollapsed = mapper.createObjectNode();
		isCollapsed.put("isCollapsedOnInit", false);
		isCollapsed.put("rightMenu", false);
		ObjectNode templates = mapper.createObjectNode();
		templates.put("node", "<i class='fa fa-folder-o fa-lg'></i>");
		templates.put("leaf","<i class='fa fa-file-o fa-lg'></i>");
		templates.put("leftMenu", "<i class='fa fa-navicon fa-lg'></i>");
		isCollapsed.putPOJO("templates", templates);
		newObject.putPOJO("settings", isCollapsed);
		return newObject;
	}
	
	private ObjectNode getTreeItem(JsonNode node, String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", node.findValue("id").asText());
		newObject.put("value", node.findValue("properties").findValue("name").asText());
		List<JsonNode> hijos = getChildren(node.findValue("id").asText(), ECMParameters.SP_TYPE_FOLDER, userId);
		ArrayNode childrens = newObject.putArray("children");
		for (JsonNode hijo:hijos) {
			childrens.add(getTreeItem(hijo, userId));
		}
		ObjectNode isCollapsed = mapper.createObjectNode();
		isCollapsed.put("isCollapsedOnInit", true);
		isCollapsed.put("rightMenu", false);
		ObjectNode templates = mapper.createObjectNode();
		templates.put("node", "<i class='fa fa-folder-o fa-lg'></i>");
		templates.put("leaf","<i class='fa fa-file-o fa-lg'></i>");
		templates.put("leftMenu", "<i class='fa fa-navicon fa-lg'></i>");
		isCollapsed.putPOJO("templates", templates);
		newObject.putPOJO("settings", isCollapsed);	
		return newObject;
	}
	
	//TODO: Obtener favoritos
	public List<JsonNode> getObjectFavoritesByType(String type, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| type == null || type.length() == 0) {
			throw gutils.throwException(423, "");
		}
		List<JsonNode> favorites = new ArrayList<JsonNode>();
		for (int i = 0; i < 5; i++) {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("id", "ID" + i);
			newObject.put("type", type);
			newObject.put("name", "Nombre" + i);
			favorites.add(newObject);
		}
		return favorites;
	}
	
	//TODO: Guardar favoritos
	public String setObjectFavoritesById(String id, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| id == null || id.length() == 0) {
			throw gutils.throwException(422, "");
		}
		return "AAA-BBB-CCC";
	}
	
	//TODO: Eliminar favoritos
	public boolean deleteObjectFavoritesById(String id, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| id == null || id.length() == 0) {
			throw gutils.throwException(434, "");
		}
		return true;
	}
	
	public List<JsonNode> getStamp(String id, String name, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(450, "");
		}
		String from = "stamp";
		String where = "";
		String orderBy = "";
		if (id == null || id.length() == 0) {
			if (name == null || name.length() == 0) {
				return bd.selectJson(from, where, orderBy);
			} else {
				where = "data->>'name' = '" + name + "'";
				return bd.selectJson(from, where, orderBy);
			}
		} else {
			if (name == null || name.length() == 0) {
				where = "data->>'id' = '" + id + "'";
				return bd.selectJson(from, where, orderBy);
			} else {
				where = "data->>'id' = '" + id + "' and data->>'name' = '" + name + "'";
				return bd.selectJson(from, where, orderBy);
			}
		}
	}
	
	public boolean deleteStamp(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(452, "");
		}
		String where = "data->>'id' = '" + id + "'"; 
		return bd.deleteJson("stamp", where);
	}
	
	public String setStamp(String id, String name, MultipartFile file, String userId) throws AquariusException {
		if (name == null || name.length() == 0 
				|| userId == null || userId.length() == 0
				|| file == null) {
			throw gutils.throwException(449, "");
		}
		boolean exists = false;
		String createdBy = "";
		JsonNode createdOn = null;
		List<JsonNode> stamps = getStamp(id, name, userId);
		if (stamps != null && stamps.size() > 0) {
			id = stamps.get(0).path("id").asText();
			createdBy = stamps.get(0).path("createdBy").asText();
			createdOn = stamps.get(0).path("createdOn");
			if (deleteStamp(id, userId)) {
				exists = true;
			}
		}
		if (id == null || id.length() == 0) {
			id = UUID.randomUUID().toString();
		}
		JsonNode defaultStorageArea = conf.getStorageAreaConf();
		ObjectNode newObject = null;
		newObject = fileService.upload(file, defaultStorageArea.path("defaultPath").asText() + "stamp/" + id);
		newObject.put("id", id);
		newObject.put("name", name);
		if (!exists) {
			newObject.put("createdBy", userId);
			newObject.putPOJO("createdOn", gutils.getTime());
		} else {
			newObject.put("modifiedBy", userId);
			newObject.putPOJO("modifiedOn", gutils.getTime());
			newObject.put("createdBy", createdBy);
			newObject.putPOJO("createdOn", createdOn);
		}
		if (bd.insertJson("stamp", newObject)) {
			return id;
		}
		throw gutils.throwException(451, "");
	}
	
	public ObjectNode getStampPath(String id, String userId) throws AquariusException {
		if (id == null || id.length() == 0 
				|| userId == null || userId.length() == 0) {
			throw gutils.throwException(450, "");
		}
		List<JsonNode> stamp = getStamp(id, null, userId);
		if (stamp.size() == 1) {
			JsonNode defaultStorageArea = conf.getStorageAreaConf();
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("path", defaultStorageArea.path("defaultPath").asText() + "stamp/" + id);
			newObject.put("mimeType", stamp.get(0).path("mimeType").asText());
			return newObject; 
		} else {
			return null;
		}
	}
	
	public static void main (String[] args) {
		//CmisService cmis = new CmisService();
		try {
			//System.out.println(cmis.getFolderList("c9f53727-9c3f-4aba-8de1-2b6a6a4b0793", ""));
			//System.out.println(cmis.getObjectProperties("e49718ed-8545-45e5-92d1-c2b3ba86590e", null, -1, null, -1, true, ""));
			/*
			//System.out.println(cmis.getObjectProperties("100aefae-d481-4b2a-88f7-02b538c6d07c", null, 0, null, 0, false));
			System.out.println(cmis.getDocumentThumbnailPath("71048bcf-f9e5-446d-af80-d8bf74df0d73", null, 0, true));
			System.out.println(cmis.getDocumentThumbnailPath("53761dda-8959-41e2-ba8d-69bf69cfac34", null, 0, true));
			System.out.println(cmis.getDocumentThumbnailPath("0f70cf9f-ff4a-4499-8a1c-a57152ae7bde", null, 0, true));
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

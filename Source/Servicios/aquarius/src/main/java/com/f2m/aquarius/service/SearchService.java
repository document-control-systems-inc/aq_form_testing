package com.f2m.aquarius.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.f2m.aquarius.parameters.ECMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SearchService {
	
	private DBService bd = new DBService();
	private ObjectMapper mapper = new ObjectMapper();
	private GeneralUtils gutils = new GeneralUtils();
	private CmisService cmis = new CmisService();
	
	
	public List<JsonNode> getSearchCriteria(String idSearchCriteria) throws AquariusException {
		String where = null;
		if (idSearchCriteria != null && idSearchCriteria.length() > 0) {
			where = "data->>'search_criteria' = '" + idSearchCriteria + "'";
		}
		return bd.selectJson("searchcriteria", where, null);
	}
	
	public ArrayNode getSearchType(String idType) throws AquariusException {
		String where = null;
		if (idType != null && idType.length() > 0) {
			where = "data->>'type' = '" + idType + "'";
		}
		ArrayNode response = mapper.createArrayNode();
		List<JsonNode> searchTypes = bd.selectJson("searchtype", where, null);
		for (JsonNode searchType:searchTypes) {
			ObjectNode type = mapper.createObjectNode();
			type.put("type", searchType.path("type").asText());
			ArrayNode search_criterias = type.putArray("search_criteria");
			Iterator<JsonNode> it = searchType.path("search_criteria").iterator();
			while (it.hasNext()) {
				List<JsonNode> criterias = getSearchCriteria(it.next().asText());
				if (criterias != null && criterias.size() > 0) {
					search_criterias.add(criterias.get(0));
				}
			}
			response.add(type);
		}
		return response; 
	}
	
	public List<JsonNode> getStoredSearch(String id) throws AquariusException {
		String where = "";
		if (id != null && id.length() > 0) {
			where += "data->'info'->>'id' = '" + id + "' "; 
		}
		String orderBy = "data->'info'->'createdOn'->>'time' desc";
		return bd.selectJson("storedsearch", where, orderBy);
	}
	
	public String setStoredSearch(String searchString, String createdBy) throws AquariusException {
		if (searchString == null || searchString.length() == 0 || createdBy == null || createdBy.length() == 0) {
			throw gutils.throwException(1202, "");
		}
		String id;
		JsonNode search = null;
		try {
			search = mapper.readTree(searchString);
		} catch (Exception e) {
			throw gutils.throwException(1202, "");
		}
		if (search != null) {
			System.out.println("Se recibió: " + search.toString());
			ObjectNode storedSearch = mapper.createObjectNode();
			//validamos que el objeto tenga los items correctos:
			ObjectNode info = (ObjectNode)search.path("info");
			if (info != null) {
				if (info.path("name") == null || info.path("name").asText() == null || info.path("name").asText().length() == 0) {
					throw gutils.throwException(1202, "");
				}
				if (info.path("id") == null || info.path("id").asText() == null || info.path("id").asText().length() == 0) {
					id = UUID.randomUUID().toString();
					info.put("id", id);
				} else {
					id = info.path("id").asText();
				}
				if (info.path("createdBy") == null || info.path("createdBy").asText() == null || info.path("createdBy").asText().length() == 0) {
					info.put("createdBy", createdBy);
				}
				if (info.path("createdOn") == null) {
					info.putPOJO("createdOn", gutils.getTime());
				}
				storedSearch.putPOJO("info", info);
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("type") == null || search.path("type").asText() == null || search.path("type").asText().length() == 0) {
				throw gutils.throwException(1202, "");
			} else {
				if (search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_DOCUMENT) || search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_FOLDER)) {
					storedSearch.put("type", search.path("type").asText());
				} else {
					throw gutils.throwException(1202, "");
				}
			}
			if (search.path("searchIn") != null && search.path("searchIn").isArray() && search.path("searchIn").size() > 0) {
				storedSearch.putPOJO("searchIn", search.path("searchIn"));
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("includeChildren") != null && search.path("includeChildren").isBoolean()) {
				storedSearch.put("includeChildren", search.path("includeChildren").asBoolean());
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("documentClass") != null && search.path("documentClass").isArray() && search.path("documentClass").size() > 0) {
				//TODO: Validar clase documental?
				storedSearch.putPOJO("documentClass", search.path("documentClass"));
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("criteria") != null && search.path("criteria").isArray() && search.path("criteria").size() > 0) {
				ArrayNode criteriaTempArray = storedSearch.putArray("criteria");
				for (JsonNode criteriaTemp1:search.path("criteria")) {
					ObjectNode criteriaTemp = (ObjectNode) criteriaTemp1;
					if (criteriaTemp.path("lstCriteria") != null) {
						criteriaTemp.remove("lstCriteria");
					}
					if (criteriaTemp.path("selectedDate") != null) {
						criteriaTemp.remove("selectedDate");
					}
					if (criteriaTemp.path("writtenValue") != null) {
						criteriaTemp.remove("writtenValue");
					}
					criteriaTempArray.add(criteriaTemp);
				}
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("visualization") != null && search.path("visualization").isArray() && search.path("visualization").size() > 0) {
				
				storedSearch.putPOJO("visualization", search.path("visualization"));
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("orderBy") != null) {
				
				storedSearch.putPOJO("orderBy", search.path("orderBy"));
			} else {
				throw gutils.throwException(1202, "");
			}
			List<JsonNode> preExists = getStoredSearch(id);
			if (preExists != null && preExists.size() > 0) {
				if (!bd.deleteJson("storedsearch", "data->'info'->>'id' = '" + id + "' ")) {
					throw gutils.throwException(1203, "");
				}
			}
			if (bd.insertJson("storedsearch", storedSearch)) {
				return id;
			} else {
				throw gutils.throwException(1203, "");
			}
		} else {
			throw gutils.throwException(1202, "");
		}
	}
	
	public ObjectNode executeSearch(String searchString, String executeUser) throws AquariusException {
		if (searchString == null || searchString.length() == 0 || executeUser == null || executeUser.length() == 0) {
			throw gutils.throwException(1204, "");
		}
		JsonNode search = null;
		HashMap<String,JsonNode> documentClasses = new HashMap<String,JsonNode>();
		
		try {
			search = mapper.readTree(searchString);
		} catch (Exception e) {
			throw gutils.throwException(1204, "");
		}
		if (search != null) {
			System.out.println("Se recibió la siguiente búsqueda: " + search.toString());
			Set<String> selectList = new LinkedHashSet<String>();
			List<String> selectTypeList = new ArrayList<String>();
			Set<String> fromList = new LinkedHashSet<String>();
			Set<String> whereList = new LinkedHashSet<String>();
			Set<String> orderByList = new LinkedHashSet<String>();
			ObjectNode folderList = mapper.createObjectNode();
			ArrayNode headerRow = folderList.putArray("headerRow");
			headerRow.add("");
			headerRow.add("#");
			
			//validamos que el objeto tenga los items correctos:
			if (search.path("type") == null || search.path("type").asText() == null || search.path("type").asText().length() == 0
					|| (!search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_DOCUMENT)
					&& !search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_FOLDER))) {
				throw gutils.throwException(1202, "");
			} else {
				
				/*
				// Búsqueda por Folders:
				if (search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_FOLDER)) {
					String where = getCommonFolderWhere();
					String from = "node";
					// obtengo las clases documentales:
					ArrayNode docClassTemp = (ArrayNode)search.path("documentClass");
					JsonNode documentClassJson = cmis.getDocumentClass("4");
					if (search.path("visualization") != null && search.path("visualization").isArray() && search.path("visualization").size() > 0) {
						for (JsonNode visualizationJson:search.path("visualization")) {
							String header = gutils.rawString(documentClassJson.path("name").asText());
							for (JsonNode field:documentClassJson.path("fields")) {
								if (field.path("name").asText().equals(visualizationJson.path("field").asText())) {
									String auxSearchFields = header + "_" + gutils.rawString(field.path("label").asText());
									selectTypeList.add(field.path("type").asText());
									selectList.add(customSelect(visualizationJson.path("field").asText(), auxSearchFields, visualizationJson.path("documentClass").asText()));
									System.out.println("Agregando clase: " + visualizationJson.path("documentClass").asText());
									headerRow.add(field.path("label").asText());
								}
							}
						}
					} else {
						throw gutils.throwException(1202, "");
					}
					if (search.path("criteria") != null && search.path("criteria").isArray() && search.path("criteria").size() > 0) {
						//criterios de búsqueda
						for (JsonNode criteriaJson:search.path("criteria")) {
							for (JsonNode field:documentClassJson.path("fields")) {
								if (field.path("name").asText().equals(criteriaJson.path("field").asText())) {
									System.out.println("estoy en where: " + documentClassJson.toString());
									//whereList.add(criteriaWhere(field.path("name").asText(), field.path("type").asText(), criteriaJson));
									break;
								}
							}
						}
					} else {
						throw gutils.throwException(1202, "");
					}
					System.out.println("Select: " + createCustomQuery(selectList, true));
					System.out.println("Where: " + createCustomQuery(whereList, true));
				}
				*/
				
				
				
				if (search.path("documentClass") != null && search.path("documentClass").isArray() && search.path("documentClass").size() > 0) {
					// se aumenta la clase documental de "Propiedades de Sistema":
					ArrayNode docClassTemp = (ArrayNode)search.path("documentClass");
					if (search.path("type").asText().equals(ECMParameters.SEARCH_TYPE_DOCUMENT)) {
						docClassTemp.add("5");
						whereList.add(getCommonDocumentWhere());
					} else {
						whereList.add(getCommonFolderWhere());
					}
					for (JsonNode documentClassId:docClassTemp) {
						JsonNode documentClassJson = cmis.getDocumentClass(documentClassId.asText());
						if (documentClassJson != null) {
							if (!documentClasses.containsKey(documentClassId.asText())) {
								documentClasses.put(documentClassId.asText(), documentClassJson);
							}
						}
					}
				} else {
					throw gutils.throwException(1202, "");
				}
				fromList.add(getCommonFrom());
				if (search.path("visualization") != null && search.path("visualization").isArray() && search.path("visualization").size() > 0) {
					selectList.addAll(getCommonSelect());
					selectTypeList.addAll(getCommonSelectTypes());
					for (JsonNode visualizationJson:search.path("visualization")) {
						JsonNode documentClass = documentClasses.get(visualizationJson.path("documentClass").asText());
						if (documentClass != null) {
							String header = gutils.rawString(documentClass.path("name").asText());
							for (JsonNode field:documentClass.path("fields")) {
								if (field.path("name").asText().equals(visualizationJson.path("field").asText())) {
									String auxSearchFields = header + "_" + gutils.rawString(field.path("label").asText());
									selectTypeList.add(field.path("type").asText());
									selectList.add(customSelect(visualizationJson.path("field").asText(), auxSearchFields, visualizationJson.path("documentClass").asText()));
									System.out.println("Agregando clase: " + visualizationJson.path("documentClass").asText());
									switch(visualizationJson.path("documentClass").asText()) {
										case "4":
											fromList.add("node");
											break;
										case "5":
											fromList.add("node");
											fromList.add("docversion");
											break;
										default:
											fromList.add("docdata");
									}
									headerRow.add(field.path("label").asText());
								}
							}
						}
					}
				} else {
					throw gutils.throwException(1202, "");
				}
				if (search.path("criteria") != null && search.path("criteria").isArray() && search.path("criteria").size() > 0) {
					//criterios de búsqueda
					for (JsonNode criteriaJson:search.path("criteria")) {
						JsonNode documentClass = documentClasses.get(criteriaJson.path("documentClass").asText());
						if (documentClass != null) {
							for (JsonNode field:documentClass.path("fields")) {
								if (field.path("name").asText().equals(criteriaJson.path("field").asText())) {
									System.out.println("estoy en where: " + documentClass.toString());
									//whereList.add(criteriaWhere(field.path("name").asText(), field.path("type").asText(), criteriaJson));
									break;
								}
							}
						}
					}
				} else {
					throw gutils.throwException(1202, "");
				}
				
			}
			if (search.path("searchIn") != null && search.path("searchIn").isArray() && search.path("searchIn").size() > 0) {
				//TODO: en qué carpetas hacer la búsqueda
			} else {
				throw gutils.throwException(1202, "");
			}
			if (search.path("includeChildren") != null && search.path("includeChildren").isBoolean()) {
				//Si incluye hijos (de carpetas)
			} else {
				throw gutils.throwException(1202, "");
			}

			ArrayNode dataRows = folderList.putArray("dataRows");
			List<JsonNode> result = bd.selectColumnsJson(createCustomQuery(selectList, true), createCustomQuery(fromList, true), createCustomQuery(whereList, false), createCustomQuery(orderByList, false));
			if (result != null) {
				int count = 0;
				for (JsonNode row:result) {
					int i = 0;
					ArrayNode dataRow = mapper.createArrayNode();
					count++;
					dataRow.add(count);
					Iterator<String> iterFields = row.fieldNames();
					while (iterFields.hasNext()) {
						String field = iterFields.next();
						if (!row.path(field).isNull()) {
							switch(selectTypeList.get(i)) {
								case "DateTime":
									dataRow.add(gutils.getDateTimeString(row.path(field).asText()));
									break;
								default:
									dataRow.add(row.path(field).asText());
									break;
							}
						} else {
							dataRow.add("");
						}
						i++;
					}
					dataRows.add(dataRow);
					
					/*
					String txtName = "";
					String txtSize = "";
					String txtCreator = "";
					String txtDate = "";
					String txtVersion = "";
					String idObj = "";
					String type = "";
					txtName = row.path("name").asText();
					txtCreator = row.path("createdby").asText();
					idObj = row.path("id").asText();
					txtDate = gutils.getTimeString(row.path("createdon"));
					type = row.path("type").asText();
					if (type.equals("document"))
					{
						int contentVersion = 0;
						if (row.path("version") != null) {
							contentVersion = row.path("version").asInt();
							txtVersion = Integer.toString(contentVersion)  + ".";
						} else {
							txtVersion = "0.";
						}
						txtVersion += Integer.toString(row.path("revision").asInt() - 1);
						if (contentVersion > 0) {
							if (row.path("size") != null) {
								long sizeKb = row.path("size").asLong();
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
					*/
				}
			}
			
			// datos dummy:
			/*
			int numeroColumnas = (int) (Math.random() * 100) + 1;
			for (int i = 0; i < numeroColumnas;i++) {
				ArrayNode dataRow = mapper.createArrayNode();
				dataRow.add("test " + i);
				dataRow.add("10 kB");
				dataRow.add("ECM");
				dataRow.add("20/04/2018");
				dataRow.add("1");
				dataRow.add("{asdasdasda}");
				dataRow.add("document");
				dataRows.add(dataRow);
			}
			*/
			return folderList;
			
			
		} else {
			throw gutils.throwException(1202, "");
		}
	}
	
	private String createCustomQuery(Set<String> list, boolean comma) {
		String query = "";
		for (String item:list) {
			query += item;
			if (comma) {
				query += ",";
			}
		}
		if (query.endsWith(",")) {
			query = query.substring(0, query.length() - 1);
		}
		return query;
	}
	
	private Set<String> getCommonSelect() {
		Set<String> tempList = new LinkedHashSet<String>();
		tempList.add("node.data->'id' as _id");
		tempList.add("node.data->'type' as _type");
		return tempList;
	}
	
	private List<String> getCommonSelectTypes() {
		List<String> tempList = new ArrayList<String>();
		tempList.add("String");
		tempList.add("String");
		return tempList;
	}
	
	private String getCommonFrom() {
		return "node";
	}
	
	private String getCommonDocumentWhere() {
		return "(node.data->>'id' = docdata.data->>'id' and docdata.data->>'status'='active' and node.data->>'type' = 'document')";
	}
	
	private String getCommonFolderWhere() {
		return "(node.data->>'type' = 'folder')";
	}
	
	private String customSelect(String field, String label, String documentClass) {
		String response = "";
		switch(documentClass) {
			case "4":
				switch(field) {
					case "name":
						response += "node.data->'properties'->'name' as " + label;
						break;
					case "id":
						response += "node.data->'id' as " + label;
						break;
					case "createdOn":
						response += "node.data->'properties'->'createdOn'->'time' as " + label;
						break;
				}
				break;
			case "5":
				switch(field) {
					case "name":
						response += "node.data->'properties'->'name' as " + label;
						break;
					case "id":
						response += "node.data->'id' as " + label;
						break;
					case "createdOn":
						response += "node.data->'properties'->'createdOn'->'time' as " + label;
						break;
					case "createdBy":
						response += "node.data->'properties'->'createdBy' as " + label;
						break;
					case "contentVersion":
						response += "(select docversion.data->>'numVersion' from docversion " +
								" where node.data->>'id' = docversion.data->>'id' " +
								" and docversion.data->>'status'='active') as " + label;
						break;
					case "metadataVersion":
						response += "(select docdata.data->>'numVersion' from docdata " +
								" where node.data->>'id' = docdata.data->>'id' " +
								" and docdata.data->>'status'='active') as " + label;
						break;
					case "mimeType":
						response += "(select docversion.data->>'mimeType' from docversion " +
								" where node.data->>'id' = docversion.data->>'id' and docversion.data->>'status'='active') as " + label;
						break;
					case "extension":
						response += "(select docversion.data->>'extension' from docversion " +
								" where node.data->>'id' = docversion.data->>'id' and docversion.data->>'status'='active') as " + label;
						break;
					case "size":
						response += "(select docversion.data->>'size' from docversion " +
								" where node.data->>'id' = docversion.data->>'id' and docversion.data->>'status'='active') as " + label;
						break;
				}
				break;
			default:
				response += "docdata.data->'properties'->'" + field + "' as " + label;
		}
		return response;
	}
	
	private String whereClauseByDocumentClass(String field, String documentClass) {
		String response = "";
		switch(documentClass) {
			case "4":
				switch(field) {
				case "name":
					response += "node.data->'properties'->>'name'";
					break;
				case "id":
					response += "node.data->>'id'";
					break;
				case "createdOn":
					response += "node.data->'properties'->>'createdOn'->'time'";
					break;
			}
				break;
		}
		return response;
	}
	
	private String criteriaWhere(String field, String type, JsonNode criteria) throws AquariusException {
		
		if (field == null || field.length() == 0 || type == null || type.length() == 0 ||
				criteria.path("value") == null || criteria.path("value").size() == 0) {
			throw gutils.throwException(1204, "");
		}
		String response = "";
		String criteriaText = whereClauseByDocumentClass(field,criteria.path("documentClass").asText());
		switch(type) {
			case "String":
			case "Text":
				String searchText = "";
				for (JsonNode stringValue:criteria.path("value")) {
					searchText += stringValue.asText() + " ";
				}
				searchText = searchText.trim();
				if (criteria.path("lower") != null && criteria.path("lower").asBoolean()) {
					response += "lower(";
					searchText = searchText.toLowerCase();
				}
				//response += "docdata.data->'properties'->>'" + field + "'";
				response += criteriaText;
				if (criteria.path("lower") != null && criteria.path("lower").asBoolean()) {
					response += ")";
				}
				switch(criteria.path("search_criteria").asText()) {
					case "StartWith":
						response += " like '" + searchText + "%'";
						break;
					case "EndWith":
						response += " like '%" + searchText + "'";
						break;
					case "Equals":
						response += " = '" + searchText + "'";
						break;
					case "NotEquals":
						response += " != '" + searchText + "'";
						break;
					case "Like":
						response += " like '%" + searchText + "%'";
						break;
					case "NotLike":
						response += " not like '%" + searchText + "%'";
						break;
					case "IncludeAny":
						searchText = searchText.replaceAll(" ", "|");
						response += " similar to '%(" + searchText + ")%'";
						break;
					case "ExcludeAll":
						response += " not similar to '%(" + searchText + ")%'";
						break;
					case "IsEmpty":
						response += " = ''";
						break;
					case "IsNotEmpty":
						response += " != ''";
						break;
				}
			break;
			case "Integer":
			case "Numeric":
			case "Long":
			case "Decimal":
			case "Currency":
				//response += "(docdata.data->'properties'->>'" + field + "')::numeric";
				response += "(" + criteriaText + ")::numeric";
				ArrayList<String> searchDecimal = new ArrayList<String>();
				for (JsonNode stringValue:criteria.path("value")) {
					searchDecimal.add(stringValue.asText().trim());
				}
				if (searchDecimal.size() < 1) {
					if (!criteria.path("search_criteria").asText().equals("IsEmpty") && !criteria.path("search_criteria").asText().equals("IsNotEmpty")) {
						throw gutils.throwException(1204, "");
					}
				}
				switch(criteria.path("search_criteria").asText()) {
				case "Equals":
					response += " = " + searchDecimal.get(0);
					break;
				case "NotEquals":
					response += " != " + searchDecimal.get(0);
					break;
				case "LessThan":
					response += " < " + searchDecimal.get(0);
					break;
				case "LessThanOrEqual":
					response += " <= " + searchDecimal.get(0);
					break;
				case "GreaterThan":
					response += " > " + searchDecimal.get(0);
					break;
				case "GreaterThanOrEqual":
					response += " >= " + searchDecimal.get(0);
					break;
				case "IsEmpty":
					response += " is null";
					break;
				case "IsNotEmpty":
					response += " is not null";
					break;
				case "Between":
					response += " between " + searchDecimal.get(0) + " and " + searchDecimal.get(1);
					break;
				case "NotBetween":
					response += " not between " + searchDecimal.get(0) + " and " + searchDecimal.get(1);
					break;
			}
				break;
			case "Date":
			case "DateTime":
				System.out.println("Falta implementar DATE: ");
				for (JsonNode stringValue:criteria.path("value")) {
					System.out.println(stringValue.asText().trim());
				}
				break;
			case "List":
				System.out.println("Falta implementar LIST: ");
				for (JsonNode stringValue:criteria.path("value")) {
					System.out.println(stringValue.asText().trim());
				}
				break;
			case "Boolean":
				System.out.println("Falta implementar BOOLEAN: ");
				for (JsonNode stringValue:criteria.path("value")) {
					System.out.println(stringValue.asText().trim());
				}
				break;
		}
		return response;
	}
	
	public static void main (String[] args) {
		try {
			SearchService search = new SearchService();
			//
			
			//search.getStoredSearch(null);
			//System.out.println(search.executeSearch("{\"info\":{\"name\":null,\"createdBy\":\"ECM\",\"createdOn\":{\"time\":1513024746070,\"timezone\":\"America/Mexico_City\"}},\"type\":\"folder\",\"searchIn\":[\"f0bb73bf-1ca2-4c49-af31-6e85944092f4\"],\"includeChildren\":true,\"documentClass\":[\"4\"],\"criteria\":[{\"documentClass\":\"4\",\"field\":\"name\",\"search_criteria\":\"StartWith\",\"value\":[\"Ho\"],\"editable\":true,\"visible\":true,\"required\":false,\"operator\":true,\"fieldType\":\"String\",\"lstCriteria\":[{\"label\":\"Empieza Por\",\"search_criteria\":\"StartWith\"},{\"label\":\"Finaliza Con\",\"search_criteria\":\"EndWith\"},{\"label\":\"Como\",\"search_criteria\":\"Like\"},{\"label\":\"No es como\",\"search_criteria\":\"NotLike\"},{\"label\":\"Igual\",\"search_criteria\":\"Equals\"},{\"label\":\"No es igual\",\"search_criteria\":\"NotEquals\"},{\"label\":\"Incluir cualquiera\",\"search_criteria\":\"IncludeAny\"},{\"label\":\"Excluir todo\",\"search_criteria\":\"ExcludeAll\"},{\"label\":\"Esta vacío\",\"search_criteria\":\"IsEmpty\"},{\"label\":\"No está vacío\",\"search_criteria\":\"IsNotEmpty\"}],\"selectedDate\":null,\"writtenValue\":\"Ho\"},"
			//		+ "{\"documentClass\":\"4\",\"field\":\"name\",\"search_criteria\":\"IncludeAny\",\"value\":[\"s\"],\"editable\":true,\"visible\":true,\"required\":false,\"operator\":true,\"fieldType\":\"String\",\"lstCriteria\":[{\"label\":\"Empieza Por\",\"search_criteria\":\"StartWith\"},{\"label\":\"Finaliza Con\",\"search_criteria\":\"EndWith\"},{\"label\":\"Como\",\"search_criteria\":\"Like\"},{\"label\":\"No es como\",\"search_criteria\":\"NotLike\"},{\"label\":\"Igual\",\"search_criteria\":\"Equals\"},{\"label\":\"No es igual\",\"search_criteria\":\"NotEquals\"},{\"label\":\"Incluir cualquiera\",\"search_criteria\":\"IncludeAny\"},{\"label\":\"Excluir todo\",\"search_criteria\":\"ExcludeAll\"},{\"label\":\"Esta vacío\",\"search_criteria\":\"IsEmpty\"},{\"label\":\"No está vacío\",\"search_criteria\":\"IsNotEmpty\"}],\"selectedDate\":null,\"writtenValue\":\"Rep\"}],\"visualization\":[{\"documentClass\":\"4\",\"field\":\"name\"}],\"orderBy\":null}", "ECM"));
			//System.out.println(search.executeSearch("{ \"info\": { \"id\": \"7fcb2a0b-83b5-4138-9f7c-d67e0f7d2c4a\", \"name\": \"Mi búsqueda\", \"createdBy\": \"ECM\", \"createdOn\": { \"time\": 1513024746070, \"timezone\": \"America/Mexico_City\" } }, \"type\": \"document\", \"orderBy\": { \"field\": \"title\", \"ascending\": true, \"documentClass\": \"1\" }, \"criteria\": [ { \"field\": \"title\", \"value\": [ \"Doc\" ], \"visible\": true, \"editable\": true, \"operator\": true, \"required\": false, \"fieldType\": \"String\", \"documentClass\": \"1\", \"search_criteria\": \"StartWith\", \"lstCriteria\": [ { \"label\": \"Empieza Por\", \"search_criteria\": \"StartWith\" }, { \"label\": \"Finaliza Con\", \"search_criteria\": \"EndWith\" }, { \"label\": \"Como\", \"search_criteria\": \"Like\" }, { \"label\": \"No es como\", \"search_criteria\": \"NotLike\" }, { \"label\": \"Igual\", \"search_criteria\": \"Equals\" }, { \"label\": \"No es igual\", \"search_criteria\": \"NotEquals\" }, { \"label\": \"Incluir cualquiera\", \"search_criteria\": \"IncludeAny\" }, { \"label\": \"Excluir todo\", \"search_criteria\": \"ExcludeAll\" }, { \"label\": \"Esta vacío\", \"search_criteria\": \"IsEmpty\" }, { \"label\": \"No está vacío\", \"search_criteria\": \"IsNotEmpty\" } ], \"writtenValue\": \"Doc\" } ], \"searchIn\": [ \"ae563353-460d-49de-ae59-f1509a6e32e1\" ], \"documentClass\": [ \"1\" ], \"visualization\": [ { \"documentClass\": \"1\", \"field\": \"title\" } ], \"includeChildren\": true }", "ECM"));
			System.out.println(search.executeSearch("{ \"info\": { \"id\": \"7fcb2a0b-83b5-4138-9f7c-d67e0f7d2c4a\", \"name\": \"Mi búsqueda\", \"createdBy\": \"ECM\", \"createdOn\": { \"time\": 1513024746070, \"timezone\": \"America/Mexico_City\" } }, \"type\": \"document\", \"orderBy\": { \"field\": \"title\", \"ascending\": true, \"documentClass\": \"1\" }, \"criteria\": [ { \"field\": \"title\", \"value\": [ \"Doc\" ], \"visible\": true, \"editable\": true, \"operator\": true, \"required\": false, \"fieldType\": \"String\", \"documentClass\": \"1\", \"search_criteria\": \"StartWith\", \"lstCriteria\": [ { \"label\": \"Empieza Por\", \"search_criteria\": \"StartWith\" }, { \"label\": \"Finaliza Con\", \"search_criteria\": \"EndWith\" }, { \"label\": \"Como\", \"search_criteria\": \"Like\" }, { \"label\": \"No es como\", \"search_criteria\": \"NotLike\" }, { \"label\": \"Igual\", \"search_criteria\": \"Equals\" }, { \"label\": \"No es igual\", \"search_criteria\": \"NotEquals\" }, { \"label\": \"Incluir cualquiera\", \"search_criteria\": \"IncludeAny\" }, { \"label\": \"Excluir todo\", \"search_criteria\": \"ExcludeAll\" }, { \"label\": \"Esta vacío\", \"search_criteria\": \"IsEmpty\" }, { \"label\": \"No está vacío\", \"search_criteria\": \"IsNotEmpty\" } ], \"writtenValue\": \"Doc\" } ], \"searchIn\": [ \"ae563353-460d-49de-ae59-f1509a6e32e1\" ], \"documentClass\": [ \"1\" ], \"visualization\": [ { \"documentClass\": \"1\", \"field\": \"title\" } ], \"includeChildren\": true }", "ECM"));
			//System.out.println(search.setStoredSearch("{\"info\": {\"name\": \"Ejemplo\",\"createdBy\": \"ECM\",\"createdOn\": {\"time\": 1513024746070,\"timezone\": \"America/Mexico_City\"}},\"type\": \"document\",\"searchIn\": [\"b99dc2e5-3499-41f5-9df9-2e2cb964001d\", \"27843d16-d5d2-4eb7-b352-e0ce2ce1054c\"],\"includeChildren\": true,\"documentClass\": [\"1\", \"2\"],\"criteria\": [{\"documentClass\": \"1\",\"field\": \"title\",\"search_criteria\": \"IncludeAny\",\"value\": [\"aaa\"],\"editable\": true,\"visible\": true,\"required\": false,\"operator\": true,\"fieldType\": \"String\",\"lstCriteria\": [{\"label\": \"Empieza Por\",\"search_criteria\": \"StartWith\"}, {\"label\": \"Finaliza Con\",\"search_criteria\": \"EndWith\"}, {\"label\": \"Como\",\"search_criteria\": \"Like\"}, {\"label\": \"No es como\",\"search_criteria\": \"NotLike\"}, {\"label\": \"Igual\",\"search_criteria\": \"Equals\"}, {\"label\": \"No es igual\",\"search_criteria\": \"NotEquals\"}, {\"label\": \"Incluir cualquiera\",\"search_criteria\": \"IncludeAny\"}, {\"label\": \"Excluir todo\",\"search_criteria\": \"ExcludeAll\"}, {\"label\": \"Esta vacío\",\"search_criteria\": \"IsEmpty\"}, {\"label\": \"No está vacío\",\"search_criteria\": \"IsNotEmpty\"}],\"selectedDate\": null,\"writtenValue\": \"aaa\"}, {\"documentClass\": \"2\",\"field\": \"cc\",\"search_criteria\": \"Like\",\"value\": [\"a\"],\"editable\": true,\"visible\": true,\"required\": false,\"operator\": true,\"fieldType\": \"String\",\"lstCriteria\": [{\"label\": \"Empieza Por\",\"search_criteria\": \"StartWith\"}, {\"label\": \"Finaliza Con\",\"search_criteria\": \"EndWith\"}, {\"label\": \"Como\",\"search_criteria\": \"Like\"}, {\"label\": \"No es como\",\"search_criteria\": \"NotLike\"}, {\"label\": \"Igual\",\"search_criteria\": \"Equals\"}, {\"label\": \"No es igual\",\"search_criteria\": \"NotEquals\"}, {\"label\": \"Incluir cualquiera\",\"search_criteria\": \"IncludeAny\"}, {\"label\": \"Excluir todo\",\"search_criteria\": \"ExcludeAll\"}, {\"label\": \"Esta vacío\",\"search_criteria\": \"IsEmpty\"}, {\"label\": \"No está vacío\",\"search_criteria\": \"IsNotEmpty\"}],\"selectedDate\": null,\"writtenValue\": \"a\"}],\"visualization\": [{\"documentClass\": \"1\",\"field\": \"title\"}, {\"documentClass\": \"2\",\"field\": \"cc\"}, {\"documentClass\": \"2\",\"field\": \"title\"}, {\"documentClass\": \"2\",\"field\": \"sentOn\"}],\"orderBy\": {\"documentClass\": \"1\",\"field\": \"title\",\"ascending\": false}}", "ECM"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

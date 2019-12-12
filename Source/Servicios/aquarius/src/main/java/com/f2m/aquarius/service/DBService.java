package com.f2m.aquarius.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f2m.aquarius.parameters.DBConnectionParams;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.DBConnectionUtil;
import com.f2m.aquarius.utils.GeneralUtils;
import com.f2m.aquarius.utils.UtilsF2m;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBService {

	private UtilsF2m utilsf2m = new UtilsF2m("bd");
	private DBConnectionParams connectionParams;
	private GeneralUtils gutils = new GeneralUtils();
	private Connection connection;
	private ObjectMapper mapper = new ObjectMapper();
	private final static Logger logger = LoggerFactory.getLogger(DBService.class);
	
	public DBService () {
		
		connectionParams = new DBConnectionParams();
		
		connectionParams.setHostname(utilsf2m.getValue("hostname"));
		connectionParams.setPort(utilsf2m.getValue("port"));
		connectionParams.setDbName(utilsf2m.getValue("dbName"));
		connectionParams.setUserName(utilsf2m.getValue("userName"));
		connectionParams.setPassword(utilsf2m.getValue("password"));
		connectionParams.setValidationInterval(Integer.parseInt(utilsf2m.getValue("validationInterval")));
		connectionParams.setTimeBetweenEvictionRuns(Integer.parseInt(utilsf2m.getValue("timeBetweenEvictionRuns")));
		connectionParams.setMaxActive(Integer.parseInt(utilsf2m.getValue("maxActive")));
		connectionParams.setInitialSize(Integer.parseInt(utilsf2m.getValue("initialSize")));
		connectionParams.setMaxWait(Integer.parseInt(utilsf2m.getValue("maxWait")));
		connectionParams.setRemoveAbandonedTimeout(Integer.parseInt(utilsf2m.getValue("removeAbandonedTimeout")));
		connectionParams.setMinEvictableIdleTime(Integer.parseInt(utilsf2m.getValue("minEvictableIdleTime")));
		connectionParams.setMinIdle(Integer.parseInt(utilsf2m.getValue("minIdle")));
	}
	
	private String returnUpdateJson(String table, JsonNode updateFields, JsonNode whereFields) throws AquariusException {
		if (table == null || table.length() == 0
				|| updateFields == null || updateFields.size() == 0
				|| whereFields == null || whereFields.size() == 0) {
			throw gutils.throwException(502, "");
		}
		String query = "update " + table + " set ";
		Iterator<JsonNode> updates = updateFields.findValue("fields").elements();
		while (updates.hasNext()) {
			JsonNode values = updates.next();
			query += " data=jsonb_set(data, '{" + values.findValue("field").asText() + "}','" + values.findValue("value").asText() + "'),";
		}
		query = query.substring(0, query.length() - 1) + " where ";
		Iterator<JsonNode> wheres = whereFields.findValue("fields").elements();
		while (wheres.hasNext()) {
			JsonNode values = wheres.next();
			query += " data->>'" + values.findValue("field").asText() + "'" + values.findValue("operator").asText() + "'" + values.findValue("value").asText() + "' and";
		}
		query = query.substring(0, query.length() - 4) + ";";
		return query;
	}
	
	private String returnQueryInsertJson (String table, String value) throws AquariusException {
		if (table == null || table.length() == 0
				|| value == null || value.length() == 0) {
			throw gutils.throwException(502, "");
		}
		String query = "insert into " + table + " (\"data\") values ('" + value + "');";
		return query;
	}
	
	private String returnQuerySelect (String select, String from, String where, String orderBy) throws AquariusException {
		if (from == null || from.length() == 0) {
			throw gutils.throwException(502, "");
		}
		String query = "select " + select + " from " + from;
		if (where != null && where.length() > 0) {
			query += " where " + where;
		}
		if (orderBy != null && orderBy.length() > 0) {
			query += " order by " + orderBy;
		}
		return query;
	}
	
	private String returnQueryDelete(String from, String where) throws AquariusException {
		if (from == null || from.length() == 0) {
			throw gutils.throwException(502, "");
		}
		String query = "delete from " + from;
		if (where != null && where.length() > 0) {
			query += " where " + where;
		}
		return query;
	}
	
	private String convertMap(LinkedHashMap<String,Object> map) {
		List<String> response = new ArrayList<String>();
		for (String key:map.keySet()) {
			response.add("\"" + key + "\": " + map.get(key));
		}
		String strResponse = response.toString();
		strResponse = strResponse.substring(1, strResponse.length() - 1);
		return "{" + strResponse + "}";
	}
	
	public Boolean updateJson(String table, JsonNode updateFields, JsonNode whereFields) throws AquariusException {
		if (table == null || table.length() == 0
				|| updateFields == null || updateFields.size() == 0
				|| whereFields == null || whereFields.size() == 0) {
			throw gutils.throwException(502, "");
		}
		boolean response = false;
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement();
			statement.executeUpdate(returnUpdateJson(table, updateFields, whereFields));
			response = true;
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
			
		}
		return response;
	}
	
	public Boolean insertJson(String table, JsonNode json) throws AquariusException {
		if (table == null || table.length() == 0
				|| json == null || json.toString().length() == 0) {
			throw gutils.throwException(502, "");
		}
		boolean response = false;
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement();
			statement.executeUpdate(returnQueryInsertJson(table, json.toString()));
			response = true;
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
			
		}
		return response;
	}
	
	public Boolean deleteJson(String from, String where) throws AquariusException {
		if (from == null || from.length() == 0) {
			throw gutils.throwException(502, "");
		}
		boolean response = false;
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement();
			statement.executeUpdate(returnQueryDelete(from, where));
			response = true;
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
			
		}
		return response;
	}
	public List<JsonNode> selectColumnsJson(String select, String from, String where, String orderBy) throws AquariusException {
		String query = returnQuerySelect(select, from, where, orderBy);
		System.out.println("Query:" + query);
		List<JsonNode> response = new ArrayList<JsonNode>();
		ResultSet resultset = null;
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			resultset = statement.executeQuery(query);
			while ( resultset.next() ) {
				ResultSetMetaData metaData = resultset.getMetaData(); 
				int columnas = metaData.getColumnCount();
				LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= columnas; i++) {
					map.put(metaData.getColumnName(i), resultset.getString(i));
				}
				response.add(mapper.readTree(convertMap(map)));
			}
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} catch (AquariusException e) {
			throw e;
		} catch (JsonProcessingException e) {
			throw gutils.throwException(504, e.getMessage());
		} catch (IOException e) {
			throw gutils.throwException(504, e.getMessage());
		} finally {
			try {
				resultset.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
		}
		return response;
	}
	
	public List<JsonNode> selectJson(String from, String where, String orderBy) throws AquariusException {
		String query = returnQuerySelect("data", from, where, orderBy);
		List<JsonNode> response = new ArrayList<JsonNode>();
		ResultSet resultset = null;
		
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			resultset = statement.executeQuery(query);
			while ( resultset.next() ) {
				response.add(mapper.readTree(resultset.getString("data")));
			}
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} catch (AquariusException e) {
			throw e;
		} catch (JsonProcessingException e) {
			throw gutils.throwException(504, e.getMessage());
		} catch (IOException e) {
			throw gutils.throwException(504, e.getMessage());
		} finally {
			try {
				resultset.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
			
		}
		return response;
	}
	
	public List<JsonNode> selectJson1(String select, String from, String where, String orderBy) throws AquariusException {
		String query = returnQuerySelect(select + "as data", from, where, orderBy);
		List<JsonNode> response = new ArrayList<JsonNode>();
		ResultSet resultset = null;
		
		Statement statement = null;
		try {
			connection = DBConnectionUtil.getConnection(connectionParams);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			resultset = statement.executeQuery(query);
			while ( resultset.next() ) {
				response.add(mapper.readTree(resultset.getString("data")));
			}
		} catch (SQLException e) {
			throw gutils.throwException(503, e.getMessage());
		} catch (AquariusException e) {
			throw e;
		} catch (JsonProcessingException e) {
			throw gutils.throwException(504, e.getMessage());
		} catch (IOException e) {
			throw gutils.throwException(504, e.getMessage());
		} finally {
			try {
				resultset.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("Error al intentar cerrar la conexión a la Base de Datos");
			}
			
		}
		return response;
	}
}

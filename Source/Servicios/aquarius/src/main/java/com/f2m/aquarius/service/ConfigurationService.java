package com.f2m.aquarius.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f2m.aquarius.parameters.LDAPConnectionParams;
import com.f2m.aquarius.parameters.MailConnectionParams;
import com.f2m.aquarius.parameters.SessionParams;
import com.f2m.aquarius.utils.AquariusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationService {

	
	private LDAPConnectionParams ldap;
	private SessionParams session;
	private MailConnectionParams mail;
	private DBService db = new DBService();
	private ObjectMapper mapper = new ObjectMapper();
	private final static Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
	
	public ConfigurationService() {
	}
	
	public LDAPConnectionParams getLdap() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'ldap'", null);
			if (!obj.isEmpty()) {
				ldap = mapper.readValue(obj.get(0).toString() , LDAPConnectionParams.class);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración de LDAP: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error al obtener la configuración de LDAP: " + e.getMessage());
			e.printStackTrace();
		}
		return ldap;
	}
	
	public SessionParams getSession() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'session'", null);
			if (!obj.isEmpty()) {
				session = mapper.readValue(obj.get(0).toString() , SessionParams.class);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración de Sesión: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error al obtener la configuración de Sesión: " + e.getMessage());
		}
		return session;
	}
	
	public MailConnectionParams getMail() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'mail'", null);
			if (!obj.isEmpty()) {
				mail = mapper.readValue(obj.get(0).toString() , MailConnectionParams.class);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración de Mail: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error al obtener la configuración de Mail: " + e.getMessage());
		}
		return mail;
	}
	
	public JsonNode getDomainConf() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'domain'", null);
			if (!obj.isEmpty()) {
				return obj.get(0);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración de Dominio: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error al obtener la configuración de Dominio: " + e.getMessage());
		}
		return null;
	}
	
	public JsonNode getStorageAreaConf() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'storagearea'", null);
			if (!obj.isEmpty()) {
				return obj.get(0);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración default del Storage Area: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error al obtener la configuración default del Storage Area: " + e.getMessage());
		}
		return null;
	}
	
	public JsonNode getStoragePolicyConf() {
		try {
			List<JsonNode> obj = db.selectJson("configuration", "data->>'id' = 'storagepolicy'", null);
			if (!obj.isEmpty()) {
				return obj.get(0);
			}
		} catch (AquariusException e) {
			logger.error("Error al obtener la configuración default del Storage Policy: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error al obtener la configuración default del Storage Policy: " + e.getMessage());
		}
		return null;
	}
}

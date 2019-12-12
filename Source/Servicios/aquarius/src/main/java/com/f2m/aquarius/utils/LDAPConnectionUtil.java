package com.f2m.aquarius.utils;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f2m.aquarius.parameters.LDAPConnectionParams;

public class LDAPConnectionUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(LDAPConnectionUtil.class);

	public static LdapConnection openConnection(LDAPConnectionParams connectionParams) {
		LdapConnection connection = new LdapConnection(connectionParams.getLdapServer(),
				connectionParams.getLdapPort());
		try {
			connection.bind(connectionParams.getLdapUser(), connectionParams.getLdapPassword());
		} catch (Exception e) {
			logger.error("Error al abrir la conexi�n al LDAP " + e.getMessage());
			//e.printStackTrace();
		}
		return connection;
	}

	public static void closeConnection(LdapConnection connection) {
		try {
			connection.unBind();
			connection.close();
		} catch (Exception e) {
			logger.error("Error al cerrar la conexi�n al LDAP " + e.getMessage());
			//e.printStackTrace();
		}
	}
}

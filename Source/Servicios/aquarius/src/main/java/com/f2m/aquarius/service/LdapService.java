package com.f2m.aquarius.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.message.LdapResult;
import org.apache.directory.ldap.client.api.message.SearchResponse;
import org.apache.directory.ldap.client.api.message.SearchResultEntry;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.cursor.Cursor;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.entry.client.DefaultClientEntry;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.DN;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.f2m.aquarius.beans.Group;
import com.f2m.aquarius.beans.LdapEntity;
import com.f2m.aquarius.beans.User;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.configuration.AppConf;
import com.f2m.aquarius.parameters.LDAPConnectionParams;
import com.f2m.aquarius.parameters.SessionParams;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.f2m.aquarius.utils.LDAPConnectionUtil;

public class LdapService {

	private LDAPConnectionParams connectionParams;
	private SessionParams sessionParams;
	private GeneralUtils gutils = new GeneralUtils();
	private UserService userService = new UserService();

	public static final String BASE_ENTRY = "dc=f2m,dc=com,dc=mx";
	public static final String USER_ENTRY = "ou=Users,dc=f2m,dc=com,dc=mx";
	public static final String GROUP_ENTRY = "ou=Groups,dc=f2m,dc=com,dc=mx";

	public static final String USER_CLASS = "inetOrgPerson";
	public static final String GROUP_CLASS = "groupOfUniqueNames";
	public static final String OU_CLASS = "organizationalUnit";
	
	private static List<UserSession> sessions = new ArrayList<UserSession>();
	private static final long ONE_MINUTE_IN_MILLIS=60000;

	public LdapService() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(AppConf.class);
		ctx.refresh();
		connectionParams = ctx.getBean(LDAPConnectionParams.class);
		sessionParams = ctx.getBean(SessionParams.class);
		ctx.close();
	}
	
	public String getUidFromSession(UserSession userSession) {
		int index = sessions.indexOf(userSession);
		if (index > -1) {
			UserSession aux = sessions.get(index);
			return aux.getUserUid().toUpperCase();
		}
		return null;
	}
	
	public Boolean deleteSession(UserSession userSession) throws Exception {
		Boolean response = false;
		if (userSession.getSessionId() != null && userSession.getSessionId().length() > 0) {
			int index = sessions.indexOf(userSession);
			if (index > -1) {
				UserSession aux = sessions.get(index);
				if (userSession.getSessionId().equals(aux.getSessionId())
						&& userSession.getHostnameConnected().equals(aux.getHostnameConnected())) {
					sessions.remove(index);
					response = true;
				} else {
					throw gutils.throwException(1, "");
				}
			} else {
				throw gutils.throwException(4, "");
			}
		} else {
			throw gutils.throwException(5, "");
		}
		return response;
	}
	
	public Boolean deleteAllSessionsOfUser(UserSession userSession) throws Exception {
		Boolean response = false;
		if (userSession.getUserUid() != null && userSession.getUserUid().length() > 0) {
			int index = -1;
			do {
				index =	sessions.indexOf(userSession);
				if (index > -1) {
					sessions.remove(index);
					response = true;
				}
			} while (index > -1);
			if (!response) {
				throw gutils.throwException(4, "");
			}
		} else {
			throw gutils.throwException(5, "");
		}
		return response;
	}
	
	public UserSession createSession(UserSession userSession) throws Exception {
		validateSessionParams(userSession);
		Date actualTime = new Date();
		userSession.setSessionId(UUID.randomUUID().toString());
		userSession.setConnectedTime(actualTime);
		userSession.setLastTransactionTime(actualTime);
		sessions.add(userSession);
		return userSession;
	}
	
	private int validateSessionParams(UserSession userSession) throws AquariusException {
		if (sessionParams.getMaxUsers() > 0) {
			if (sessions.size() >= sessionParams.getMaxUsers()) {
				throw gutils.throwException(10, "");
			}
		}
		int numSessions = Collections.frequency(sessions, userSession);
		if (numSessions >= sessionParams.getMaxConcurrent()) {
			throw gutils.throwException(7, "");
		}
		return numSessions;
	}
	
	public Boolean validateSession(UserSession userSession) throws AquariusException {
		Boolean response = false;
		int numSessions = validateSessionParams(userSession);
		if (numSessions > 0) {
			if (userSession.getSessionId() != null) {
				List<UserSession> auxList = sessions.subList(0, sessions.size());
				for (int i = 0; i < numSessions; i++) {
					int index = auxList.indexOf(userSession);
					Date actualTime = new Date();
					if (index > -1) {
						UserSession aux = sessions.get(index);
						if (userSession.getSessionId().equals(aux.getSessionId())
								&& userSession.getHostnameConnected().equals(aux.getHostnameConnected())) {
							long t = aux.getLastTransactionTime().getTime();
							Date limitTime = new Date(t + (sessionParams.getMinutes() * ONE_MINUTE_IN_MILLIS));
							if (actualTime.getTime() < limitTime.getTime()) {
								aux.setLastTransactionTime(actualTime);
								sessions.remove(index);
								sessions.add(aux);
								response = true;
							} else {
								sessions.remove(index);
								throw gutils.throwException(1, "");
							}
						}						
					}
					if (response) {
						break;
					} else {
						auxList = auxList.subList(index, auxList.size());
					}
				}
				if (!response) {
					throw gutils.throwException(6, "");
				}
			}
		}
		return response;
	}
	
	/**
	 * Cifra la contraseña en el formato Md5.
	 * 
	 * @param password
	 *            Contraseña cifrar.
	 * @return Contraseña cifrada en formato Md5.
	 * @throws Exception
	 *             Si no cumple con los requisitos obligatorios.
	 */
	private String digestMd5(final String password) throws Exception {
		String base64 = "";
		if (password == null || password.length() == 0) {
			throw gutils.throwException(103, "");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			base64 = new Base64().encodeToString(digest.digest());
		} catch (Exception e) {
			//e.printStackTrace();
			throw gutils.throwException(104, e.getMessage());
		}
		return "{MD5}" + base64;
	}

	/**
	 * Verifica la contraseña de un usuario.
	 * 
	 * @param user
	 *            objeto del usuario a verificar contraseña. Dn y password
	 *            obligatorios.
	 * @return Afirmativo si las credenciales son correctas.
	 * @throws Exception
	 *             Si no cumple los requisitos obligatorios.
	 */
	public Boolean checkPassword(User user) throws Exception {
		if (user == null || user.getDn() == null || user.getDn().length() == 0 || user.getPassword() == null
				|| user.getPassword().length() == 0) {
			throw gutils.throwException(105, "");
		}
		String errormg = null;
		boolean credentialsValid = false;
		LdapConnection connection = null;
		try {
			connection = new LdapConnection(connectionParams.getLdapServer(), connectionParams.getLdapPort());
			credentialsValid = (connection.bind(user.getDn(), user.getPassword()).getLdapResult()
					.getResultCode() == ResultCodeEnum.SUCCESS);
		} catch (Exception e) {
			errormg = user.getUid() + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(106, errormg);
		}
		return credentialsValid;
	}

	/**
	 * Actualiza la contrasña de un usuario.
	 * 
	 * @param user
	 *            objeto del usuario a verificar contraseña. Dn y password
	 *            obligatorios.
	 * @return Afirmativo si se actualizó la contraseña.
	 * @throws Exception
	 *             Si no cumple con los requisitos obligatorios.
	 */
	public Boolean changePassword(User user) throws Exception {
		if (user == null || user.getDn() == null || user.getDn().length() == 0 || user.getPassword() == null
				|| user.getPassword().length() == 0) {
			throw gutils.throwException(107, "");
		}
		String errormg = null;
		boolean changePassword = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			DN dn = new DN(user.getDn());
			Entry entry = new DefaultClientEntry(dn);
			if (user.getPassword() != null) {
				if (user.getPassword().length() > 0) {
					entry.add(SchemaConstants.USER_PASSWORD_AT, digestMd5(user.getPassword()));
				}
			}
			changePassword = (connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE).getLdapResult()
					.getResultCode() == ResultCodeEnum.SUCCESS);
		} catch (Exception e) {
			errormg = e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(108, errormg);
		}
		return changePassword;
	}

	/**
	 * Elimina un usuario del LDAP.
	 * 
	 * @param userDn
	 *            DN del usuario a eliminar.
	 * @return Afirmativo si logró eliminar al usuario.
	 * @throws Exception
	 *             Si no cumple con los requisitos mínimos.
	 */
	public Boolean deleteUser(String userDn) throws Exception {
		if (userDn == null || userDn.length() == 0) {
			throw gutils.throwException(109, "");
		}
		boolean isException = false;
		String errormg = null;
		boolean deleteUser = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			User auxUser = getUserByDN(userDn);
			DN dn = new DN(userDn);
			LdapResult ldapResult = connection.delete(dn).getLdapResult();
			deleteUser = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
			if (deleteUser) {
				removeUserFromGroup(userDn, auxUser.getGroups());
			} else {
				errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
			}
		} catch (Exception e) {
			isException = true;
			errormg = userDn + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(110, errormg);
			}
			else {
				throw gutils.throwException(118, errormg);
			}
		}
		return deleteUser;
	}

	/**
	 * Elimina un grupo del LDAP.
	 * 
	 * @param groupDn
	 *            DN del grupo a eliminar.
	 * @return Afirmativo si logró eliminar al grupo.
	 * @throws Exception
	 *             Si no cumple con los requisitos mínimos.
	 */
	public Boolean deleteGroup(String groupDn) throws Exception {
		if (groupDn == null || groupDn.length() == 0) {
			throw gutils.throwException(203, "");
		}
		boolean isException = false;
		String errormg = null;
		boolean deleteGroup = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			DN dn = new DN(groupDn);
			LdapResult ldapResult = connection.delete(dn).getLdapResult();
			deleteGroup = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
			if (!deleteGroup) {
				errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
			}
		} catch (Exception e) {
			errormg = groupDn + " " + e.getMessage();
			isException = true;
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(204, errormg);
			}
			else {
				throw gutils.throwException(215, errormg);
			}
		}
		return deleteGroup;
	}

	/**
	 * Ejecuta el Query para obtener usuarios.
	 * 
	 * @param query
	 *            query a ejecutar, ya sea por CN o por DN.
	 * @return Lista de usuarios que cumplen con los criterios del Query.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	private List<User> getUsersByQuery(String query) throws Exception {
		if (query == null || query.length() == 0) {
			throw gutils.throwException(111, "");
		}
		String errormg = null;
		List<User> users = new ArrayList<User>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);

			Cursor<SearchResponse> cursor = connection.search(USER_ENTRY, query, SearchScope.SUBTREE, "*");
			while (cursor.next()) {
				User user = new User();
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				user.setDn(response.getEntry().getDn().toString());
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("uid".equalsIgnoreCase(key)) {
						user.setUid(getUidWithDomainByDN(user.getDn()));
						//user.setUid(attribute.getString());
					} else if ("sn".equalsIgnoreCase(key)) {
						user.setLastName(attribute.getString());
					} else if ("givenName".equalsIgnoreCase(key)) {
						user.setGivenName(attribute.getString());
					} else if ("mail".equalsIgnoreCase(key)) {
						user.setMail(attribute.getString());
					}
				}
				user.setGroups(getGroupsByUserDN(user.getDn()));
				try {
					user = userService.getUserAditionalInfo(user);	
				} catch (AquariusException e) {
					if (gutils.getErrorCode(e.getMessage()) == 121) {
						user = userService.getDefaultValues(user);
						if (!userService.setUserAditionalInfo(user)) {
							throw gutils.throwException(101, user.getUid());
						}
					} else {
						throw e;
					}
				}
				users.add(user);
			}
			cursor.close();
		} catch (Exception e) {
			errormg = query + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(112, errormg);
		}
		return users;
	}

	/**
	 * Obtiene la informaci�n de un usuario en la búsqueda por DN
	 * 
	 * @param dnUser
	 *            DN del usuario a buscar.
	 * @return Si encuentra el usuario, el objeto del Usuario. Si no lo
	 *         encuentra, null.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public User getUserByDN(String dnUser) throws Exception {
		if (dnUser == null || dnUser.length() == 0) {
			throw gutils.throwException(109, "");
		}
		String uid = "";
		int indexOf = dnUser.indexOf(",");
		if (indexOf > -1) {
			uid = dnUser.substring(0, indexOf);
		}
		String query = "(" + uid + ")";
		List<User> users = getUsersByQuery(query);
		for (User user : users) {
			if (user.getDn().equals(dnUser)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Obtiene la información de un usuario por dominio.
	 * @param domain El dominio del usuario en el formato ou\[...]\ uid.
	 * @return Si encuentra el usuario, el objeto de Usuario. Si no lo encuentra, null.
	 * @throws Exception Si no se cumple con los requisitos mínimos.
	 */
	public User getUserByDomain(String domain) throws Exception {
		if (domain == null || domain.length() == 0) {
			throw gutils.throwException(113, "");
		}
		String dn = "";
		String ou = "";
		if (!domain.startsWith("uid=")) {
			dn += "uid=";
		}
		int indexOfOu = 0;
		while (indexOfOu > -1) {
			indexOfOu = domain.indexOf("\\");
			if (indexOfOu > -1) {
				String auxOu = domain.substring(0, indexOfOu);
				domain = domain.substring(indexOfOu + 1);
				if (!auxOu.startsWith("ou=")) {
					ou += "ou=" + auxOu + ",";
				} else {
					ou += auxOu + ",";
				}
			}
		}
		dn += domain + "," + ou + LdapService.USER_ENTRY;
		return getUserByDN(dn);
	}
	
	/**
	 * Obtiene la informaci�n de uno o m�s usuarios en la búsqueda por su Uid.
	 * 
	 * @param uid
	 *            Uid del usuario a buscar.
	 * @return Lista de objetos User si se encuentran. La lista regresa vacñ si
	 *         es que no se encuentra el usuario.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public List<User> getUserByUid(String uid) throws Exception {
		if (uid == null || uid.length() == 0) {
			throw gutils.throwException(114, "");
		}
		String query = "(";
		if (!uid.startsWith("uid=")) {
			query += "uid=";
		}
		int indexOf = uid.indexOf("," + USER_ENTRY);
		if (indexOf > -1) {
			uid = uid.substring(0, indexOf);
		}
		query += uid + ")";
		return getUsersByQuery(query);
	}

	/**
	 * Obtiene la informaci�n de todos los usuarios del LDAP.
	 * 
	 * @return Lista de objetos User de todos los usuarios del LDAP.
	 */
	public List<User> listAllUsers() throws Exception {
		String errormg = null;
		List<User> users = new ArrayList<User>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			String query = "(objectClass=" + USER_CLASS + ")";
			Cursor<SearchResponse> cursor = connection.search(USER_ENTRY, query, SearchScope.SUBTREE, "*");
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				User user = new User();
				user.setDn(response.getEntry().getDn().toString());
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("uid".equalsIgnoreCase(key)) {
						user.setUid(getUidWithDomainByDN(user.getDn()));
						//user.setUid(attribute.getString());
					} else if ("sn".equalsIgnoreCase(key)) {
						user.setLastName(attribute.getString());
					} else if ("givenName".equalsIgnoreCase(key)) {
						user.setGivenName(attribute.getString());
					} else if ("mail".equalsIgnoreCase(key)) {
						user.setMail(attribute.getString());
					}
				}
				user.setGroups(getGroupsByUserDN(user.getDn()));
				try {
					user = userService.getUserAditionalInfo(user);	
				} catch (Exception e) {
					if (gutils.getErrorCode(e.getMessage()) == 121) {
						user = userService.getDefaultValues(user);
						if (!userService.setUserAditionalInfo(user)) {
							throw gutils.throwException(101, user.getUid());
						}
					} else {
						throw e;
					}
				}
				users.add(user);
			}
			cursor.close();
		} catch (Exception e) {
			errormg = e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(115, errormg);
		}
		return users;
	}

	/**
	 * Obtiene la informaci�n de todos los grupos del LDAP.
	 * 
	 * @return Lista de objetos Group de todos los usuarios del LDAP.
	 */
	public List<Group> listAllGroups() throws Exception {
		String errormg = null;
		List<Group> groups = new ArrayList<Group>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			String query = "(objectClass=" + GROUP_CLASS + ")";
			Cursor<SearchResponse> cursor = connection.search(GROUP_ENTRY, query, SearchScope.SUBTREE, "*");
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				Group group = new Group();
				group.setDn(response.getEntry().getDn().toString());
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("cn".equalsIgnoreCase(key)) {
						group.setCn(attribute.getString());
					} else if ("uniqueMember".equalsIgnoreCase(key)) {
						Iterator<Value<?>> iter = attribute.iterator();
						while (iter.hasNext()) {
							Value<?> members = iter.next();
							if (members.toString().length() > 0) {
								group.addMember(members.toString());
							}
						}
					}
				}
				groups.add(group);
			}
			cursor.close();
		} catch (Exception e) {
			errormg = e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(205, errormg);
		}
		return groups;
	}

	/**
	 * Inserta o actualiza un grupo
	 * 
	 * @param newGroup
	 *            Objeto Group del grupo a insertar o actualizar. Es requerido
	 *            el campo CN. Si el DN no es colocado, lo busca en la ra�z.
	 * @return Afirmativo si pudo insertar o actualizar el grupo.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean insertGroup(Group newGroup) throws Exception {
		if (newGroup == null || newGroup.getCn() == null || newGroup.getCn().length() == 0) {
			throw gutils.throwException(206, "");
		}
		boolean isException = false;
		String errormg = null;
		boolean insertGroup = false;
		LdapConnection connection = null;
		try {
			boolean isNuevo = true;
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			String query = "(cn=" + newGroup.getCn() + ")";
			Cursor<SearchResponse> cursor = connection.search(GROUP_ENTRY, query, SearchScope.SUBTREE, "*");
			List<String> Dns = new ArrayList<String>();
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Dns.add(response.getEntry().getDn().toString());
			}
			if (newGroup.getDn() == null || newGroup.getDn().length() == 0) {
				newGroup.setDn("cn=" + newGroup.getCn() + "," + GROUP_ENTRY);
			}
			if (Dns.size() > 0) {
				if (Dns.contains(newGroup.getDn())) {
					isNuevo = false;
				}
			}
			DN dn = new DN(newGroup.getDn());
			Entry entry = new DefaultClientEntry(dn);
			entry.add(SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.GROUP_OF_UNIQUE_NAMES_OC);
			entry.add(SchemaConstants.CN_AT, newGroup.getCn());
			if (!isNuevo) {
				entry.remove(SchemaConstants.UNIQUE_MEMBER_AT, "");
				insertGroup = (connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE).getLdapResult()
						.getResultCode() == ResultCodeEnum.SUCCESS);
			}
			if (newGroup.getMembers() == null) {
				entry.add(SchemaConstants.UNIQUE_MEMBER_AT, "");
			} else {
				if (newGroup.getMembers().size() == 0) {
					entry.add(SchemaConstants.UNIQUE_MEMBER_AT, "");
				} else {
					for (String member : newGroup.getMembers()) {
						if (getUserByDN(member) != null) {
							entry.add(SchemaConstants.UNIQUE_MEMBER_AT, member);
						}
					}
				}
			}
			LdapResult ldapResult = null;
			if (isNuevo) {
				ldapResult = connection.add(entry).getLdapResult();
			} else {
				ldapResult = connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE).getLdapResult();
			}
			insertGroup = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
			if (!insertGroup) {
				if (ldapResult != null) {
					errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
				}
			}
		} catch (Exception e) {
			isException = true;
			errormg = newGroup.getCn() + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(207, errormg);
			}
			else {
				throw gutils.throwException(216, errormg);
			}
		}
		return insertGroup;
	}

	/**
	 * Inserta o actualiza un usuario
	 * 
	 * @param newUser
	 *            Objeto User del usuario a insertar o actualizar. Es requerido
	 *            el campo UID. Si el DN no es colocado, lo busca en la ra�z.
	 * @return Afirmativo si pudo insertar o actualizar el usuario.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean insertUser(User newUser) throws Exception {
		if (newUser == null || newUser.getUid() == null || newUser.getUid().length() == 0) {
			throw gutils.throwException(116, "");
		}
		boolean isException = false;
		String errormg = null;
		boolean insertUser = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			boolean isNuevo = true;
			String query = "(uid=" + newUser.getUid() + ")";
			Cursor<SearchResponse> cursor = connection.search(USER_ENTRY, query, SearchScope.SUBTREE, "*");
			List<String> Dns = new ArrayList<String>();
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Dns.add(response.getEntry().getDn().toString());
			}
			if (newUser.getDn() == null || newUser.getDn().length() == 0) {
				newUser.setDn("uid=" + newUser.getUid() + "," + USER_ENTRY);
			}
			if (Dns.size() > 0) {
				if (Dns.contains(newUser.getDn())) {
					isNuevo = false;
				}
			}
			DN dn = new DN(newUser.getDn());
			Entry entry = new DefaultClientEntry(dn);
			entry.add(SchemaConstants.CN_AT, newUser.getUid());
			if (newUser.getGivenName() == null || newUser.getGivenName().length() == 0) {
				newUser.setGivenName(" ");
			}
			entry.add("givenName", newUser.getGivenName());
			if (newUser.getLastName() == null || newUser.getLastName().length() == 0) {
				newUser.setLastName(" ");
			}
			entry.add(SchemaConstants.SN_AT, newUser.getLastName());
			entry.add(SchemaConstants.DISPLAY_NAME_AT, newUser.getGivenName() + " " + newUser.getLastName());
			if (newUser.getMail() == null || newUser.getMail().length() == 0) {
				newUser.setMail(" ");
			}
			entry.add("mail", newUser.getMail());
			boolean isPassword = false;
			if (newUser.getPassword() != null && newUser.getPassword().length() > 0) {
				entry.add(SchemaConstants.USER_PASSWORD_AT, digestMd5(newUser.getPassword()));
				isPassword = true;
			}
			LdapResult ldapResult = null;
			if (isNuevo) {
				if (isPassword) {
					entry.add(SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.INET_ORG_PERSON_OC);
					ldapResult = connection.add(entry).getLdapResult();
					insertUser = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
					if (insertUser) {
						if (newUser.getGroups() != null) {
							addUserToGroup(newUser.getDn(), newUser.getGroups());
						}
					}
				} else {
					errormg = "Error Code [103]";
				}
			} else {
				ldapResult = connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE).getLdapResult();
				insertUser = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
				if (newUser.getGroups() != null && insertUser) {
					List<String> gruposActuales = getGroupsByUserDN(newUser.getDn());
					removeUserFromGroup(newUser.getDn(), gruposActuales);
					addUserToGroup(newUser.getDn(), newUser.getGroups());
				}
			}
			if (!insertUser) {
				if (ldapResult != null) {
					errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
				}
			}
		} catch (Exception e) {
			isException = true;
			errormg = e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(117, errormg);
			}
			else {
				throw gutils.throwException(119, errormg);
			}
		}
		return insertUser;
	}

	/**
	 * Ejecuta el Query para obtener grupos.
	 * 
	 * @param query
	 *            query a ejecutar, ya sea por CN o por DN.
	 * @return Lista de grupos que cumplen con los criterios del Query.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	private List<Group> getGroupsByQuery(String query) throws Exception {
		if (query == null || query.length() == 0) {
			throw gutils.throwException(208, "");
		}
		String errormg = null;
		List<Group> groups = new ArrayList<Group>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			Cursor<SearchResponse> cursor = connection.search(GROUP_ENTRY, query, SearchScope.SUBTREE, "*");
			while (cursor.next()) {
				Group group = new Group();
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				group.setDn(response.getEntry().getDn().toString());
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("cn".equalsIgnoreCase(key)) {
						group.setCn(attribute.getString());
					} else if ("uniqueMember".equalsIgnoreCase(key)) {
						Iterator<Value<?>> iter = attribute.iterator();
						while (iter.hasNext()) {
							Value<?> members = iter.next();
							if (members.toString().length() > 0) {
								group.addMember(members.toString());
							}
						}
					}
				}
				groups.add(group);
			}
			cursor.close();
		} catch (Exception e) {
			errormg = query + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(209, errormg);
		}
		return groups;
	}

	/**
	 * Obtiene la informaci�n de un grupo en la búsqueda por DN
	 * 
	 * @param dnGroup
	 *            DN del usuario a buscar.
	 * @return Si encuentra el grupo, el objeto de Group. Si no lo encuentra,
	 *         null.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Group getGroupByDN(String dnGroup) throws Exception {
		if (dnGroup == null || dnGroup.length() == 0) {
			throw gutils.throwException(203, "");
		}
		String cnGroup = "";
		int indexOf = dnGroup.indexOf(",");
		if (indexOf > -1) {
			cnGroup = dnGroup.substring(0, indexOf);
		}
		String query = "(" + cnGroup + ")";
		List<Group> groups = getGroupsByQuery(query);
		for (Group group : groups) {
			if (group.getDn().equals(dnGroup)) {
				return group;
			}
		}
		return null;
	}

	/**
	 * Obtiene la información de un grupo por dominio.
	 * @param domain El dominio del grupo en el formato ou\[...]\cn.
	 * @return Si encuentra el grupo, el objeto de Group. Si no lo encuentra, null.
	 * @throws Exception Si no se cumple con los requisitos mínimos.
	 */
	public Group getGroupByDomain(String domain) throws Exception {
		if (domain == null || domain.length() == 0) {
			throw gutils.throwException(210, "");
		}
		String dn = "";
		String ou = "";
		if (!domain.startsWith("cn=")) {
			dn += "cn=";
		}
		int indexOfOu = 0;
		while (indexOfOu > -1) {
			indexOfOu = domain.indexOf("\\");
			if (indexOfOu > -1) {
				String auxOu = domain.substring(0, indexOfOu);
				domain = domain.substring(indexOfOu + 1);
				if (!auxOu.startsWith("ou=")) {
					ou += "ou=" + auxOu + ",";
				} else {
					ou += auxOu + ",";
				}
			}
		}
		dn += domain + "," + ou + LdapService.GROUP_ENTRY;
		return getGroupByDN(dn);
	}
	
	/**
	 * Obtiene la informaci�n de uno o m�s Grupos en la búsqueda por su CN.
	 * 
	 * @param cn
	 *            CN del grupo a buscar.
	 * @return Lista de objetos Group si se encuentran. La lista regresa vacío
	 *         si es que no se encuentra el grupo.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public List<Group> getGroupByCN(String cn) throws Exception {
		if (cn == null || cn.length() == 0) {
			throw gutils.throwException(211, "");
		}
		String query = "(";
		if (!cn.startsWith("cn=")) {
			query += "cn=";
		}
		int indexOf = cn.indexOf(",");
		if (indexOf > -1) {
			cn = cn.substring(0, indexOf);
		}
		query += cn + ")";
		return getGroupsByQuery(query);
	}

	/**
	 * Obtiene la lista de DN de los grupos a los que pertenece un usuario.
	 * 
	 * @param userDn
	 *            DN del usuario que se intenta buscar los grupos a los que
	 *            pertence.
	 * @return Lista de String de DN de los grupos a los que pertence el usuario.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	private List<String> getGroupsByUserDN(String userDn) throws Exception {
		if (userDn == null || userDn.length() == 0) {
			throw gutils.throwException(203, "");
		}
		String errormg = null;
		List<String> groups = new ArrayList<String>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			Cursor<SearchResponse> cursor = connection.search(GROUP_ENTRY, "(uniqueMember= " + userDn + ")",
					SearchScope.SUBTREE, "*");
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				groups.add(response.getEntry().getDn().toString());
			}
			cursor.close();

		} catch (Exception e) {
			errormg = userDn + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(212, errormg);
		}
		return groups;
	}

	/**
	 * Regresa los miembros del grupo
	 * 
	 * @param groupCN
	 *            cn del grupo
	 * @return Lista con String de DN de los usuarios
	 */
	public List<String> getGroupMembers(String groupCN) throws Exception {
		if (groupCN == null || groupCN.length() == 0) {
			throw gutils.throwException(211, "");
		}
		String errormg = null;
		List<String> users = new ArrayList<String>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);

			Cursor<SearchResponse> cursor = connection.search(GROUP_ENTRY, "(cn=" + groupCN + ")", SearchScope.SUBTREE,
					"*");
			while (cursor.next()) {
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("uniqueMember".equalsIgnoreCase(key)) {
						Iterator<Value<?>> iter = attribute.iterator();
						while (iter.hasNext()) {
							Value<?> members = iter.next();
							if (members.toString().length() > 0) {
								users.add(members.toString());
							}
						}
					}
				}
			}
			cursor.close();
		} catch (Exception e) {
			errormg = groupCN + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(213, errormg);
		}
		return users;
	}

	/**
	 * Agrega un usuario por su DN como miembro de uno o varios grupos (por su
	 * DN).
	 * 
	 * @param userDn
	 *            DN del usuario a agregar.
	 * @param groups
	 *            Lista de Strings con los DN de los grupos a donde se agregará
	 *            al usuario.
	 * @return Afirmativo si pudo insertar o actualizar el usuario como miembro
	 *         de los grupos.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean addUserToGroup(String userDn, List<String> groups) throws Exception {
		if (userDn == null || userDn.length() == 0 || groups == null) {
			throw gutils.throwException(214, "");
		}
		boolean addUserToGroup = true;
		for (String userGroups : groups) {
			Group grupo = getGroupByDN(userGroups);
			if (grupo != null && grupo.getMembers() != null) {
				grupo.addMember(userDn);
				addUserToGroup = insertGroup(grupo);
			}
		}
		return addUserToGroup;
	}

	/**
	 * Elimina un usuario por su DN como miembro de uno o varios grupos (por su
	 * DN).
	 * 
	 * @param userDn
	 *            DN del usuario a quitar.
	 * @param groups
	 *            Lista de Strings con los DN de los grupos a donde se eliminará
	 *            al usuario.
	 * @return Afirmativo si pudo eliminar el usuario como miembro de los
	 *         grupos.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean removeUserFromGroup(String userDn, List<String> groups) throws Exception {
		if (userDn == null || userDn.length() == 0 || groups == null) {
			throw gutils.throwException(214, "");
		}
		boolean removeUserToGroup = true;
		for (String group : groups) {
			Group grupo = getGroupByDN(group);
			if (grupo != null && grupo.getMembers() != null) {
				grupo.removeMember(userDn);
				removeUserToGroup = insertGroup(grupo);
			}
		}
		return removeUserToGroup;
	}

	/**
	 * Inserta una Organization Unit.
	 * 
	 * @param ou
	 *            Nombre de la OU que se desea insertar.
	 * @param dnOu
	 *            DN donde se insertará la OU.
	 * @return Afirmativo si pudo insertar la Organization Unit.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean insertOrganizationUnit(String ou, String dnOu) throws Exception {
		if (ou == null || ou.length() == 0 || dnOu == null || dnOu.length() == 0) {
			throw gutils.throwException(301, "");
		}
		boolean isException = false; 
		String errormg = null;
		boolean insertOrganizationUnit = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			DN dn = new DN(dnOu);
			Entry entry = new DefaultClientEntry(dn);
			entry.add(SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.ORGANIZATIONAL_UNIT_OC);
			entry.add(SchemaConstants.ORGANIZATIONAL_UNIT_NAME_AT, ou);
			LdapResult ldapResult = connection.add(entry).getLdapResult();
			insertOrganizationUnit = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
			if (!insertOrganizationUnit) {
				errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
			}
		} catch (Exception e) {
			isException = true;
			errormg = ou + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(302, errormg);
			}
			else {
				throw gutils.throwException(307, errormg);
			}
		}
		return insertOrganizationUnit;
	}

	/**
	 * Elimina una Organization Unit.
	 * 
	 * @param dnOu
	 *            DN de la OU que se eliminará.
	 * @return Afirmativo si pudo eliminar la Organization Unit.
	 * @throws Exception
	 *             Si no se cumple con los requisitos mínimos.
	 */
	public Boolean deleteOrganizationUnit(String dnOu) throws Exception {
		if (dnOu == null || dnOu.length() == 0) {
			throw gutils.throwException(303, "");
		}
		boolean isException = false;
		String errormg = null;
		boolean deleteOrganizationUnit = false;
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			DN dn = new DN(dnOu);
			LdapResult ldapResult = connection.deleteTree(dn).getLdapResult();
			deleteOrganizationUnit = (ldapResult.getResultCode() == ResultCodeEnum.SUCCESS);
			if (!deleteOrganizationUnit) {
				errormg = ldapResult.getErrorMessage() + " " + ldapResult.getResultCode().toString();
			}
		} catch (Exception e) {
			isException = true;
			errormg = dnOu + " " + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			if (isException) {
				throw gutils.throwException(304, errormg);
			}
			else {
				throw gutils.throwException(308, errormg);
			}
		}
		return deleteOrganizationUnit;
	}
	
	/**
	 * Enlista las Entidades del LDAP a partir de una base dada.
	 * @param baseDN DN de la base desde donde se enlistan sus elementos.
	 * @return Lista de objetos LdapEntity de los elementos encontrados.
	 * @throws Exception Si no se cumple con los requisitos mínimos.
	 */
	public List<LdapEntity> listElements(String baseDN) throws Exception {
		if (baseDN == null || baseDN.length() == 0) {
			throw gutils.throwException(305, "");
		}
		String errormg = null;
		List<LdapEntity> elements = new ArrayList<LdapEntity>();
		LdapConnection connection = null;
		try {
			connection = LDAPConnectionUtil.openConnection(connectionParams);
			Cursor<SearchResponse> cursor = connection.search(baseDN, "(objectClass=top)",
					SearchScope.ONELEVEL, "*");
			while (cursor.next()) {
				LdapEntity element = new LdapEntity();
				SearchResultEntry response = (SearchResultEntry) cursor.get();
				Iterator<EntryAttribute> itEntry = response.getEntry().iterator();
				element.setDn(response.getEntry().getDn().toString());
				while (itEntry.hasNext()) {
					EntryAttribute attribute = itEntry.next();
					String key = attribute.getId();
					if ("objectClass".equalsIgnoreCase(key)) {
						Iterator<Value<?>> iter = attribute.iterator();
						while (iter.hasNext()) {
							Value<?> classes = iter.next();
							if (classes.toString().length() > 0) {
								switch(classes.toString()) {
									case LdapService.GROUP_CLASS:
										element.setObjectClass(LdapService.GROUP_CLASS);
										break;
									case LdapService.OU_CLASS:
										element.setObjectClass(LdapService.OU_CLASS);
										break;
									case LdapService.USER_CLASS:
										element.setObjectClass(LdapService.USER_CLASS);
										break;
								}
							}
						}
					}
				}
				if (element.getObjectClass() != null) {
					elements.add(element);
				}
			}
			cursor.close();
		} catch (Exception e) {
			errormg = baseDN + e.getMessage();
			//e.printStackTrace();
		} finally {
			LDAPConnectionUtil.closeConnection(connection);
		}
		if (errormg != null) {
			throw gutils.throwException(306, errormg);
		}
		return elements;
	}
	
	protected String getUidWithDomainByDN(String dn) throws Exception {
		if (dn == null || dn.length() == 0) {
			throw gutils.throwException(306, "");
		}
		String user = "";
		
		int index = dn.indexOf(LdapService.USER_ENTRY);
		if (index > -1) {
			String auxDn = dn.substring(0, index - 1);
			index = auxDn.indexOf(",ou="); 
			if (index == -1) {
				user = auxDn.substring(4);
			} else {
				user = auxDn.substring(4, index);
				auxDn = auxDn.substring(index);
				String myDomain = "";
				while(true) {
					String domain = auxDn.substring(4);
					index = domain.indexOf(",ou=");
					if (index > -1) {
						domain = domain.substring(0, index);
						myDomain += domain + '\\';
						auxDn = auxDn.substring(index + 4);
					} else {
						myDomain += domain + '\\';
						break;
					}
				}
				user = myDomain + user;
			}
		} else {
			throw gutils.throwException(306, "");
		}
		return user;
	}

	public static void main(String[] args) {
		LdapService ldap = new LdapService();
		
		try {
			System.out.println(ldap.getUidWithDomainByDN("uid=demo,ou=Users,dc=f2m,dc=com,dc=mx"));
			System.out.println(ldap.getUidWithDomainByDN("uid=user,ou=test,ou=Users,dc=f2m,dc=com,dc=mx"));
			
			
			
			User user1 = ldap.getUserByDomain("test\\user");
			System.out.println(user1.toString());
			
			
			List<User> users = ldap.listAllUsers();
			for (User user:users) {
				System.out.println(user);
			}
			
			/*
			List<Group> grupos = ldap.listAllGroups();
			for (Group grupo:grupos) {
				System.out.println(grupo);
			}
			List<User> users = ldap.listAllUsers();
			for (User user:users) {
				System.out.println(user);
			}
			List<LdapEntity> elements = ldap.listElements(LdapService.BASE_ENTRY);
			for (LdapEntity element:elements) {
				System.out.println("-" + element);
				if (element.getObjectClass().equals(LdapService.OU_CLASS)) {
					System.out.println("\tElementos: " + element.getDn());
					List<LdapEntity> elements1 = ldap.listElements(element.getDn());
					for (LdapEntity element1:elements1) {
						System.out.println(element1);
					}
				}
			}
			*/
			//System.out.println("Eliminando la OU: " + ldap.deleteOrganizationUnit("ou=test,ou=Users,dc=f2m,dc=com,dc=mx"));
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

	}
}

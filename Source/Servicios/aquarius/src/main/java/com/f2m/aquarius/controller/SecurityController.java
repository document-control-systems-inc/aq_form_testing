package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.service.SecurityService;
import com.f2m.aquarius.service.UserService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SecurityController {
	
	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private SecurityService securityService = new SecurityService();
	private UserService userService = new UserService();
	
	@RequestMapping(value = "/security/profile/page", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setPageProfile(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "name") String profileName,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "elements") String elements) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(securityService.setPageProfile(profileName, id, elements, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/security/profile/page", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getPageProfile(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(securityService.getPageProfileById(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/security/page", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setPage(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "name") String pageName,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(securityService.setPage(pageName, id, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/security/page", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getPageById(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(securityService.getPage(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/security/component", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setComponentByIdPage(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "idPage") String idPage,
			@RequestHeader(required = false, value = "name") String componentName,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(securityService.setPageComponent(idPage, componentName, id, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/security/component", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getCommentByIdObject(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "idComponent") String idComponent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(securityService.getPageComponentById(id, idComponent));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}

	@RequestMapping(value = "/security/profile/user", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setPageProfileToUser(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idProfile,
			@RequestHeader(required = false, value = "uid") String uid) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(userService.addSecurityProfile(idProfile, uid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
}

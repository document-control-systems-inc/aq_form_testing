package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.service.ScanService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ScanController {
	
	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private ScanService scanService = new ScanService();
	
	@RequestMapping(value = "/scan/profile", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getScanProfile(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "perfil") String perfil) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(scanService.getScanProfile(userUid, id, perfil));
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

	@RequestMapping(value = "/scan/profile", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setScanProfile(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "profile") String profile) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(scanService.setScanProfile(userUid, profile));
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
	
	@RequestMapping(value = "/scan/profile", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteScanProfile(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "perfil") String perfil) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(scanService.deleteScanProfile(userUid, id, perfil));
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

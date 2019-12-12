package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.service.SocialService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SocialController {

	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private SocialService socialService = new SocialService();
	
	@RequestMapping(value = "/social/comment", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getCommentByIdObject(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(socialService.getCommentById(id, userUid));
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
	
	@RequestMapping(value = "/social/comment", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setCommentbyIdObject(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "comment") String comment) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(socialService.setCommentByIdObject(id, userUid, comment));
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

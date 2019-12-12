package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.BatchService;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class BatchController {
	
	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private BatchService batchService = new BatchService();

	@RequestMapping(value = "/batch/ocr/time", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setBatchTime(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "time") long time) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				response.setExito(batchService.setOCRTime(time, userUid));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/batch/ocr/execute", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setBatchExecute(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "execute") boolean execute) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				response.setExito(batchService.setOCRExecution(execute, userUid));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
}

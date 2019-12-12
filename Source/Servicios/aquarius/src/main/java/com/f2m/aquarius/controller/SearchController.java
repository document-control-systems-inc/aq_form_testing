package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.service.SearchService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SearchController {

	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private SearchService search = new SearchService();
	
	@RequestMapping(value = "/searchcriteria", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getSearchCriteria(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(search.getSearchCriteria(""));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/searchcriteria/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getSearchCriteriaById(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(search.getSearchCriteria(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/searchtype", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getSearchType(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(search.getSearchType(""));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/searchtype/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getSearchTypeById(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(search.getSearchType(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/search/stored", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setStoredSearch(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "search") String storedSearch) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) { 
					response.setExito(search.setStoredSearch(storedSearch, userUid)); 
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
	
	@RequestMapping(value = "/search/stored", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getStoredSearch(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(search.getStoredSearch(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse executeSearch(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "search") String executeSearch) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				response.setExito(search.executeSearch(executeSearch, userUid));
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

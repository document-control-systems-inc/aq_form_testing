package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.service.ErrorService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ErrorController {

	private GeneralUtils gutils = new GeneralUtils();
	private ErrorService errorService = new ErrorService();;
	
	@RequestMapping(value = "/errors", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getErrors(@RequestHeader(required = false, value = "locale") String locale) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			String tmpLocale = errorService.validateLocale(locale);
			response.setExito(errorService.getErrors(tmpLocale));
			response.setStatus(0);
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/error", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getErrorById(@RequestHeader(required = false, value = "id") int id, 
			@RequestHeader(required = false, value = "locale") String locale) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			String tmpLocale = errorService.validateLocale(locale);
			response.setExito(errorService.getErrorById(id, tmpLocale));
			response.setStatus(0);
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	public static void main(String[] args) {
		ErrorController error = new ErrorController();
		error.getErrors("fr");
	}
}

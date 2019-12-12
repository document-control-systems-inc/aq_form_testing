package com.f2m.aquarius.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.f2m.aquarius.parameters.LDAPConnectionParams;
import com.f2m.aquarius.parameters.MailConnectionParams;
import com.f2m.aquarius.parameters.SessionParams;
import com.f2m.aquarius.service.ConfigurationService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@Configuration
public class AppConf {
	
	private ConfigurationService conf = new ConfigurationService();
	private GeneralUtils gutils = new GeneralUtils();

	@Bean
	public LDAPConnectionParams getLdapConf() throws AquariusException {
		LDAPConnectionParams params =  conf.getLdap();
		if (params == null) {
			throw gutils.throwException(802, "");
		}
		return params;
	}
	
	@Bean
	public SessionParams getSessionConf() throws AquariusException {
		SessionParams params = conf.getSession();
		if (params == null) {
			throw gutils.throwException(802, "");
		}
		return params;
	}
	
	@Bean
	public MailConnectionParams getMailConf() throws AquariusException {
		MailConnectionParams params = conf.getMail();
		if (params == null) {
			throw gutils.throwException(802, "");
		}
		return params;
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver(){
	    CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
	    commonsMultipartResolver.setDefaultEncoding("utf-8");
	    commonsMultipartResolver.setMaxUploadSize(50000000);
	    return commonsMultipartResolver;
	}
	
}

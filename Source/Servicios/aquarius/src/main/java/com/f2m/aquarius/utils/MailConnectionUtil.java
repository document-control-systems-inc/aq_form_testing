package com.f2m.aquarius.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.f2m.aquarius.parameters.MailConnectionParams;

public class MailConnectionUtil {
	
	private static Session connection = null;

	public static Session getConnection(MailConnectionParams params) throws Exception {
		if (connection == null) {
			GeneralUtils gutils = new GeneralUtils();
			try {
				Properties props = new Properties();
				if (params.getAuth() != null) {
					props.put("mail.smtp.auth", params.getAuth().toString());
				}
				if (params.getTtl() != null) {
					props.put("mail.smtp.starttls.enable", params.getTtl().toString());
				}
				props.put("mail.smtp.host", params.getHost());
				props.put("mail.smtp.port", params.getPort());

				connection = Session.getInstance(props,
				  getAuthenticator(params.getUser(), params.getPassword()));
			} catch (Exception e) {
				throw gutils.throwException(601, e.getMessage());
			}
		}
		return connection;
	}
	
	private static Authenticator getAuthenticator(final String user, final String password) {
		return new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		  };
	}
}

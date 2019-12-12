package com.f2m.aquarius.service;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.f2m.aquarius.configuration.AppConf;
import com.f2m.aquarius.parameters.MailConnectionParams;
import com.f2m.aquarius.utils.GeneralUtils;
import com.f2m.aquarius.utils.MailConnectionUtil;

public class MailService {
	
	private MailConnectionParams connectionParams;
	private GeneralUtils gutils = new GeneralUtils();

	public MailService() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(AppConf.class);
		ctx.refresh();
		connectionParams = ctx.getBean(MailConnectionParams.class);
		ctx.close();
		
	}
	
	public Boolean sendMail(String to, String subject, String text) throws Exception {
		Boolean response = false;
		try {
			Message message = new MimeMessage(MailConnectionUtil.getConnection(connectionParams));
			message.setFrom(new InternetAddress(connectionParams.getSender()));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);

			Multipart multipart = new MimeMultipart();
			
			// Atachment:
			/*
			String fileName = "Folleto ADN NOV. 2017 baja (2).pdf";
			String filename_path = "C:/Users/gomado/Documents/F2M/Edge/Adn/info/";
			
			File file = new File(filename_path + fileName);
			if (file.exists()) {
				System.out.println("Archivo existe");
			}
			DataSource source = new FileDataSource(file.getAbsolutePath());
			BodyPart attachmentBodyPart = new MimeBodyPart();
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			attachmentBodyPart.setFileName(file.getName());
			multipart.addBodyPart(attachmentBodyPart);
			*/
			
			//Body
			BodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setContent(text , "text/html");
			multipart.addBodyPart(htmlBodyPart);
			message.setContent(multipart); 
		
			Transport.send(message);
			response = true;
		} catch (MessagingException e) {
			throw gutils.throwException(602, e.getMessage());
		}
		return response;
	}
	
	public static void main (String[] args) {
		
		try {
			MailService mail = new MailService();
			mail.sendMail("gomado@gmail.com", "Prueba de Correo Electrónico", "Esta es una prueba de correo electrónico");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

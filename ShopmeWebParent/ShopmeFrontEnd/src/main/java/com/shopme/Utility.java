package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.setting.EmailSettingBag;

public class Utility {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);
	
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		LOGGER.info("CustomerRegisterUtil | getSiteURL | siteURL.replace(request.getServletPath(), \"\") :" 
				+ siteURL.replace(request.getServletPath(), ""));
				
		return siteURL.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(settings.getHost());
		mailSender.setPort(settings.getPort());
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		mailSender.setDefaultEncoding("utf-8");
		
		Properties properties = new Properties();
		properties.setProperty("mail.smtps.auth", settings.getSmtpAuth());
		properties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
		
		mailSender.setJavaMailProperties(properties);
		LOGGER.info("CustomerRegisterUtil | prepareMailSender | mailSender : " + mailSender.toString());

		return mailSender;
	}
}

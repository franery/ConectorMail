package org.mule.extension.internal;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class PasswordAuthenticator extends Authenticator {
	
	private String username;
	private String password;
	
	PasswordAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}

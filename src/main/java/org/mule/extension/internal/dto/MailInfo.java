package org.mule.extension.internal.dto;

public class MailInfo {

	private String id;
	
	private String from;
	
	private String subject;
	
	private String payload;

	MailInfo() {
		
	}

	public MailInfo(String id, String from, String subject) {
		super();
		this.id = id;
		this.from = from;
		this.subject = subject;
	}
	
	public MailInfo(String id, String from, String subject, String payload) {
		super();
		this.id = id;
		this.from = from;
		this.subject = subject;
		this.payload = payload;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	
	
}

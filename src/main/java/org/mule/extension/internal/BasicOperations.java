package org.mule.extension.internal;


import static javax.mail.Folder.READ_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMultipart;

import org.mule.extension.internal.dto.MailInfo;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;

/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class BasicOperations {

	Folder folder;

  /**
   * Example of an operation that uses the configuration and a connection instance to perform some action.
   */
  @MediaType(MediaType.TEXT_PLAIN)
  public String test(@DisplayName("Name") String metricName) {
    return "Hola";
  }
  
  @MediaType(MediaType.TEXT_PLAIN)
  public String testBis(@DisplayName("MetricName") String metricName) {
    return "Hola Mundo";
  }
  
	@Summary("Lists the emails in the given GMAIL Mailbox Folder")
	@DisplayName("List - Gmail")
	public List<MailInfo> listImap(@Connection BasicConnection connection,
			@Optional(defaultValue = "INBOX") String mailboxFolder,
			@Optional(defaultValue = "10") int pageSize,
			@Optional(defaultValue = "0") int page,
			StreamingHelper streamingHelper) throws Exception {
		folder = connection.getFolder(mailboxFolder, READ_ONLY);
		Message[] messages = folder.getMessages();
		List<MailInfo> subjects = new ArrayList<>();
		for(Integer i = messages.length - (page * pageSize) - 1;i>messages.length - (page+1)*pageSize - 1 && i >= 0; i--) {
			subjects.add(new MailInfo(i.toString(), messages[i].getFrom()[0].toString(), messages[i].getSubject()));
		}
		return subjects;
	}
	
	@Summary("Returns a specific mail")
	@DisplayName("Specific mail")
	public MailInfo mail(@Connection BasicConnection connection,
			@Optional(defaultValue="INBOX") String folderName,
			Integer index) throws Exception {
		folder = connection.getFolder(folderName, READ_ONLY);
		Message mail = folder.getMessages()[index];
		return new MailInfo(index.toString(),mail.getFrom()[0].toString(),mail.getSubject(), getTextFromMessage(mail));
	}
	
	private String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	    return result;
	}

	private String getTextFromMimeMultipart(
	        MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}
	
	
	@Summary("Send a mail")
	@DisplayName("Send")
	public void send(@Connection BasicConnection connection, String payload) throws AddressException, MessagingException {
		connection.send(payload);
	}

}

package org.mule.extension.internal;

import static java.lang.String.format;
import static org.mule.runtime.api.connection.ConnectionValidationResult.failure;
import static org.mule.runtime.api.connection.ConnectionValidationResult.success;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.tls.TlsContextFactory;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class BasicConnection {

	private static final String IMAP = "imaps";
	
	private Store store;
	private Folder folder;
	protected final Session session;

	/**
	 * Creates a new instance of the of the {@link MailboxConnection} secured by TLS.
	 *
	 * @param username          the username to establish the connection.
	 * @param password          the password corresponding to the {@code username}.
	 * @param host              the host name of the mail server
	 * @param port              the port number of the mail server.
	 * @param tlsContextFactory the tls context factory for creating the context to secure the connection
	 */
	public BasicConnection(String username, String password, String host, String port,
			TlsContextFactory tlsContextFactory) {
		session = Session.getDefaultInstance(properties(host,port),
				new PasswordAuthenticator(username, password));
		try {
			this.store = session.getStore(IMAP);
			this.store.connect(host,username,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Properties properties(String host, String port) {
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", host);
		p.setProperty("mail.smtp.socketFactory.port", port);
		p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.smtp.port", port);
		return p;
	}

	public ConnectionValidationResult validate() {
		String errorMessage = "Store is not connected";
		return store.isConnected() ? success() : failure(errorMessage, new Exception(errorMessage));
	}

	/**
	 * Opens and return the email {@link Folder} of name {@code mailBoxFolder}. The folder can contain Messages, other Folders or
	 * both.
	 * <p>
	 * If there was an already opened folder and a different one is requested the opened folder will be closed and the new one will
	 * be opened.
	 *
	 * @param mailBoxFolder the name of the folder to be opened.
	 * @param openMode      open the folder in READ_ONLY or READ_WRITE mode
	 * @return the opened {@link Folder}
	 * @throws Exception 
	 */
	public synchronized Folder getFolder(String mailBoxFolder, int openMode) throws Exception {
		try {
			if (folder != null) {
				if (folder.isOpen() && folder.getMode() == openMode && folder.getFullName().equals(mailBoxFolder)) {
					return folder;
				}
			}
			System.out.println("FOLDERS: ");
			for(Folder folder : store.getDefaultFolder().list("*")) {
				System.out.println(folder.getName() +" :: "+folder.getFullName());
			}
			folder = store.getFolder(mailBoxFolder);
			folder.open(openMode);
			return folder;
		} catch (MessagingException e) {
			throw new Exception(format("Error while opening folder %s", mailBoxFolder), e);
		}
	}

	public void send(String payload) throws AddressException, MessagingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("from@gmail.com"));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("francisco.nery@softvision.com"));
		message.setSubject("Prueba");
		
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(payload, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}

}

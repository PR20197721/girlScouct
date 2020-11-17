package org.girlscouts.common.osgi.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.apache.commons.mail.EmailException;
import org.girlscouts.common.components.GSEmailAttachment;

public interface GSEmailService {

	public void sendEmail(String subject, List<String> toAddresses, String body)
			throws AddressException, EmailException, MessagingException;
	public void sendEmail(String subject, List<String> toAddresses, String body, String fromAddress)
			throws AddressException, EmailException, MessagingException;
    public void sendEmail(String subject, List<String> toAddresses, List<String> bccAddresses, String body, String fromAddress)
            throws AddressException, EmailException, MessagingException;
	public void sendEmail(String subject, List<String> toAddresses, String body, Set<GSEmailAttachment> attachments)
			throws EmailException, MessagingException, IOException;
	public void sendEmail(String subject, List<String> toAddresses, String body, Set<GSEmailAttachment> attachments, String fromAddress)
			throws EmailException, MessagingException, IOException;

}

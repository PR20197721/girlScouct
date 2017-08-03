package org.girlscouts.web.service.email;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.apache.commons.mail.EmailException;
import org.girlscouts.web.components.GSEmailAttachment;

public interface GSEmailService {

	public void sendEmail(String subject, List<String> toAddresses, String body)
			throws AddressException, EmailException;

	public void sendEmail(String subject, List<String> toAddresses, String body, Set<GSEmailAttachment> attachments)
			throws EmailException, MessagingException, IOException;

}

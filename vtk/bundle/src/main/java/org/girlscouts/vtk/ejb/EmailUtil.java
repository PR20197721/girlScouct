package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.salesforce.Email;
import org.girlscouts.vtk.salesforce.tester3;

@Component
@Service(EmailUtil.class)
public class EmailUtil {

	public void sendMeetingReminder(Troop user, EmailMeetingReminder emr) {

		org.girlscouts.vtk.auth.models.ApiConfig apiConfig = null;
		java.util.List<String> emailTo = new java.util.ArrayList();
		java.util.List to = new java.util.ArrayList();
		if (emr.getEmailToSelf() != null)
			emailTo.add(emr.getFrom());

		String _to = "";
		for (int i = 0; i < emailTo.size(); i++)
			_to += emailTo.get(i);
		emr.setTo(_to);

		// TODO send email via salesforce
		Email email = new Email();
		email.setFrom(emr.getFrom());
		email.setTo(emr.getTo());
		email.setSubject(emr.getSubj());
		email.setTxtEmail(emr.getHtml());
		email.setParentId("500Z0000007Szyg");
		String confId = new tester3().sendEmail(apiConfig, email);
	}

}

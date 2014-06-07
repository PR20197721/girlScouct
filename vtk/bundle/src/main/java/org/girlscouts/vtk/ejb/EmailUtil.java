package org.girlscouts.vtk.ejb;

import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.models.user.User;

import org.girlscouts.vtk.salesforce.Campaign;
import org.girlscouts.vtk.salesforce.Contact;
import org.girlscouts.vtk.salesforce.Email;
import org.girlscouts.vtk.salesforce.tester3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



@Component
@Service(EmailUtil.class)
public class EmailUtil {

	
	
	public void sendMeetingReminder(User user, EmailMeetingReminder emr){
		
		org.girlscouts.vtk.auth.models.ApiConfig apiConfig= user.getApiConfig();
		
		java.util.List <String> emailTo = new java.util.ArrayList();
		
		//java.util.List<String> getCampaignContactIds(ApiConfig apiConfig, Campaign campaign)
		
		//all emails to send this reminder
		java.util.List to= new java.util.ArrayList();
		
		String campaignId = "701Z0000000WZA7";
		
		if( emr.getEmailToGirlParent()!=null ){
			//TODO get girl/parent emails
			java.util.List <Contact>  contacts = new tester3().getGirstEmails(apiConfig, campaignId, "XXX");
			for(int i=0;i<contacts.size();i++)
				emailTo.add( contacts.get(i).getEmail());
				
		}
		
		if( emr.getEmailToTroopVolunteer()!=null ){
			
			java.util.List <Contact>  contacts = new tester3().getGirstEmails(apiConfig, campaignId, "Volunteer");
			for(int i=0;i<contacts.size();i++)
				emailTo.add( contacts.get(i).getEmail());
		}
		
		
		
		/*
		System.err.println(apiConfig.getAccessToken());
		
		System.err.println("From: "+ emr.getFrom());
		System.err.println("Subj: "+ emr.getSubj());
		System.err.println("To: "+ emr.getTo());
		System.err.println("CC: "+ emr.getCc());
		System.err.println("Html : "+ emr.getHtml());
		
		System.err.println("Sending emails via salesforce.... " );
		
		//new SalesforceDAO().getUserInfo(apiConfig);
		//new SalesforceDAO().getContactEmailList(apiConfig);
		*/

		
		//TODO send email via salesforce
		Email email = new Email();
		email.setFrom(emr.getFrom());
		email.setTo(emailTo.toString());
		email.setSubject(emr.getSubj());
		email.setTxtEmail(emr.getHtml());
		email.setParentId("500Z0000007Szyg");
		String confId =new tester3().sendEmail(apiConfig, email);
		
	}
	
	
	


		
}

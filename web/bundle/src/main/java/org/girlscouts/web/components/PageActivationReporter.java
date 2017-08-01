package org.girlscouts.web.components;

import java.util.ArrayList;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

public class PageActivationReporter {

	private static Logger log = LoggerFactory.getLogger(PageActivationReporter.class);

	private Node dateRolloutNode;
	private Session session;
	private ArrayList<Node> reportNodes;
	private Node currentReportNode;
	private int reportIndex;
	private int reportNodeIndex;
	ArrayList<String> statusList;
	private MessageGatewayService messageGatewayService;

	public PageActivationReporter(Node dateRolloutNode, Session session, MessageGatewayService messageGatewayService) {
		this.dateRolloutNode = dateRolloutNode;
		this.session = session;
		this.reportIndex = 0;
		this.reportNodes = new ArrayList<Node>();
		this.currentReportNode = null;
		this.reportNodeIndex = 0;
		this.statusList = new ArrayList<String>();
		this.messageGatewayService = messageGatewayService;
	}

	public void report(String status) {
		statusList.add(status);
		try {
			if (currentReportNode == null || reportIndex >= 50) {
				currentReportNode = dateRolloutNode.addNode("report" + reportNodeIndex, "nt:unstructured");
				reportNodes.add(currentReportNode);
				reportNodeIndex++;
				reportIndex = 0;
			}
			currentReportNode.setProperty("status" + reportIndex, status);
			session.save();
			reportIndex++;
		} catch (Exception e) {
			log.error("GS Page Activator - Failed to create or retrieve report node. Status is " + status);
			e.printStackTrace();
			return;
		}
	}

	public void sendReportEmail(long startTime, long endTime, Boolean delay, Boolean crawl, TreeSet<String> builtPages,
			Node dateNode, Session session) throws Exception {
		ArrayList<String> emails = new ArrayList<String>();
		this.report("Retrieving email addresses for report");
		if (dateNode.hasProperty("emails")) {
			Value[] values = dateNode.getProperty("emails").getValues();
			for (Value v : values) {
				emails.add(v.toString());
			}
		} else {
			this.report("No email address property found. Can't send any emails");
			return;
		}
		if (emails.size() < 1) {
			this.report("No email addresses found in email address property. Can't send any emails.");
			return;
		}
		if (builtPages.size() > 0) {
			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			for (String s : emails) {
				emailRecipients.add(new InternetAddress(s));
			}
			email.setTo(emailRecipients);
			email.setSubject("Girl Scouts Activation Process Report");
			String html = "<p>The Girl Scouts Activation Process has just finished running.</p>";
			if (delay) {
				html = html + "<p>It was of type - Scheduled Activation</p>";
			} else {
				if (crawl) {
					html = html + "<p>It was of type - Immediate Activation with Crawl</p>";
				} else {
					html = html + "<p>It was of type - Immediate Activation without Crawl</p>";
				}
			}
			html = html + "<p>Pages Activated:</p><ul>";
			for (String page : builtPages) {
				html = html + "<li>" + page + "</li>";
			}
			html = html + "</ul>";
			long timeDiff = (endTime - startTime) / 60000;
			html = html + "<p>The entire process took approximately " + timeDiff + " minutes to complete</p>";

			html = html + "<p>The following is the process log for the activation process so far:</p><ul>";
			for (String s : statusList) {
				html = html + "<li>" + s + "</li>";
			}
			html = html + "</ul>";
			email.setHtmlMsg(html);
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			messageGateway.send(email);
		}
	}
}

package org.girlscouts.web.components;

import java.util.ArrayList;
import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageActivationReporter {

	private static Logger log = LoggerFactory.getLogger(PageActivationReporter.class);

	private Node dateRolloutNode;
	private Session session;
	private ArrayList<Node> reportNodes;
	private Node currentReportNode;
	private int reportIndex;
	private int reportNodeIndex;
	ArrayList<String> statusList;

	public PageActivationReporter(Node dateRolloutNode, Session session) {
		this.dateRolloutNode = dateRolloutNode;
		this.session = session;
		this.reportIndex = 1;
		this.reportNodes = new ArrayList<Node>();
		this.currentReportNode = null;
		this.reportNodeIndex = 1;
		this.statusList = new ArrayList<String>();
	}

	public void report(String status) {
		statusList.add(status);
		try {
			if (currentReportNode == null || reportIndex > 50) {
				currentReportNode = dateRolloutNode.addNode("report" + reportNodeIndex,
						"nt:unstructured");
				reportNodes.add(currentReportNode);
				reportNodeIndex++;
				reportIndex = 1;
			}
			currentReportNode.setProperty("status" + formatedIndex(reportIndex), status);
			session.save();
			reportIndex++;
		} catch (Exception e) {
			log.error("GS Page Activator - Failed to create or retrieve report node. Status is " + status);
			e.printStackTrace();
			return;
		}
	}

	public String formatedIndex(int num) {
		if (num < 10) {
			return "0" + String.valueOf(num);
		}
		return String.valueOf(num);
	}

	public ArrayList<String> getStatusList() {
		return statusList;
	}
}

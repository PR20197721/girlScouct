package org.girlscouts.web.constants;

import java.util.regex.Pattern;

public interface PageActivationConstants {

	public static final String STATUS_CREATED = "created";
	public static final String STATUS_AGGREGATED = "aggregated";
	public static final String STATUS_QUEUED = "queued";
	public static final String STATUS_PROCESSING = "processing";
	public static final String STATUS_DELAYED = "delayed";
	public static final String STATUS_COMPLETE = "complete";
	public static final String STATUS_FAILED = "failed";

	public static final String DATE_NODE_FMT = "yyyy-MM-dd'T'HHmmss";

	public static final String ETC_NODE = "/etc";
	public static final String PAGE_ACTIVATIONS_NODE = "gs-activations";
	public static final String PAGE_ACTIVATIONS_PATH = ETC_NODE + "/" + PAGE_ACTIVATIONS_NODE;
	public static final String EVENT_ACTIVATIONS_NODE = "gs-delayed-activations";
	public static final String EVENT_ACTIVATIONS_PATH = ETC_NODE + "/" + EVENT_ACTIVATIONS_NODE;
	public static final String GS_REPORTEMAIL_PATH = "/etc/msm/rolloutreports";
	public static final String DELAYED_NODE = "delayed";
	public static final String INSTANT_NODE = "instant";
	public static final String COMPLETED_NODE = "completed";

	public static final String PARAM_COUNCILS = "councils";
	public static final String PARAM_DELAY = "delayActivation";
	public static final String PARAM_CRAWL = "crawl";
	public static final String PARAM_BREAK_INHERITANCE = "breakInheritance";
	public static final String PARAM_IS_CANCELLED_FOR_CHILDREN = "cq:isCancelledForChildren";
	public static final String PARAM_USE_TEMPLATE = "useTemplate";
	public static final String PARAM_TEMPLATE_PATH = "template";
	public static final String PARAM_NOTIFY = "notify";
	public static final String PARAM_COUNCIL_NOTIFICATIONS_SENT = "council_notifications_sent";
	public static final String PARAM_GSUSA_NOTIFICATIONS_SENT = "gsusa_notifications_sent";
	public static final String PARAM_EMAIL_SUBJECT = "subject";
	public static final String PARAM_EMAIL_MESSAGE = "message";
	public static final String PARAM_ACTIVATE = "activate";
	public static final String PARAM_STATUS = "status";
	public static final String PARAM_SOURCE_PATH = "srcpath";
	public static final String PARAM_TEST_MODE = "testMode";
	public static final String PARAM_PAGES = "pages";
	public static final String PARAM_ACTIVATED_PAGES = "activatedPages";
	public static final String PARAM_UNMAPPED_PAGES = "unmappedPages";
	public static final String PARAM_REPORT_EMAILS = "emails";
	public static final String PARAM_DISPATCHER_IPS = "ips";
	public static final String PARAM_GROUP_SIZE = "groupsize";
	public static final String PARAM_MINUTES = "minutes";
	public static final String PARAM_CRAWL_DEPTH = "crawldepth";

	public static final int DEFAULT_PARAM_GROUP_SIZE = 1;
	public static final int DEFAULT_PARAM_MINUTES = 5;
	public static final int DEFAULT_PARAM_CRAWL_DEPTH = -1;
	public static final int DEFAULT_CRAWL_WAIT_TIME = 5_000;
	public static final int DEFAULT_REMORSE_WAIT_TIME = 0;

	public static final String BRANCH_PATTERN_STR = "^(/content/[^/]+)/?";
	public static final Pattern BRANCH_PATTERN = Pattern.compile(BRANCH_PATTERN_STR);

	public interface Email {
		public static final String DEFAULT_REPORT_SUBJECT = "GSUSA Rollout (Production) Report";
		public static final String DEFAULT_REPORT_ATTACHMENT = "gs_rollout_notifications_log";
		public static final String DEFAULT_REPORT_GREETING = "Dear Girl Scouts USA User,";
		public static final String DEFAULT_REPORT_INTRO = "The following is a report for the GSUSA Rollout Workflow (Production).";
		public static final String DEFAULT_REPORT_HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>"
				+ DEFAULT_REPORT_SUBJECT + "</title></head>";
		public static final String DEFAULT_NOTIFICATION_SUBJECT = "GSUSA Rollout Notification";
		public static final String DEFAULT_NOTIFICATION_HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "<title>Girl Scouts</title></head>";
		public static final String DEFAULT_NOTIFICATION_MESSAGE = "<p>Dear Council, </p>"
				+ "<p>It has been detected that one or more component(s) on the following page(s) has been modified by GSUSA. Please review and make any updates to content or simply reinstate the inheritance(s). If you choose to reinstate the inheritance(s) please be aware that you will be <b>discarding</b> your own changes (custom content) that have been made to this page and will <b>immediately</b> receive the new national content.</p>"
				+ "<p><b>National page URL:</b> <%template-page%></p>" + "<p><b>Your page URL:</b> <%council-page%></p>"
				+ "<p>Click <a href='<%council-author-page%>'>here</a> to edit your page.</p>"
				+ "<p>Please note that any changes made as part of this rollout will not reflect on you live site until after midnight (this includes any page updates which you may see live in author). We have added the feature that delays activation of any updates or new pages to midnight in order to avoid outages.</p>";

		public static final String DEFAULT_COMPLETION_REPORT_SUBJECT = "Girl Scouts Activation Process Report";
		public static final String DEFAULT_COMPLETION_REPORT_ATTACHMENT = "gs_activation_process_log";
		public static final String DEFAULT_COMPLETION_REPORT_HEAD = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>"
				+ DEFAULT_COMPLETION_REPORT_SUBJECT + "</title></head>";
	}
}
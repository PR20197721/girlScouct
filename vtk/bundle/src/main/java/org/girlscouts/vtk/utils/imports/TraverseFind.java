package org.girlscouts.vtk.utils.imports;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Meeting;

public class TraverseFind {

	private String err = "";
	private String activityLog = null;

	public String fmtStr(String txt) {

		txt = txt.replace("[[_", "");

		if (txt.contains("[[Activity")) {
			txt = fmtActivity(txt).replace("[Activity", "Activity");
		}

		if (txt.contains("<p>"))
			txt = txt.substring(txt.indexOf("<p>"));
		if (txt.endsWith("</p>"))
			txt.substring(0, txt.lastIndexOf("</p>"));
		return txt;
	}

	public java.util.Map getMeetingInfo(String fileLoc) {
		java.util.Map container = null;
		TraverseFind me = new TraverseFind();
		try {
			container = me.doParse(fileLoc);
		} catch (Exception e) {
			me.err += " Found problem parsing file";
			e.printStackTrace();
		}

		return container;
	}

	public static void main(String[] args) {

		TraverseFind me = new TraverseFind();
		try {

			// me.test();if(true) return;

			java.util.Map container = null;
			try {
				container = me
						.doParse("/Users/akobovich/Desktop/fileTest3.docx");
			} catch (Exception e) {
				me.err += " Found problem parsing file";
				e.printStackTrace();
			}

			String meetingId = null;
			try {
				meetingId = me.getRetrValue(container, "meeting id");
			} catch (Exception e) {
				me.err += "Failed to retrieve field 'meeting id'";
				e.printStackTrace();
				return;
			}

			Meeting meeting = null;
			try {
				meeting = me.getMeeting(container);
			} catch (Exception e) {
				me.err += " Found problem while processing Meeting/Activities ("
						+ me.getActivityLog() + " " + e.getMessage() + ")";
				e.printStackTrace();
			}
			if (meeting == null) {
				me.err += " Meeting/Activities not processed. ("
						+ me.getActivityLog() + ")";
			} else {
				meeting.setId(meetingId);
				meeting.setName(me.getRetrValue(container,
						"meeting short description"));
				meeting.setBlurb((String) container.get("overview"));
				// TODO: Mike Z. What's going on here
				// meeting.setMeetingInfo(getMeetingInfo);

			}

			// print title, msg
			me.printContainer(container);

			// write to flat file
			me.writeToFile(container);

		} catch (Exception e) {
			me.err += " File not processed " + e.getMessage();
			e.printStackTrace();
		}

		me.err = null;
		me.activityLog = null;
	}

	public void writeToFile(java.util.Map container) throws Exception {

		StringBuffer strBuf = new StringBuffer();
		java.util.Iterator _itr = container.keySet().iterator();
		while (_itr.hasNext()) {
			String title = (String) _itr.next();
			// container.get(title)) ;
			String txt = (String) container.get(title);
			txt = txt.replace("[[_", "");
			txt = txt.replace(title + "]]", "");

			if (txt.contains("[[Activity")) {
				txt = fmtActivity(txt).replace("[Activity", "Activity");
			}

			strBuf.append("<h1>" + title + "</h1>" + txt);
		}
		writeToFile(strBuf.toString());

	}

	public void printContainer(java.util.Map container) {

		// ************ PRINT
		java.util.Iterator _itr = container.keySet().iterator();
		while (_itr.hasNext()) {
			String title = (String) _itr.next();
			// container.get(title)) ;
		}
	}

	public void log() {
		System.err.println("isErr: " + isErr() + " err: " + getErr());
	}

	public String getErr() {
		return this.err;
	}

	public boolean isErr() {
		if (err == null || err.trim().equals(""))
			return false;
		else
			return true;
	}

	public String getActivityLog() {
		return activityLog;
	}

	public void setActivityLog(String activityLog) {
		this.activityLog = activityLog;
	}

	public Meeting getMeeting(java.util.Map _container) {

		Meeting meeting = null;
		java.util.Iterator _itr = _container.keySet().iterator();
		while (_itr.hasNext()) {
			String title = (String) _itr.next();
			// MEETING populate
			if (title.trim().toLowerCase().replaceAll("<.*?>", "")
					.equals("detailed activity plan"))
				meeting = new TraverseFind().processMeeting((String) _container
						.get(title));
		}

		return meeting;
	}

	public void print(java.util.Map _container) {

		// ************ PRINT
		java.util.Iterator _itr = _container.keySet().iterator();
		while (_itr.hasNext()) {
			String title = (String) _itr.next();
			new TraverseFind().breakup1((String) _container.get(title),
					"\\[(.*?)\\]]");

			// *********Write to html file
			// -writeToFile( "<h1>"+title+"</h1>" + _container.get(title) );

			// MEETING populate
			Meeting meeting = null;
			if (title.trim().toLowerCase().equals("detailed activity plan"))
				meeting = new TraverseFind().processMeeting((String) _container
						.get(title));
		}
	}

	public java.util.Map doParse(String fileLoc) {
		InputStream fs = null;
		// XWPFWordExtractor extractor = null ;
		java.util.Map _container = null;

		try {

			// TODO switch between http/desktop
			try {
				fs = new java.net.URL("http://localhost:4503" + fileLoc)
						.openConnection().getInputStream();
			} catch (Exception e) {/*
									 * System.err.println(
									 * "File not foundon http 4503 trying local: "
									 * +fileLoc);
									 */
			}
			if (fs == null)
				try {
					fs = new FileInputStream(fileLoc);
				} catch (Exception e) {/*
										 * System.err.println(
										 * "File not foundon http 4503 trying local: "
										 * +fileLoc);
										 */
				}

			if (fs == null) {
				System.err.println("File not found: " + fileLoc);
				return null;
			}

			XWPFDocument hdoc = new XWPFDocument(OPCPackage.open(fs));

			java.util.List<XWPFParagraph> parags = hdoc.getParagraphs();

			String txt = "";
			int level = -1;

			// System.err.println("Par: "+ (parags==null ) );
			// System.err.println(parags.size());
			for (int p = 0; p < parags.size(); p++) {

				String _txt = "";
				XWPFParagraph Par = parags.get(p);
				/*
				 * 9/4/14 if( Par.getParagraphText() ==null ||
				 * Par.getParagraphText().trim().equals("") ) continue;
				 */
				boolean isLi = false, isP = true;
				if (Par.getNumID() != null
						|| (Par.getStyleID() != null && Par.getStyleID()
								.equals("C-Toolkitcopywbullets")))
					isLi = true;

				if (Par.getStyleID() != null
						&& Par.getStyleID().equals("NoSpacing"))
					isP = true;
				else if (Par.getStyleID() != null
						&& Par.getStyleID().equals("null"))
					isP = false;

				if (isLi || Par.getNumID() != null) {
					// _txt += "<p><li"+ (Par.getIndentationLeft() > 0 ?
					// " style=\"padding-left:50px;\"" : "") +">";
					_txt += /* "<p> */"<li style=' padding-left:"
							+ Par.getNumID() + "px;'>";

				} else if (isP) {
					_txt += "<p>";
				}

				/*
				 * if( Par.getIndentationLeft()>0){ _txt +=
				 * "<span style=\"padding-left:10px;\"></span>";
				 * 
				 * //System.err.println(_txt +" : "+ Par.getParagraphText()); }
				 */

				java.util.List<XWPFRun> runs = Par.getRuns();
				// System.err.println("&&& "+ runs.size( ));

				for (int y = 0; y < runs.size(); y++) {
					XWPFRun run = runs.get(y);

					String str = run.toString().toString();
					if (str == null)
						continue;
					// System.err.println("\n\n\n****** "+ str);
					// if( Par.getRuns().size() !=0 &&
					// str.trim().toLowerCase().equals("steps"))
					// str="[Steps]]";

					if (run.isBold())
						_txt += "<b>" + str + "</b>";
					else if (run.isItalic())
						_txt += "<i>" + str + "</i>";
					else if (run.isStrike())
						_txt += "<del>" + str + "</del>";
					else
						_txt += str;
					// System.err.println( "..... "+txt );
				}// edn of

				if (isLi || Par.getNumID() != null) {
					_txt += "</li>";// </p>";
				} else if (isP) {
					_txt += "</p>";
				}
				// System.err.println( ",,,,..,,,, "+txt );

				/*
				 * _txt= _txt.replace("</b> </p>", "</b></p>"); if(
				 * _txt.contains("</b></p>") ){ _txt= _txt.replace("<p><b>",
				 * "<h2>").replace("</b></p>", "</h2>"); }
				 */

				txt += _txt;

			}

			// -txt = encodeToUnicodeStandard(txt);
			// System.err.println( "..________... "+txt );
			// parse it
			_container = new TraverseFind().breakIt(txt, "\\[\\[_(.*?)\\]\\]"); // "\\[_(.*?)\\]");
																				// \\[\\[_(.*?)\\]\\]

		} catch (IOException e) {
			err = "Error: Check file location and name: " + fileLoc;
			e.printStackTrace();

		} catch (Exception e) {

			err = "Error: not able to process file " + fileLoc;
			e.printStackTrace();

		} finally {
			// document.close();
			try {
				if (fs != null)
					fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return _container;
	}

	public java.util.Map breakIt(String STR, String pattern) {

		// System.err.println("_________"+pattern+"_______ "+ STR);

		String str = STR;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);

		java.util.List<String> headers = new java.util.ArrayList();
		while (m.find()) {

			// headers.add("<p><b>[["+m.group(1) +"]]</b></p>");

			String h = m.group(1);
			// h= h.replace("<b>","");
			// h= h.replace("</b>","");
			headers.add(h);

			// headers.add( m.group(1));
			// System.err.println("Adding: "+ m.group(1));
		}

		java.util.Map container = new java.util.LinkedHashMap();
		for (int i = 0; i < headers.size(); i++) {

			// System.err.println("****STR**"+pattern+"***** "+ str);
			str = str.trim();
			String from = headers.get(i);
			// from= from.replace("<b>","");
			// from= from.replace("</b>","");

			// System.err.println("####### from : "+ from);

			if (i <= headers.size() && (i + 1) != headers.size()) {
				String to = headers.get(i + 1);
				// to= to.replace("<b>","");
				// to= to.replace("</b>","");

				// System.err.println("#### to: "+ to );
				int ind_start = 0;// str.indexOf(from);
				if (str.contains("<p><b>[[Activity" + from))
					ind_start = str.indexOf("<p><b>[[Activity" + from);
				else if (str.contains("<p>[[Activity" + from))
					ind_start = str.indexOf("<p>[[Activity" + from);

				int ind_end = 0; // str.indexOf(to + "]");

				if (str.contains("<p><b>[["
						+ (pattern.contains("_") ? "_" : "Activity") + to))
					ind_end = str.indexOf(to + "]") - 16;// 9;
				else if (str.contains("<p>[["
						+ (pattern.contains("_") ? "_" : "Activity") + to))
					ind_end = str.indexOf(to + "]") - 9;// 5;

				// System.err.println( ind_start +" : "+ ind_end );
				String parg = str.substring(ind_start, ind_end);
				// System.err.println( ind_start +" : "+ ind_end );

				str = str.substring(ind_end);
				// System.err.println("???"+
				// pattern+"???? "+headers.get(i)+" *********** "+ parg);
				container.put(
						headers.get(i).replace("<b>", "").replace("</b>", ""),
						parg);

			}// edn if

		}// edn for

		// System.err.println("????" + pattern +"?????... "+ str);

		if (headers.size() == 0) {
			container.put("", str);
			// System.err.println("??????? XXX *********** "+ str);
		} else {
			container.put(headers.get(headers.size() - 1).replace("<b>", "")
					.replace("</b>", ""), str);
			// System.err.println("??????? "+ headers.get(headers.size() - 1)
			// +" *********** "+ str);
		}

		return container;
	}

	public void writeToFile(String txt) throws Exception {
		FileOutputStream out1 = new FileOutputStream(
				"/Users/akobovich/Desktop/testHtml.html");
		out1.write(txt.getBytes());
		out1.close();
	}

	private String frmFont(String font) {

		if (font != null && font.toLowerCase().trim().contains("arial bold"))
			return "30px arial bold ,sans-serif";
		else
			return font;

	}

	public static String encodeToUnicodeStandard(String pData) {
		StringBuffer encodedData = new StringBuffer();

		StringBuffer sBuff = new StringBuffer(pData);
		for (int i = 0; i < sBuff.length(); i++) {
			char ch = sBuff.charAt(i);
			int chVal = (int) ch;

			// if this is a special character (ie. > 8 bits), encode it
			if (chVal > Byte.MAX_VALUE) {

				encodedData.append("&#x").append(Integer.toHexString(chVal))
						.append(";");

			} else {
				encodedData.append(ch);
			}
		}

		return encodedData.toString();
	}

	public java.util.Map breakup1(String txt, String pattern) {

		java.util.Map _container = new java.util.LinkedHashMap();
		java.util.Map container = breakIt(txt, pattern);
		java.util.Iterator itr = container.keySet().iterator();
		while (itr.hasNext()) {

			String title = (String) itr.next();

			String str = (String) container.get(title);

			_container.put(title, str);

		}

		return container;
	}

	public Meeting processMeeting(String txt) {

		Meeting meeting = new Meeting();

		String pattern = "\\[.*?Activity(.*?)\\]]";
		pattern = "\\[\\[Activity(.*?)\\]\\]";

		// System.err.println( "$$$$$$$$$$$ : "+ txt);
		java.util.Map container = breakup1(txt, pattern);

		java.util.List<Activity> activities = new java.util.ArrayList();

		java.util.Iterator itr = container.keySet().iterator();
		while (itr.hasNext()) {

			String title = (String) itr.next();
			// System.err.println("******* "+ title);

			// - title = title.replace("[", "");
			setActivityLog("Processing title: " + title);
			String str = (String) container.get(title);

			// - title = title.replaceAll("<.*?>", "");
			Activity activity = new Activity();

			// String g= str.substring(str.indexOf("<p>") );
			// g= g.replaceAll("\\[Activity\\|(.*?)\\|(.*?)\\]]", "hello");

			// System.err.println("STR ::: "+str);
			activity.setActivityDescription(str);// .substring(str.indexOf("<p>")));
			// activity.setActivityDescription(g);

			StringTokenizer t = new StringTokenizer(title, "|");

			activity.setActivityNumber(Integer.parseInt(t.nextToken()));
			activity.setName(t.nextToken().replace("&#x201d;", ""));

			// System.err.println("Adding activ: "+ activity.getName() +" num: "
			// + activity.getActivityNumber() );

			// GOOD activity.setDuration(Integer.parseInt( t.nextToken() ) );

			// - 9/7/14 java.util.Map subContainer = breakup1(str,
			// "\\[(.*?)\\]]");
			java.util.Map subContainer = breakup1(str, "\\[\\[(.*?)\\]]");

			java.util.Iterator subItr = subContainer.keySet().iterator();
			activities.add(activity);

		}

		meeting.setActivities(activities);
		return meeting;

	}

	private String getRetrValue(java.util.Map container, String match) {

		String meetingId = null;
		String test = container.get(match).toString().replace("<p></p>", "");
		Pattern p = Pattern.compile("<p>(.+?)</p>");
		Matcher m = p.matcher(test);
		while (m.find()) {

			meetingId = m.group(1);
		}
		return meetingId.trim();

	}

	private void test() {

		String pattern = "\\[Activity\\|(.*?)\\|(.*?)\\]]";
		// pattern="\\[Activity\\|[0-9]";////\[\[Activity\|[0-9]\|(.*?)\|[0-9]+\]\]
		String str = "[[Activity|1|&#x201d;As Girls Arrive&#x201d;]]";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);

		while (m.find()) {
			int count = m.groupCount();
			for (int i = 0; i <= m.groupCount(); i++) {
				// System.err.println("*** * " + m.group(i));
			}
		}
	}

	private String fmtActivity(String str) {

		String pattern = "\\[Activity\\|(.*?)\\|(.*?)\\]]";

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);

		// String activityName="";
		while (m.find()) {
			str = str.replace(m.group(0),
					"Activity " + m.group(1) + " : " + m.group(2));

		}
		return str;
	}

}// end of class


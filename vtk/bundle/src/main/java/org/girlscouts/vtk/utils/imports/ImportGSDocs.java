package org.girlscouts.vtk.utils.imports;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Meeting;
import java.util.zip.*;

public class ImportGSDocs {

	private Session session; // jcr connect to repository
	private String rootPath = "/Users/akobovich/Documents/VTK_Imports/VTK-ASSETS";
	private String meetindDir = null;
	private String xlsFile = "metadata.xlsx";
	private String activityLog = "";

	public ImportGSDocs(Session session) {
		this.session = session;
	}

	public ImportGSDocs(Session session, String mainDir, String meetingDir,
			String xlsFileName) {
		this.session = session;
		this.rootPath = mainDir;
		this.xlsFile = xlsFileName;
		this.meetindDir = meetingDir;
	}

	public void doClean(String fileName) {

		try {

			java.io.File file = new java.io.File(fileName);

			if (file.delete()) {
				System.err.println(file.getName() + " is deleted!");
			} else {
				System.err.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// public Meeting getMeetings(String testFile) throws Exception {
	/*
	 * public void getMeetings(String testFile) throws Exception {
	 * getMeetings(testFile, null); } public void getMeetings(String testFile,
	 * String singleDocName) throws Exception {
	 */

	public void getMeetings() throws Exception {
		getMeetings(null);
	}

	public void getMeetings(String singleDocName) throws Exception {

		/*
		 * System.err.println("Fiule: "+ testFile); //InputStream in = new
		 * java.net.URL(
		 * "http://localhost:4503/tmp/import/assets/15_0.03358374794012875/jcr%3Acontent"
		 * ).openConnection().getInputStream(); java.util.Map<String, String>
		 * __meetings = new TraverseFind().getMeetingInfo(testFile);
		 * System.err.println("Test: "+ __meetings==null); if(true)return
		 * __meetings;
		 */

		/*
		 * javax.jcr.Repository repository =
		 * JcrUtils.getRepository("http://localhost:4503/crx/server/");
		 * 
		 * // Workspace Login SimpleCredentials creds = new
		 * SimpleCredentials("admin", "admin".toCharArray());
		 * 
		 * Session session = null; session = repository.login(creds,
		 * "crx.default"); Node root = session.getRootNode();
		 * 
		 * Node fileNode =
		 * root.getNode("/tmp/import/assets/15_0.03358374794012875/"); Node
		 * jcrContent = fileNode.getNode("jcr:content"); String fileName =
		 * fileNode.getName(); InputStream content =
		 * jcrContent.getProperty("jcr:data").getStream();
		 * System.err.println("CHK: "+ (content ==null ));
		 */

		// ImportGSDocs me = new ImportGSDocs();
		Meeting meeting = null;

		doHeal();
		// if(true)return;

		FileInputStream fis = new FileInputStream(rootPath + "/" + xlsFile);
		// "/Users/mike/Desktop/brownie/metadata.xlsx");
		activityLog = activityLog.concat("<br/>Processing xls file...");
		Workbook workbook = WorkbookFactory.create(fis);

		FormulaEvaluator evaluator = workbook.getCreationHelper()
				.createFormulaEvaluator();

		Sheet sheet = workbook.getSheetAt(0);
		int i = 1;
		char levelInitial = '-';
		int position = 0;
		while (true) {
			i++;
			// System.err.println(i);
			String meetingId = getCellVal(evaluator, sheet, "A" + i);

			// System.err.println( "mid: "+meetingId );

			if (meetingId == null || meetingId.equals("")) {
				System.err.println("mid empty..exiting loop");
				break;
			}

			if (singleDocName != null
					&& !meetingId.toUpperCase().trim()
							.equals(singleDocName.toUpperCase().trim()))
				continue;

			if (meetingId.charAt(0) != levelInitial) {
				position = 1;
				levelInitial = meetingId.charAt(0);
			} else {
				position++;
			}

			// String meetingTitle = me.getCellVal(evaluator, sheet, "B" + i);
			String meetingName = getCellVal(evaluator, sheet, "B" + i);
			String level = getCellVal(evaluator, sheet, "C" + i);
			String meetingBlurb = getCellVal(evaluator, sheet, "D" + i);
			String cat = getCellVal(evaluator, sheet, "E" + i);
			String aids_tags = getCellVal(evaluator, sheet, "F" + i)
					.replaceAll("\\s+?", ";");
			String resource_tags = getCellVal(evaluator, sheet, "G" + i)
					.replaceAll("\\s+?", ";");
			String agenda = getCellVal(evaluator, sheet, "H" + i);
			// System.err.println( "strAgendar: "+ agenda);
			meeting = new Meeting();
			meeting.setId(meetingId.trim());
			meeting.setName(meetingName.trim());
			meeting.setBlurb(meetingBlurb.trim());
			meeting.setLevel(level.trim());
			meeting.setCat(cat.trim());
			meeting.setAidTags(aids_tags.trim());
			meeting.setAgenda(agenda.trim());

			// System.err.println( "agenda: "+ meeting.getAgenda() );

			meeting.setResources(resource_tags.trim());
			meeting.setPosition(position);

			String meetingPath = "/content/girlscouts-vtk/meetings/2014/"
					+ level + "/" + meetingId;
			// meetingPath= "/content/girlscouts-vtk/meetings/"+meetingId;
			meetingPath = "/content/girlscouts-vtk/meetings/myyearplan/"
					+ level.toLowerCase() + "/" + meetingId;
			meeting.setPath(meetingPath);

			// System.err.println("Processing: "+ meetingId.toUpperCase() ) ;
			// meeting =doSingleFile(meeting.getPath(), meeting);
			activityLog = activityLog.concat("<br/><br/>Processing "
					+ meetingId.toUpperCase() + ".docx");
			meeting = doSingleFile(rootPath + "/" + meetindDir + "/"
					+ meetingId.toUpperCase() + ".docx", meeting);

			// ********* DOCX
			/*
			 * TraverseFind docx = new TraverseFind();
			 * 
			 * java.util.Map<String, String> meetings = docx
			 * .getMeetingInfo(rootPath+ "/meetings/" + meetingId.toUpperCase()
			 * + ".docx");
			 * 
			 * Meeting docxMeeting = null; try { docxMeeting =
			 * docx.getMeeting(meetings); } catch (Exception e) {
			 * e.printStackTrace(); } if (docxMeeting == null) {
			 * System.err.println("Cannot parse meeting:" + meetingId); } else {
			 * List<Activity> chngActivities = new java.util.ArrayList();
			 * Pattern p = Pattern.compile("\\[(.*?)\\^(.*?)\\^(.*?)\\]");
			 * Matcher m = p.matcher(meeting.getAgenda()); int count = 0; while
			 * (m.find()) { Activity activity =
			 * docxMeeting.getActivities().get(count); //
			 * System.err.println(count+ " - "+m.group(0)+" ___________" //
			 * +m.group(2)+ "____    "+m.group(3) +" : :  :"+ //
			 * activities.get(count).getName() //
			 * +" : "+activities.get(count).getActivityNumber());
			 * activity.setDuration(Integer.parseInt(m.group(3))); //
			 * activity.setPath("/ALEX");
			 * 
			 * String desc = activity.getActivityDescription().replace(
			 * "[[Activity", ""); if (desc.endsWith("<p>")) desc =
			 * desc.substring(0, desc.lastIndexOf("<p>"));
			 * 
			 * desc = Formatter.format(desc);
			 * activity.setActivityDescription(desc);
			 * chngActivities.add(activity); count++; }
			 * 
			 * meeting.setActivities(chngActivities);
			 * 
			 * java.util.Map<String, JcrCollectionHoldString> _meetings = new
			 * java.util.TreeMap<String, JcrCollectionHoldString>();
			 * 
			 * java.util.Iterator itr = meetings.keySet().iterator(); while
			 * (itr.hasNext()) { String title = (String) itr.next(); String
			 * titleWithoutTags = title.replaceAll("<.*?>", "");
			 * 
			 * 
			 * String value = docx.fmtStr(meetings.get(title)); if
			 * (titleWithoutTags.equals("meeting short description") ||
			 * titleWithoutTags.equals("meeting id")) { value =
			 * Formatter.stripTags(value); } else { value =
			 * Formatter.format(value); }
			 * 
			 * _meetings.put( titleWithoutTags, new
			 * JcrCollectionHoldString(value)); }
			 * 
			 * meeting.setMeetingInfo(_meetings);
			 * 
			 * try { doJcr(meeting); } catch (Exception e) {
			 * e.printStackTrace(); }
			 * 
			 * }
			 */

			try {
				// System.err.println("to jcr");
				boolean isInDb = false;
				if (meeting != null) {

					isInDb = doJcr(meeting);

				}

				if (isInDb)
					activityLog = activityLog.concat("<br/>populating db...");
				else
					activityLog = activityLog
							.concat("<br/><font color='red'>Not processed</font>");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// return meeting;
	}

	// Meeting
	public boolean doJcr(Meeting meeting) throws Exception {

		boolean isSucc = false;
		/*
		 * // Connection javax.jcr.Repository repository = JcrUtils
		 * .getRepository("http://localhost:4503/crx/server/");
		 * 
		 * // Workspace Login SimpleCredentials creds = new
		 * SimpleCredentials("admin", "admin".toCharArray()); Session session =
		 * null;
		 * 
		 * session = repository.login(creds, "crx.default");
		 */
		Node root = session.getRootNode();

		List<Class> classes = new ArrayList<Class>();
		classes.add(Meeting.class);
		classes.add(Activity.class);
		classes.add(JcrCollectionHoldString.class);

		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);

		String path = meeting.getPath().substring(0,
				meeting.getPath().lastIndexOf("/"));

		// System.err.println("Path: "+ path );
		// System.err.println("Meeting path : "+ meeting.getPath() );

		// Node isExist = JcrUtils.getOrCreateUniqueByPath(path,
		// "nt:unstructured", session);

		if (session.itemExists(meeting.getPath()))
			ocm.update(meeting);
		else {
			Node isExist = JcrUtils.getOrCreateByPath(path, "nt:unstructured",
					session);
			ocm.insert(meeting);
		}

		ocm.save();
		isSucc = true;
		return isSucc;
	}

	private String getCellVal(FormulaEvaluator evaluator, Sheet sheet,
			String field) {

		String toRet = "";
		CellReference ref = new CellReference(field);
		Row r = sheet.getRow(ref.getRow());
		if (r != null) {
			Cell c = r.getCell(ref.getCol());
			CellValue value = evaluator.evaluate(c);

			if (value != null)
				toRet = value.getStringValue();
		}
		return toRet.trim();
	}

	public void doHeal() {

		// java.util.Map<String, String> results = new java.util.TreeMap<String,
		// String>();

		java.io.File[] files = new java.io.File(rootPath + "/meetings/")
				.listFiles();

		for (java.io.File file : files) {
			if (file.isFile()) {
				String name = file.getName();
				String fixedName = file.getName();
				if (name.contains("_")) {
					String z = file.getName();
					z = z.replace(" ", "_");
					String x = z.substring(0, z.indexOf("_"));
					String y = z.substring(z.lastIndexOf("."));
					fixedName = x + y;
				}
				if (!fixedName.equals(name))
					renameFile(rootPath + "/meetings/" + name, rootPath
							+ "/meetings/" + fixedName);

				System.err.println("\n" + name + " : " + fixedName);
				// results.put(name, fixedName);
			}
		}

		// System.err.println( results );
	}

	private boolean renameFile(String oldFileName, String newFileName) {

		java.io.File oldfile = new java.io.File(oldFileName);
		java.io.File newfile = new java.io.File(newFileName);

		if (oldfile.renameTo(newfile)) {
			System.out.println("Rename succesful");
		} else {
			return false;
		}
		return true;
	}

	// public Meeting doSingleFile(String fileName) throws Exception{
	public Meeting doSingleFile(String fileName) throws Exception {
		return doSingleFile(fileName, null);
	}

	public Meeting doSingleFile(String fileName, Meeting meeting)
			throws Exception {

		System.err.println("FileLoc: " + fileName);

		// Meeting meeting = meeting;
		if (meeting == null)
			meeting = new Meeting();
		// ********* DOCX
		TraverseFind docx = new TraverseFind();

		java.util.Map<String, String> meetings = docx.getMeetingInfo(fileName);
		if (meetings == null) {
			System.err.println("Found error. file not processed " + fileName);
			return null;
		}

		Meeting docxMeeting = null;
		try {

			docxMeeting = docx.getMeeting(meetings);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (docxMeeting == null) {
			System.err.println("Cannot parse meeting:" + fileName);
		} else {

			/*
			 * meeting.setAgenda("[1^As Girls Arrive^5]"+
			 * "[2^Opening Ceremony: Circle of Adventure^5]"+
			 * "[3^Introducing the Journey Awards^5]"+ "[4^Team Passport^20]"+
			 * "[5^Games Around the Globe^15]"+
			 * "[6^Our Globe of Girls^15]                       [7^Story and Snack Time^20]                             [8^Closing Ceremony: A Great Place^5]"
			 * );
			 */

			if (meeting.getAgenda() != null) { // preview no xls

				List<Activity> chngActivities = new java.util.ArrayList();
				Pattern _p = Pattern.compile("\\[(.*?)\\^(.*?)\\^(.*?)\\]");
				Matcher _m = _p.matcher(meeting.getAgenda());
				int count = 0;
				while (_m.find()) {
					// System.err.println( "test: "+_m.group(3) );
					// System.err.println(
					// "Activ size: "+docxMeeting.getActivities().size() );

					Activity activity = docxMeeting.getActivities().get(count);

					activity.setDuration(Integer.parseInt(_m.group(3)));

					String desc = activity.getActivityDescription();

					/*
					 * desc= desc.replace("[[Activity", ""); if
					 * (desc.endsWith("<p>")) desc = desc.substring(0,
					 * desc.lastIndexOf("<p>"));
					 * 
					 * desc = Formatter.format(desc);
					 */

					// //////// fmt
					String txt = desc;

					txt = txt.replaceAll("\\[\\[_(.*?)\\]\\]", "");

					java.util.regex.Pattern p = java.util.regex.Pattern
							.compile("\\[\\[Activity(.*?)\\]\\]");
					java.util.regex.Matcher m = p.matcher(txt);
					while (m.find()) {
						// out.println("**"+m.group()+"**");
						String rpls = "Activity ";
						java.util.StringTokenizer t = new java.util.StringTokenizer(
								m.group(), "|");
						t.nextToken();
						rpls += t.nextToken() + " : ";
						rpls += t.nextToken().replace("]]", "");
						txt = txt.replace(m.group(), rpls);
					}
					// ////// end fmt

					// System.err.println("Activ: "+
					// activity.getActivityDescription());
					activity.setActivityDescription(txt);
					chngActivities.add(activity);
					count++;
				}

				meeting.setActivities(chngActivities);
			}

			java.util.Map<String, JcrCollectionHoldString> _meetings = new java.util.TreeMap<String, JcrCollectionHoldString>();

			java.util.Iterator itr = meetings.keySet().iterator();
			while (itr.hasNext()) {
				String title = (String) itr.next();
				// String titleWithoutTags = title.replaceAll("<.*?>", "");

				String value = meetings.get(title); // docx.fmtStr(meetings.get(title));
				/*
				 * if (titleWithoutTags.equals("meeting short description") ||
				 * titleWithoutTags.equals("meeting id")) { // value =
				 * Formatter.stripTags(value); } else { // value =
				 * Formatter.format(value); }
				 */

				// ********* startfmt
				String txt = value;

				txt = txt.replaceAll("\\[\\[_(.*?)\\]\\]", "");

				java.util.regex.Pattern p = java.util.regex.Pattern
						.compile("\\[\\[Activity(.*?)\\]\\]");
				java.util.regex.Matcher m = p.matcher(txt);
				while (m.find()) {
					// out.println("**"+m.group()+"**");
					String rpls = "Activity ";
					java.util.StringTokenizer t = new java.util.StringTokenizer(
							m.group(), "|");
					t.nextToken();
					rpls += t.nextToken() + " : ";
					rpls += t.nextToken().replace("]]", "");
					txt = txt.replace(m.group(), rpls);
				}
				// ********* end fmt

				// _meetings.put(title,new JcrCollectionHoldString(value));
				_meetings.put(title, new JcrCollectionHoldString(txt));
			}

			meeting.setMeetingInfo(_meetings);

		}
		return meeting;
	}

	public void unzip(String zipFilePath, String destDirectory)
			throws java.io.IOException {

		System.err.println("--" + zipFilePath);

		java.io.File destDir = new java.io.File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(
				zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		System.err.println(entry == null);

		while (entry != null) {
			String filePath = destDirectory + java.io.File.separator
					+ entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it

				System.err.println(filePath);

				// extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				// File dir = new File(filePath);
				// dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	public void unzipFile1(String filePath, String dir) {

		FileInputStream fis = null;
		java.util.zip.ZipInputStream zipIs = null;
		java.util.zip.ZipEntry zEntry = null;
		try {
			fis = new FileInputStream(filePath);
			zipIs = new java.util.zip.ZipInputStream(
					new java.io.BufferedInputStream(fis));
			while ((zEntry = zipIs.getNextEntry()) != null) {
				try {
					byte[] tmp = new byte[4 * 1024];
					java.io.FileOutputStream fos = null;
					String opFilePath = dir + "/" + zEntry.getName();
					fos = new java.io.FileOutputStream(opFilePath);
					int size = 0;
					while ((size = zipIs.read(tmp)) != -1) {
						fos.write(tmp, 0, size);
					}
					fos.flush();
					fos.close();
				} catch (Exception ex) {

				}
			}
			zipIs.close();
		} catch (java.io.FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getActivityLog() {
		return this.activityLog;
	}
}

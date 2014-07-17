package org.girlscouts.tools.meetingimporter;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
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

public class ParseXls1 {
    public static void main(String[] args) throws Exception {
        ParseXls1 me = new ParseXls1();

        FileInputStream fis = new FileInputStream(
                "/Users/mike/Desktop/brownie/metadata.xlsx");
        Workbook workbook = WorkbookFactory.create(fis);

        FormulaEvaluator evaluator = workbook.getCreationHelper()
                .createFormulaEvaluator();

        Sheet sheet = workbook.getSheetAt(0);
        int i = 1;
        while (true) {
            i++;

            String meetingId = me.getCellVal(evaluator, sheet, "A" + i);
            if (meetingId == null || meetingId.equals("")) {
                System.err.println("xls record# : " + i);
                break;
            }

            //String meetingTitle = me.getCellVal(evaluator, sheet, "B" + i);
            String meetingName = me.getCellVal(evaluator, sheet, "B" + i);
            String level = me.getCellVal(evaluator, sheet, "C" + i);
            String meetingBlurb = me.getCellVal(evaluator, sheet, "D" + i);
            String cat = me.getCellVal(evaluator, sheet, "E" + i);
            String aids_tags = me.getCellVal(evaluator, sheet, "F" + i);
            String resource_tags = me.getCellVal(evaluator, sheet, "G" + i);
            String agenda = me.getCellVal(evaluator, sheet, "H" + i);

            Meeting meeting = new Meeting();
            meeting.setId(meetingId);
            meeting.setName(meetingName);
            meeting.setBlurb(meetingBlurb);
            meeting.setLevel(level);
            meeting.setCat(cat);
            meeting.setAidTags(aids_tags);
            meeting.setAgenda(agenda);
            meeting.setResources(resource_tags);

            String meetingPath = "/content/girlscouts-vtk/meetings/2014/"
                    + level + "/" + meetingId;
            // meetingPath= "/content/girlscouts-vtk/meetings/"+meetingId;
            meetingPath = "/content/girlscouts-vtk/meetings/myyearplan/"
                    + level.toLowerCase() + "/" + meetingId;
            meeting.setPath(meetingPath);

            // ********* DOCX
            TraverseFind docx = new TraverseFind();

            java.util.Map<String, String> meetings = docx
                    .getMeetingInfo("/Users/mike/Desktop/brownie/meetings/"
                            + meetingId.toUpperCase() + ".docx");

            Meeting docxMeeting = null;
            try {
                docxMeeting = docx.getMeeting(meetings);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (docxMeeting == null) {
                System.err.println("Cannot parse meeting:" + meetingId);
            } else {
                List<Activity> chngActivities = new java.util.ArrayList();
                Pattern p = Pattern.compile("\\[(.*?)\\^(.*?)\\^(.*?)\\]");
                Matcher m = p.matcher(meeting.getAgenda());
                int count = 0;
                while (m.find()) {
                    Activity activity = docxMeeting.getActivities().get(count);
                    // System.err.println(count+ " - "+m.group(0)+" ___________"
                    // +m.group(2)+ "____    "+m.group(3) +" : :  :"+
                    // activities.get(count).getName()
                    // +" : "+activities.get(count).getActivityNumber());
                    activity.setDuration(Integer.parseInt(m.group(3)));
                    // activity.setPath("/ALEX");

                    String desc = activity.getActivityDescription().replace(
                            "[[Activity", "");
                    if (desc.endsWith("<p>"))
                        desc = desc.substring(0, desc.lastIndexOf("<p>"));

                    activity.setActivityDescription(desc);
                    chngActivities.add(activity);
                    count++;
                }

                meeting.setActivities(chngActivities);

                java.util.Map<String, JcrCollectionHoldString> _meetings = new java.util.TreeMap<String, JcrCollectionHoldString>();

                java.util.Iterator itr = meetings.keySet().iterator();
                while (itr.hasNext()) {
                    String title = (String) itr.next();
                    String titleWithoutTags = title.replaceAll("<.*?>", "");

                    _meetings.put(
                            titleWithoutTags,
                            new JcrCollectionHoldString(docx.fmtStr(meetings
                                    .get(title))));
                }

                meeting.setMeetingInfo(_meetings);

                try {
                    me.doJcr(meeting);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    // Meeting
    public void doJcr(Meeting meeting) throws Exception {
        System.out.println("beginning doJcr");
        // Connection
        javax.jcr.Repository repository = JcrUtils
                .getRepository("http://localhost:4502/crx/server/");

        // Workspace Login
        SimpleCredentials creds = new SimpleCredentials("admin",
                "admin".toCharArray());
        Session session = null;
        session = repository.login(creds, "crx.default");
        Node root = session.getRootNode();

        List<Class> classes = new ArrayList<Class>();
        classes.add(Meeting.class);
        classes.add(Activity.class);
        classes.add(JcrCollectionHoldString.class);

        Mapper mapper = new AnnotationMapperImpl(classes);
        ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);

        String path = meeting.getPath().substring(0,
                meeting.getPath().lastIndexOf("/"));
        System.err.println(" PAthL: " + path);

        /*
         * Node node = root.getNode(path); System.err.println((node==null)
         * +" PAthL: "+ path );
         */

        // Node isExist = JcrUtils.getOrCreateUniqueByPath(path,
        // "nt:unstructured", session);

        if (session.itemExists(meeting.getPath()))
            ocm.update(meeting);
        else {
            Node isExist = JcrUtils.getOrCreateByPath(path, "nt:unstructured", session);
            ocm.insert(meeting);
        }
        ocm.save();

        // System.err.println("doneee 1");

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
        return toRet;
    }

}
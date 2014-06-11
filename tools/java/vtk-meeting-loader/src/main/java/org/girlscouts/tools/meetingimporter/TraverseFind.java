package org.girlscouts.tools.meetingimporter;

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
import org.girlscouts.tools.meetingimporter.models.Activity;
import org.girlscouts.tools.meetingimporter.models.Meeting;

public class TraverseFind {

    private String err = "";
    private String activityLog = null;

    public String fmtStr(String txt) {

        txt = txt.replace("[[_", "");

        if (txt.contains("[[Activity")) {
            txt = fmtActivity(txt).replace("[Activity", "Activity");
        }

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
                System.err.println("MeetingId:: " + meetingId);
            } catch (Exception e) {
                me.err += "Failed to retrieve field 'meeting id'";
                e.printStackTrace();
                me.log();
                return;
            }

            // TEST System.err.println( "** "+container.get("overview"));

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
                //meeting.setMeetingInfo(getMeetingInfo);

                System.err.println("**********  " + container.get("overview"));
                /*
                 * //** write to JSON file new test.TestJson().toJson(meeting);
                 * 
                 * 
                 * //write to MangoDb new
                 * test.TestJson().saveMeetingToDb(meeting);
                 */
            }

            // print title, msg
            me.printContainer(container);

            // write to flat file
            me.writeToFile(container);

        } catch (Exception e) {
            me.err += " File not processed " + e.getMessage();
            e.printStackTrace();
        }

        me.log();

        me.err = null;
        me.activityLog = null;
    }

    public void writeToFile(java.util.Map container) throws Exception {

        StringBuffer strBuf = new StringBuffer();
        java.util.Iterator _itr = container.keySet().iterator();
        while (_itr.hasNext()) {
            String title = (String) _itr.next();
            // System.err.println( "Titles: "+title + " : "+
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
            // System.err.println( "Titles: "+title + " : "+
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
            System.err.println("--- " + title);
            // MEETING populate
            if (title.trim().toLowerCase().equals("detailed activity plan"))
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
            System.err.println("Titles: " + title);
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

            fs = new FileInputStream(fileLoc);
            // fs = new FileInputStream("/Users/akobovich/Desktop/caca.docx");
            XWPFDocument hdoc = new XWPFDocument(OPCPackage.open(fs));

            java.util.List<XWPFParagraph> parags = hdoc.getParagraphs();

            String txt = "";
            for (int p = 0; p < parags.size(); p++) {
                XWPFParagraph Par = parags.get(p);

                if (true)// Par.getRuns().size() ==0 )
                    txt += "<p>";
                else
                    txt += "<p style=\"font-size:"
                            + Par.getRuns().get(0).getFontSize()
                            + "; font: "
                            + new TraverseFind().frmFont(Par.getRuns().get(0)
                                    .getFontFamily()) + ";\">";

                if (Par.getNumID() != null)
                    txt += "<li>";

                java.util.List<XWPFRun> runs = Par.getRuns();
                for (int y = 0; y < runs.size(); y++) {
                    XWPFRun run = runs.get(y);
                    String str = run.toString().toString();

                    // if( Par.getRuns().size() !=0 &&
                    // str.trim().toLowerCase().equals("steps"))
                    // str="[Steps]]";

                    if (run.isBold())
                        txt += "<b>" + str + "</b>";
                    else if (run.isItalic())
                        txt += "<i>" + str + "</i>";
                    else if (run.isStrike())
                        txt += "<del>" + str + "</del>";
                    else
                        txt += str;

                }// edn of
                if (Par.getNumID() != null)
                    txt += "</li>";
                txt += "</p>";

            }

            txt = encodeToUnicodeStandard(txt);

            // parse it
            _container = new TraverseFind().breakIt(txt, "\\[_(.*?)\\]");

        } catch (IOException e) {
            err = "Error: Check file location and name: " + fileLoc;
            e.printStackTrace();

        } catch (Exception e) {

            err = "Error: not able to process file " + fileLoc;
            e.printStackTrace();

        } finally {
            // document.close();
            try {

                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return _container;
    }

    public java.util.Map breakIt(String STR, String pattern) {

        String str = STR;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);

        java.util.List<String> headers = new java.util.ArrayList();
        while (m.find()) {

            headers.add(m.group(1));

        }
        // System.err.println(headers);

        java.util.Map container = new java.util.LinkedHashMap();
        for (int i = 0; i < headers.size(); i++) {

            String from = headers.get(i);
            if (i <= headers.size() && (i + 1) != headers.size()) {
                String to = headers.get(i + 1);

                int ind_start = str.indexOf(from);

                /*
                 * org :do not remove int ind_end = str.indexOf( to ); String
                 * parg = str.substring( ind_start, ind_end );
                 */

                int ind_end = str.indexOf(to + "]");
                String parg = str.substring(ind_start, ind_end);

                str = str.substring(ind_end);

                container.put(headers.get(i), parg);

                // System.err.println( ">> "+parg);
            }// edn oif

        }// edn for

        if (headers.size() == 0)
            container.put("", str);
        else
            container.put(headers.get(headers.size() - 1), str);

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
        // System.err.println("&&&&&&&&&&& Meeting.."+txt);
        String pattern = "\\[Activity(.*?)\\]]"; // "\\[[Activity|[0=9]|\\]]";
                                                 // //"\\[(.*?)\\]]"
        java.util.Map container = breakup1(txt, pattern);
        java.util.List<Activity> activities = new java.util.ArrayList();

        java.util.Iterator itr = container.keySet().iterator();
        while (itr.hasNext()) {

            String title = (String) itr.next();
            title = title.replace("[", "");
            setActivityLog("Processing title: " + title);
            // System.err.println( "Title: "+ title);
            String str = (String) container.get(title);

            Activity activity = new Activity();

            // String g= str.substring(str.indexOf("<p>") );
            // g= g.replaceAll("\\[Activity\\|(.*?)\\|(.*?)\\]]", "hello");

            activity.setActivityDescription(str.substring(str.indexOf("<p>")));
            // activity.setActivityDescription(g);

            StringTokenizer t = new StringTokenizer(title, "|");
            // t.nextToken(); //?

            activity.setActivityNumber(Integer.parseInt(t.nextToken()));
            activity.setName(t.nextToken().replace("&#x201d;", ""));
            // GOOD activity.setDuration(Integer.parseInt( t.nextToken() ) );

            java.util.Map subContainer = breakup1(str, "\\[(.*?)\\]]");

            java.util.Iterator subItr = subContainer.keySet().iterator();
            while (subItr.hasNext()) {

                String _title = (String) subItr.next();

                if (_title.replace("[", "").toLowerCase().trim()
                        .equals("materials")) {
                    activity.setMaterials((String) subContainer.get(_title));
                } else if (_title.replace("[", "").toLowerCase().trim()
                        .equals("steps")) {
                    activity.setSteps((String) subContainer.get(_title));
                }
            }
            activities.add(activity);

            // System.err.println(">>>"+activity.getActivityDescription());
            /*
             * System.err.println("\n\nActivity: "+ activity.getActivityNumber()
             * +" : "+ activity.getDuration() +" : "+
             * activity.getActivityDescription()); //activity.getName()
             * +" \nStpppp:"+activity.getSteps() );//+"\nMaterrrrr"+
             * activity.getMaterials()
             * +" Desc: "+activity.getActivityDescription());
             */

        }

        meeting.setActivities(activities);
        return meeting;

    }

    // SAmple <p>MEETING ID<p>
    private String getRetrValue(java.util.Map container, String match) {

        // System.err.println(container.get("meeting id") );
        String meetingId = null;
        String test = container.get(match).toString().replace("<p></p>", "");
        Pattern p = Pattern.compile("<p>(.+?)</p>");
        Matcher m = p.matcher(test);
        while (m.find()) {

            meetingId = m.group(1);
            // System.err.println( meetingId);
        }
        return meetingId.trim();

    }

    private void test() {

        String pattern = "\\[Activity\\|(.*?)\\|(.*?)\\]]";
        // pattern="\\[Activity\\|[0-9]";////\[\[Activity\|[0-9]\|(.*?)\|[0-9]+\]\]
        String str = "[[Activity|1|&#x201d;As Girls Arrive&#x201d;]]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        System.err.println(str);

        while (m.find()) {
            int count = m.groupCount();
            System.err.println(count);
            for (int i = 0; i <= m.groupCount(); i++) {
                System.err.println("*** * " + m.group(i));
            }
        }
        System.err.println(str.replaceAll("\\[Activity\\|(.*?)\\|(.*?)\\]]",
                "test"));

    }

    private String fmtActivity(String str) {

        String pattern = "\\[Activity\\|(.*?)\\|(.*?)\\]]";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        // System.err.println("** "+ str);

        // String activityName="";
        while (m.find()) {
            System.err.println(m.group(0) + " ::: " + m.group(2));
            // activityName =m.group(2);
            str = str.replace(m.group(0),
                    "Activity " + m.group(1) + " : " + m.group(2));

        }
        // System.err.println( str.replaceAll("\\[Activity\\|(.*?)\\|(.*?)\\]]",
        // "test") );
        return str; // .replaceAll("\\[Activity\\|(.*?)\\|(.*?)\\]]",
                    // activityName);
    }

}// end of class
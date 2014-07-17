package org.girlscouts.tools.meetingimporter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class Formatter {
    public static void main(String[] args) {
        String src = "<p></p><p><b>Materials</b></p><p><li>None</li></p><p> </p><p><b>Time Allotment</b></p><p>10 minutes</p><p></p><p><b>Steps</b></p><p><b>SAY: </b></p><p><li><i>Welcome! Are you looking forward to being Brownies this year? Are you ready to start our Brownie adventures together?</i></li></p><p><li><i>This is our Brownie </i><i>c</i><i>ircle</i><i>. It&#x2019;s a special place where we will come together to share thoughts and ideas and make decisions and plans for our troop. In the Brownie </i><i>R</i><i>ing, everyone gets a chance to talk, and everyone listens to what others have to say. </i></li></p><p><li><i>During this meeting, we are going to learn a little about Girl Scouts and spend some time getting to know each other better. Let&#x2019;s do that now.</i></li></p><p><li><i>Each person is going to say their name along with an action-word to go with their name </i><i>(such as \"Swimming Samantha</i><i>&#x201d;</i><i> or </i><i>&#x201c;</i><i>Jumping Jennifer\"), and then you will show the a</i><i>c</i><i>tion to the group. So if my name </i><i>was</i><i> Samantha, I would say &#x201c;Swimming Samantha&#x201d; and then pretend to swim. Got it?</i></li></p><p><li><i>Let&#x2019;s try! Everyone stand u</i><i>p</i><i> </i><i>&#x2014;</i><i>we might need some space for this.</i></li></p><p>Start the game by saying your action name and then perform the action.</p><p><li><i>Have girls repeat your action name and do the action</i><i>.</i></li></p><p>Now ask the girl to your left to say her action name and perform her action. Her action name should start with the same letter as her first name.</p><p><li><i>Great! Now let&#x2019;s all try to remember from the beginning. </i></li></p><p>Everyone says her action name and performs the action, then the next girl&#x2019;s name and performs the action, and then the third girl will introduce her action name and perform her action. The group will then repeat all the action names and perform the actions of all girls before her, and so on, until all girls have been introduced.</p><p></p><p><b>Keep It Girl-Led Tip</b></p><p>Let girls choose their own action word for their name. Don&#x2019;t decide for them, or let other girls decide for them. If a girl is struggling to come up with a word, ask if she would like the group&#x2019;s help before giving her hints.</p><p></p><p>After the game, introduce yourself by telling a little about you, including whether you&#x2019;re related to any girls in the troop.</p><p></p><p><b>SAY:</b></p><p><li><i>I will be guiding you this year as we have our troop meetings, do activities, earn badges and awards, sell cookies, and so much more. The most important thing that I want you to know is that this is YOUR Brownie troop and you get to make the plans and dec</i><i>i</i><i>sions</i><i>.</i><i> </i></li></p><p><li><i>How does that sound?</i></li></p><p><li><i>I will help to guide you and make sure we always stay safe.</i></li></p><p></p><p>Cadettes or members of your Friends and Family Network should also introduce themselves.</p><p></p><p><b>";
        System.out.println(Formatter.format(src));
    }
    
    public static String format(String src) {
        String dst = src;
        
        // Cleanup html
        Document doc = Jsoup.parse(dst);
        dst = doc.body().html();

        // Empty bold
        dst = dst.replaceAll("<b>(\\s*?)</b>", "$1");
        dst = dst.replaceAll("</b><b>", "");
        // Empty paragraphs
        dst = dst.replaceAll("<p>\\s*?</p>", "");
        // Extra <p> around <li>
        dst = dst.replaceAll("<p><li>(.*?)</li></p>", "<li>$1</li>");
        // Strange apostrophe
        dst = dst.replaceAll("’", "'");
        
        // Cleanup html
        doc = Jsoup.parse(dst);
        dst = doc.body().html();
        dst = Jsoup.clean(dst, Whitelist.relaxed());

        // Add <ul>
        String[] lines = dst.split(System.getProperty("line.separator"));
        boolean inUl = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!inUl && line.startsWith("<li>")) {
                lines[i] = "<ul>\n" + line;
                inUl = true;
            }
            
            if (inUl && (i == lines.length - 1 || !lines[i+1].startsWith("<li>"))) {
                lines[i] = line + "\n</ul>";
                inUl = false;
            }
        }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]).append('\n');
        }
        dst = builder.toString();

        return dst;
    }
}

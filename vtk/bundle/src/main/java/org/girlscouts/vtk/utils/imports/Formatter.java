package org.girlscouts.vtk.utils.imports;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class Formatter {
	public static void main(String[] args) {
		String src = "<p>Get girls excited about all the fun, friendships, and fabulous firsts they will experience in Girl Scouts this year!</p><p><li><i>Are you ready to learn about Brownie Girl Scout traditions?</i></li></p><p><li><i>Let&#x2019;s start by learning the Girl Scout Sign. Girl Scouts make the Girl Scout Sign when they recite the Girl Scout Promise and the Girl Scout Law. </i></li></p><p><li><i>Hold up your right hand with the three middle fingers up and hold your thumb and pinky finger together. </i></li></p><p><li><i>Now let&#x2019;s recite the Girl Scout Promise. Repeat after me as I say each line one at a time.</i></li></p><p>!!!!!SECONDLEVEL!!!!!<li>On my honor, </li></p><p>!!!!!SECONDLEVEL!!!!!<li>I will try,</li></p><p>!!!!!SECONDLEVEL!!!!!<li>to serve God and my country,</li></p><p>!!!!!SECONDLEVEL!!!!!<li>to help people at all times,</li></p><p>!!!!!SECONDLEVEL!!!!!<li>And to live by the Girl Scout Law.</li></p><p><li><i>Great job! Did you notice that when we say the </i><i>P</i><i>romise, we are holding up three fingers and there are three parts to the Girl Scout Promise? What were the three parts?</i></li></p><p>!!!!!SECONDLEVEL!!!!!<li><i>Serve God and my country</i></li></p><p>!!!!!SECONDLEVEL!!!!!<li><i>Help people at all times</i></li></p><p>!!!!!SECONDLEVEL!!!!!<li><i>Live by the Girl Scout Law</i></li></p><p><li><i>The three fingers that we hold up remind us that we are making these three important promises. These three parts of our Girl Scout </i><i>P</i><i>romise are also represented by the three clover shapes on your </i><i>name tag</i><i>. That shape is called a trefoil and it is the symbol for Girl Scouts. </i></li></p><p><li><i>Flip your </i><i>name tag</i><i> over</i><i>;</i><i> what is on the back?</i></li></p><p><li><i>That&#x2019;s right! It&#x2019;s the Girl Scout Promise. Now you can practice it at home.</i></li></p><p></p><p>Girls will: </p><p><li>Learn the Girl Scout Promise </li></p><p><li>Learn the Girl Scout Law</li></p><p><li>Play games to get to know each other</li></p><p></p><p>Feel free to substitute activities based on the experience of girls in your troop. For example, if your troop members already know each other or are familiar with the Girl Scout Promise and Law, use this opportunity to get to learn more about each other or to deepen the understanding of the Girl Scout Promise and Law.</p><p></p><p><b>Note to Volunteers</b></p><p>The excitement and fast pace of running troop meetings for the first time can sometimes leave us tongue-tied.&#xa0;For that reason scripting is included for&#xa0;guiding&#xa0;girls through a meeting; these \"lines\" are under the heading&#xa0;\"<b>SAY</b>.\"&#xa0;However, you&#x2019;re the expert. If you feel you don&#x2019;t need the script, do what makes sense for you and your girls.</p><p></p><p><b>Prepare Ahead</b></p><p></p><p>Talk with your Friends and Family Network, Cadettes earning their LiA Award, or other assistants about their roles for the first meeting. </p><p></p><p>Optional: Make a poster with the Girl Scout Promise and Law written on it.</p><p></p><p><b>Get Help from Your Friends and Family Network</b></p><p></p><p>Your Friends and Family Network can include:</p><p><li>The Brownies&#x2019; parents, aunts, uncles, older siblings, etc.</li></p><p><li>Cadettes who are interested in earning their LiA Award. This is an award for Girl Scout Cadettes (grades 6-8) who help Girl Scout Brownies with their activities.</li></p><p><li>Other volunteers who have offered to help with the meeting.</li></p><p></p><p>Ask them how they can pitch in. For example, they might want to:</p><p><li>Bring snacks</li></p><p><li>Help welcome girls to the meeting</li></p><p><li>Make name tags</li></p><p><li>Gather materials for activities</li></p><p><li>Guide the girls in one of the activities</li></p><p><li>Or just be ready to take care of small problems (spilled juice!), hand out art supplies (<i>S</i><i>hare the crayons, please</i>!), or act as cheerleaders (<i>I like how you are designing your name tag, great job!</i>).</li></p><p></p><p><b>Tip:</b> This first meeting is a special introduction to Girl Scouting! Invite your whole Network so everyone can join in the fun of starting Brownies on their Adventure!</p><p></p><p><b>Award Connection</b></p><p>This meeting is an introductory meeting. Girls will not be working towards an award at this meeting.</p><p></p><p><b>Meeting Length </b></p><p></p><p>90 minutes</p><p></p><p>The times given for each activity will be different, depending on how many girls are in the troop. </p><p></p><p>Plus, girls may really enjoy a particular activity and want to continue past the allotted time. As much as possible, let them! That&#x2019;s part of keeping Girl Scouting girl-led!</p><p></p><p>And what do you do if you only have an hour for the meeting? Simply omit some of the activities.</p><p></p><p><b>";
	}

	public static String stripTags(String src) {
		return src.replaceAll("<.*?>", "");
	}

	public static String format(String src) {

		String dst = src;

		// Cleanup html
		Document doc = Jsoup.parse(dst);
		dst = doc.body().html();

		// Empty bold
		dst = dst.replaceAll("<b>(\\s*?)</b>", "$1");
		dst = dst.replaceAll("</b>(\\s*?)<b>", "$1");
		// Empty italics
		dst = dst.replaceAll("<i>(\\s*?)</i>", "$1");
		dst = dst.replaceAll("</i>(\\s*?)<i>", "$1");
		// Empty paragraphs
		dst = dst.replaceAll("<p>\\s*?</p>", "");
		// Extra <p> around <li>
		dst = dst.replaceAll("<p><li>(.*?)</li></p>", "<li>$1</li>");
		// Strange apostrophe
		dst = dst.replaceAll("’", "'");
		// Stange double quote
		dst = dst.replaceAll("“", "");
		dst = dst.replaceAll("”", "");

		// Cleanup html
		doc = Jsoup.parse(dst);
		dst = doc.body().html();
		dst = Jsoup.clean(dst, Whitelist.relaxed());

		// mdash
		dst = dst.replaceAll("—", "&mdash;");

		return dst;
	}
}

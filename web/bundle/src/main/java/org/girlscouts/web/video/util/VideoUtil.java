package org.girlscouts.web.video.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.girlscouts.web.video.dto.Video;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoUtil {
		
	// Taken from carousel.jsp
	public static String extractYTId(String ytUrl) {
		String vId = null;
		Pattern pattern = Pattern.compile(".*(?:youtu\\.be\\/|v\\/|u\\/w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
		Matcher matcher = pattern.matcher(ytUrl);
		if (matcher.find()){
			vId = matcher.group(1);
		}
		return vId;
	}

	// Taken from carousel.jsp
	public static String extractVimeoId(String vimeoUrl) {
		String vId = null;
		Pattern pattern = Pattern.compile(".*(?:vimeo.com.*/)(\\d+)");
		Matcher matcher = pattern.matcher(vimeoUrl);
		if (matcher.find()){
			vId = matcher.group(1);
		}
		return vId;
	}

	// Taken from carousel.jsp
	public static String readUrlFile(String urlString) {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
			return buffer.toString();
		} catch (IOException uhe) {
			return "";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					return "";
				}
			}
		}
	}
	
	// TODO: Would be very nice to put some caching around this...
	public static Video getVideo(String videoLocation, String imageSize) {
		VIDEO_TYPE type = VIDEO_TYPE.detect(videoLocation);
		if(type == VIDEO_TYPE.YOUTUBE) {
			return getYoutubeVideo(videoLocation, imageSize);
		}else if(type == VIDEO_TYPE.VIMEO) {
			return getVimeoVideo(videoLocation, imageSize);
		}else {
			return new Video();
		}
	}

	public static Video getVimeoVideo(String videoLocation, String imageSize) {

		String vimeoId = extractVimeoId(videoLocation);
		String randomId = "vimeoPlayer" + String.valueOf(new Random().nextInt(1000) + 1);
		String link = "https://player.vimeo.com/video/" + vimeoId + "?api=1&player_id=" + randomId;
		String title = "";
		String thumbnail = "";
		
		String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
		if (!"".equals(jsonOutput)) {
			try {
				JSONArray json = new JSONArray(jsonOutput);
				if (!json.isNull(0)) {
	                JSONObject snippet = json.getJSONObject(0);
					thumbnail = snippet.getString("thumbnail_large");
					title = snippet.getString("title");
				}
			}catch (JSONException pe) {
				return new Video();
			}
		}
		
		return new Video(imageSize, videoLocation, randomId, thumbnail, link, title, VIDEO_TYPE.VIMEO);
	}

	private static Video getYoutubeVideo(String videoLocation, String imageSize) {
		
		String youtubeId = extractYTId(videoLocation);
		String randomId = "youtubePlayer" + String.valueOf(new Random().nextInt(1000) + 1);
		String link = "https://www.youtube.com/embed/" + youtubeId + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent";
		String title = "";
		String thumbnail = "";
		
		//String jsonOutput = readUrlFile("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + youtubeId + "&key=AIzaSyBMs9oY1vT7DuNXAkGuKk2-5ScGMprtN-Y"); // Use for local testing: AIzaSyBMs9oY1vT7DuNXAkGuKk2-5ScGMprtN-Y
        String jsonOutput = readUrlFile("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + youtubeId + "&key=AIzaSyBLliIIeCT9fzRuejc64WpZN1OJXVu0hsI"); // Use for local testing: AIzaSyBMs9oY1vT7DuNXAkGuKk2-5ScGMprtN-Y
        
        // Get a sample response here: https://developers.google.com/youtube/v3/docs/videos/list					
        if (!"".equals(jsonOutput)) {
			try {
				JSONObject json = new JSONObject(jsonOutput);
		        if (json != null) {
	                JSONObject snippet = json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
	                thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"); // default, medium
					title = snippet.getString("title");
				}
			}catch (JSONException pe) {
				return new Video();
			}
		}
		return new Video(imageSize, videoLocation, randomId, thumbnail, link, title, VIDEO_TYPE.YOUTUBE);
	}
	
}

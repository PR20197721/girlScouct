package org.girlscouts.web.badges.dto;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

/*
 * Expects tags to be structured like this...
 * 
 gsusa:badges/gradelevel/group1/junior
 		/etc/tags/gsusa/badges/gradelevel/group1/junior
 			jcr:title = Junior (Grades 4-5)

 gsusa:badges/topics/group1/entrepreneurship
 		/etc/tags/gsusa/badges/topics/group1/entrepreneurship
 			jcr:title = Entrepreneurship
 gsusa:badges/topics/group2/financialliteracy
 		/etc/tags/gsusa/badges/topics/group2/financialliteracy
 			jcr:title = Financial Literacy
 */

public class BadgeDTO {

	
	private String title, image, description, link, rank, badgeCTA;
	private List<String> otherTags;
	
	public BadgeDTO(Resource badge, ValueMap props) {
		List<String> tags = Stream.of(props.get("cq:tags",String[].class)).map(tag -> tag.replaceAll("gsusa:", "/etc/tags/gsusa/")).collect(Collectors.toList());
		
		rank = getTagTitle(badge.getResourceResolver(), tags.stream().filter(tag -> tag.contains("/gradelevel")).findFirst().orElse("Unknown Grade Level"));
		otherTags = tags.stream().filter(tag -> tag.contains("/topics")).map(tag -> getTagTitle(badge.getResourceResolver(), tag)).collect(Collectors.toList());
		
		title = props.get("dc:title",String.class);
		link = props.get("dc:description",String.class);
		badgeCTA = props.get("badgeCTA", "GET THIS BADGE");
		
		Asset asset = badge.adaptTo(Asset.class);
		Rendition regRendition = asset.getRendition("cq5dam.thumbnail.319.319.png");
		if(regRendition != null && regRendition.getPath() != null){
			image = regRendition.getPath();
		}else{
			image = badge.getPath();
		}
		
		description = props.get("adobe_dam:restrictions",String.class);
		+
	}
	
	private static String getTagTitle(ResourceResolver resolver, String tagPath) {
		return resolver.getResource(tagPath).getValueMap().get("jcr:title", String.class);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public List<String> getOtherTags() {
		return otherTags;
	}

	public void setOtherTags(List<String> otherTags) {
		this.otherTags = otherTags;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	public String getBadgeCTA() {
		return badgeCTA;
	}

	public void setBadgeCTA(String badgeCTA) {
		this.badgeCTA = badgeCTA;
	}
}

package org.girlscouts.web.rest.entity.mulesoft;

import java.util.List;

public class PayloadEntity {

    private String councilCode;

    private boolean memberOnly;

    private String id;

    private String title;

    private List<String> tags;

    private String start;

    private String end;

    private String timezone;

    private String locationLabel;

    private String address;

    private String srchdisp;

    private String details;

    private String image;

    private String color;

    private String register;

    private String visibleDate;

    private String thumbImage;

    private String priceRange;

    public void setCouncilCode(String councilCode){
        this.councilCode = councilCode;
    }
    public String getCouncilCode(){
        return this.councilCode;
    }
    public void setMemberOnly(boolean memberOnly){
        this.memberOnly = memberOnly;
    }
    public boolean getMemberOnly(){
        return this.memberOnly;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTags(List<String> tags){
        this.tags = tags;
    }
    public List<String> getTags(){
        return this.tags;
    }
    public void setStart(String start){
        this.start = start;
    }
    public String getStart(){
        return this.start;
    }
    public void setEnd(String end){
        this.end = end;
    }
    public String getEnd(){
        return this.end;
    }
    public void setTimezone(String timezone){
        this.timezone = timezone;
    }
    public String getTimezone(){
        return this.timezone;
    }
    public void setLocationLabel(String locationLabel){
        this.locationLabel = locationLabel;
    }
    public String getLocationLabel(){
        return this.locationLabel;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }
    public void setSrchdisp(String srchdisp){
        this.srchdisp = srchdisp;
    }
    public String getSrchdisp(){
        return this.srchdisp;
    }
    public void setDetails(String details){
        this.details = details;
    }
    public String getDetails(){
        return this.details;
    }
    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }
    public void setColor(String color){
        this.color = color;
    }
    public String getColor(){
        return this.color;
    }
    public void setRegister(String register){
        this.register = register;
    }
    public String getRegister(){
        return this.register;
    }
    public void setVisibleDate(String visibleDate){
        this.visibleDate = visibleDate;
    }
    public String getVisibleDate(){
        return this.visibleDate;
    }
    public void setThumbImage(String thumbImage){
        this.thumbImage = thumbImage;
    }
    public String getThumbImage(){
        return this.thumbImage;
    }
    public void setPriceRange(String priceRange){
        this.priceRange = priceRange;
    }
    public String getPriceRange(){
        return this.priceRange;
    }

    @Override
    public String toString() {
        return "PayloadEntity{" + "councilCode='" + councilCode + '\'' + ", memberOnly=" + memberOnly + ", id='" + id + '\'' + ", title='" + title + '\'' + ", tags=" + tags + ", start='" + start + '\'' + ", end='" + end + '\'' + ", timezone='" + timezone + '\'' + ", locationLabel='" + locationLabel + '\'' + ", address='" + address + '\'' + ", srchdisp='" + srchdisp + '\'' + ", details='" + details + '\'' + ", image='" + image + '\'' + ", color='" + color + '\'' + ", register='" + register + '\'' + ", visibleDate='" + visibleDate + '\'' + ", thumbImage='" + thumbImage + '\'' + ", priceRange='" + priceRange + '\'' + '}';
    }
}

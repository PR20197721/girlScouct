<div  class="__resource-item columns small-22 small-centered" style="display:none" data-value="<%=meeting.getMeetingPlanType()%>" data-id="<%=meeting.getId()%>" data-path="<%=meeting.getPath()%>">
	<%
        String formattedCats = meeting.getCatTags() ==null ? "" : meeting.getCatTags().replaceAll("_", " ").replaceAll(",", ", ").trim();
        formattedCats = formattedCats.endsWith(",") ? formattedCats.substring(0, formattedCats.length()-1) : formattedCats ;
        boolean isReq = ( meeting.getReq()==null || "".equals(meeting.getReq() ) ) ? false : true;
        String classForModal = isReq ? "_requirement_modal" : "";
    %>
                
    <div class="row">
        <div class="__top columns small-20" <% if(!isReq){ %> style="pointer-events: none" <% } %>>
            <p class="__title" >
                <span >
                    <%=meeting.getName()%>
                </span> <span <% if(!isReq){ %> style="display: none" <% } %> 
                    class="arrow close"></span>
            </p>
            
            <p class="__description">
                <%= meeting.getBlurb() %>
            </p><br />
            
            <p>
                <b>
                    <%= formattedCats %>
                </b>
            </p>
        </div>

        <div class="__image columns small-4">
            <div>
                <img 
                    class="<%= classForModal %>"  
                    width="60px" 
                    height="60px" 
                    <% if(isReq) { %> onclick="afterClick(this)" <% } %>
                    src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=meeting.getId()%>.png"
                />
            </div>
        </div>

    </div>


    <div class="row __view_meeting_details" style="display:none; padding-top:20px; background-color:#f9f9f9" id="view_meeting_details_<%=meeting.getId()%>">
        <div  class="columns small-20 medium-21">
                <% if(isReq){ %>
                <div class="row">
                    <div class="columns small-24">
                        <p style="margin-bottom:5px">
                            <b>
                                <%= meeting.getReqTitle() %>
                            </b>
                        </p>
                         <%= meeting.getReq() %>
                    </div>
                </div>
                <% } %>

                <!-- <div class="row">
                    <div class="columns small-2">
                        <a href="javascript:void(0)" onclick="window.open('/content/girlscouts-vtk/controllers/vtk.pdfPrintMeetingOverview.html?meetingPath=<%=meeting.getPath()%>')">
                            <i style="color:#00AE58; width:25px; height:25px" class="icon-pdf-file-extension">
                            </i>
                        </a>
                    </div>

                    <div class="columns small-22" id="__content-<%=meeting.getId()%>">
                    </div>
                </div> -->

                <% if(isReq){ %>
                    <div class="row">
                        <p class="_close" style="text-align:center;color:#00ae58">
                          less <span onclick="afterClick(this)" class="arrow close up"></span>
                        </>
                    </div>
                <% } %>
        </div>            
    </div>
    
</div>
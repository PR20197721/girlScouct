<div class="__overview-types row">

	<div class="columns small-24">
		<h5 style="font-weight:bold;margin-bottom:20px;">
			Select the type of meetings you'd like to see
		</h5>
	</div>
	<div class="__list-radio columns small-24 medium-19 end">
		<%for( String meetingPlayType: meetingPlanTypes){ %>
			<div class="small-24 medium-12 large-8 columns">
				<input  type="radio" name="meetingTypes" id="<%=meetingPlayType%>" value="<%=meetingPlayType%>" onclick="showMeetingTypes('<%=meetingPlayType%>')" />
				<label for="<%=meetingPlayType%>"><span></span><p> <%=meetingPlayType.replace("_"," ")%> (<%=sortMeetingByMeetingType.get(meetingPlayType).size()%>) </p></label>
			</div>
		<%}%>
		
		<div class="small-24 medium-12 large-8 columns">
			<%if(meetingPlanTypes.size()>1){%>
				<input type="radio" name="meetingTypes" id="all" value="" onclick="showAllMeetings()" />
				<label for="all"><span></span><p> All (<%=raw_meetings.size()%>) </p></label>
			<%}%>
		</div>
	</div>
</div>




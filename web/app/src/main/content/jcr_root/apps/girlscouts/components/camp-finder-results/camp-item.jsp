<div class="result">
    <div class="row">
        <h5 class="columns large-17 medium-17 small-24"><a href="{{Website}}" target="_blank">{{CampName}}</a></h5>
        <i class="columns large-6 medium-6 small-24">{{Distance}} from {{../env.zip}}</i>
    </div>
    <p>Council Name: {{CouncilName}}</p>
    <p>Camp Location: {{CampCity}}, {{CampState}}</p>
    <p>Camp Dates: {{DateDescription}}</p>
    <p>Length of Sessions: {{SessionDescription}}</p>
    {{#if Fee}}
	    <p>Fees: {{Fee}}</p>
    {{/if}}
    {{#if GradeDescription}}
    	<p>Grades the Camp Serves: {{GradeDescription}}</p>
    {{/if}}
    {{#if Website}}
	    <p>Website: <a href="{{Website}}" target="_blank">{{Website}}</a></p>
    {{/if}}
	{{#if (or Emails Phone)}}
	    <p>Contact Information: {{{Emails}}}   
		{{#if (and Emails Phone)}}
		    or 
    	{{/if}}
	    {{Phone}}</p>
    {{/if}}
    
    {{#if CampDescription}}
	    <div class="more-section">
    	    <section style="display:none;">
        	 <p>{{CampDescription}}</p>
	        </section>
    	    <a title="read more" class="read-more">Read More</a>
	    </div>
	{{else}}
		<div class="more-section"></div>
    {{/if}}
</div>
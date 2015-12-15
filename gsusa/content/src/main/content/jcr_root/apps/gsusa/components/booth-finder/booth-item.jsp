<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"%>

<div class="row details">
	<div class="detail clearfix">
	    <section>
	        <p>{{Location}}</p>
	        <p>{{Address1}}</p>
	        <p>{{Address2}}</p>
	        <p>{{City}}, {{State}} {{ZipCode}}</p>
	    </section>
	    <section>
	        <p>{{DateStart}}</p>
	        <p>{{TimeOpen}} - {{TimeClose}}</p>
	    </section>
	    <section>
	        <p>{{Distance}} Miles</p>
	    </section>
	</div>
	<div class="clearfix right">
	    <a class='viewMapA{{uid}} button'>View Details</a>
	</div>
</div>
<%@include file="/libs/foundation/global.jsp"%>

<%@ page import="com.day.cq.mailer.MessageGatewayService, 
    			org.apache.commons.mail.HtmlEmail, 
				java.util.ArrayList, javax.mail.internet.InternetAddress"
				%>

<cq:include script="/libs/wcm/core/components/init/init.jsp" />

<h1 id="page_title">Contact Us</h1>
<br/>

<cq:includeClientLib categories="apps.girlscouts" />


<h2 id="page_subtitle">We'd love to hear from you</h2>

<cq:include path="form_start" resourceType="foundation/components/form/start" />

<cq:include path="input_fields" resourceType="foundation/components/parsys" />

<cq:include path="form_end" resourceType="foundation/components/form/end" />

<br/>

<h3 id="bottomocontent_title"> Visit one of our Service Centers </h3>
<div id="bottomcontent_text">
    <p>
    For your convenience, the council has a Girl Scout shop at each service center.
        You'll find all the basic Girl Scouts supplies as well as other accessories.
    </p>
    
    <p>
    Metropolis Service Center 1234 Main Street Anytown, ST 56789 Fax: 123-456-7890
    </p>
    
    <p>
        Ph: 1-123-456-7890
    </p>
    
    <p>
    Greenville Service Center 2345 2nd Ave. Anytown, ST 56789 Fax: 123-456-7890
    </p>
    <p>
        Ph: 1-123-456-7890
    </p>
    
    <p>
    Hours
    </p>
    <p>
    Monday 8:30 AM - 7:00 PM
    </p>
    <p>
       Tuesday through Friday 8:30 AM - 4:30 PM
    </p>
    <br/>
    
    <p>
    Open select Saturdays: 9:00 AM - 12:00 PM
    </p>
    
    <ul>
        <li>May 4</li>
        <li>June 1</li>
        <li>August 3</li>
        <li>September 7</li>
        <li>October 5</li>
        <li>November 2</li>
        <li>December 7</li>
    </ul>    
</div>





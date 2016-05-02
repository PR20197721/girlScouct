<%@include file="/libs/foundation/global.jsp" %>
<cq:includeClientLib categories="granite.csrf.standalone"/>


<form action="#" method="post">
<input type="hidden" name="action" value="trigger"/>
This button will trigger the ftp event integration. If you do not know the system, please be cautious!
This button should not be used until the ftp instance is disconnected.
<br>

<input type="submit" value="Submit">
</form>

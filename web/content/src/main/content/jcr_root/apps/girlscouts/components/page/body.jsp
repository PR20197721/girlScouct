<%@include file="/libs/foundation/global.jsp" %>
Hello World! This is Girl Scouts.
<cq:include path="par" resourceType="foundation/components/parsys"/>
Testing if <a href="http://foundation.zurb.com/">Foundation</a> is working.
<cq:includeClientLib categories="apps.girlscouts" />
<script type="text/javascript">
	$(document).foundation();
</script>
<%@include file="/libs/foundation/global.jsp"%>
<html><head><title>Migrate Join / Volunteer / Renew Link </title>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
    <cq:includeClientLib categories="granite.csrf.standalone"/>
</head>
<body>
<cq:include script="form.jsp"/>
<br/>
<input id="start" type="button" onclick="$.post('<%= resource.getPath() %>', $('#form').serialize(), function(data) { $('#status').html(data);}); return false;" value="start"/>
| <input id="stop" type="button" onclick="$.post('<%= resource.getPath() %>', {cmd: 'stop'}, function(data) { $('#status').html(data);}); return false;" value="stop"/>
| <input id="getstatus" type="button" onclick="$.post('<%= resource.getPath() %>', {cmd: 'status'}, function(data) { $('#status').html(data);}); return false;" value="Get Status" />
<div id="status">

</div>
</body>
</html>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%!


%>
<span id="pdfGen">
    <a id="pdfLink" style="margin-left:6px;" pagepath = "<%=currentPage.getPath()%>.pdfrender.html">Generate PDF</a>
</span>
<script>
    $("#pdfGen").on('click', function(){
        $CQ.ajax({
            url: '/etc/servlets/pdf/page-pdf.html',
            type: 'POST',
            data: {
                pageHtml: $("#mainContent").html(),
                path: $("#pdfLink").attr("pagepath")
            },
            success: function(data){
                var blob=new File([this.response], filename, {type: "application/pdf"});
                var URL = window.URL || window.webkitURL;
                var downloadUrl = URL.createObjectURL(blob);
                window.open(downloadUrl);
                //var win = window.open();
                //win.document.write(data);
            }
        });
    });
</script>
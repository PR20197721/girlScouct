<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%!


%>
<span id="pdfGen">
    <a id="pdfLink" style="margin-left:6px;" pagepath = "<%=currentPage.getPath()%>.pdfrender.html" title="<%=currentPage.getTitle()%>">Generate PDF</a>
</span>
<script>
    function buildPdfHtml(){
        var html = "";
        $("#mainContent .par").children().each(function(index){
            if(!$(this).attr("class").includes("image") && $(this).text().trim() != ""){
                var tag = $(this).prop("tagName").toLowerCase()
                html = html + "<" + tag + ">";
                html = html + $(this).text().trim();
                html = html + "</" + tag +">";
                html = html + ";";
            }
        });
        return html;
    }
    $("#pdfGen").on('click', function(){
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/etc/servlets/pdf/page-pdf.html', true);
        xhr.responseType = 'arraybuffer';
        var returner = new Promise(function(res, rej) {
            xhr.onload = function () {
                if (this.status === 200) {
                    var filename = "PagePdf";
                    var type = "application/pdf";
                    var blob;
                    try{
                        blob = new File([this.response], filename, {type: type})
                    }catch(err){
                        // IE / Safari dont' like creating files.
                        blob = new Blob([this.response], {type: type});
                    }
                    //IE workaround
                    if (typeof window.navigator.msSaveBlob !== 'undefined') {
                        // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                        window.navigator.msSaveBlob(blob, filename + '.pdf');
                    } else{
                        var URL = window.URL || window.webkitURL;
                        var downloadUrl = URL.createObjectURL(blob);
                        window.open(downloadUrl);
                    }
                    res();
                }else{
                    console.log("Pdf generation failed")
                    rej();
                }
            };
        });
        var html = buildPdfHtml();
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send($.param({pageHtml: html, path: $("#pdfLink").attr("pagepath"), title: $("#pdfLink").attr("title")}));
        return returner;

    });
</script>
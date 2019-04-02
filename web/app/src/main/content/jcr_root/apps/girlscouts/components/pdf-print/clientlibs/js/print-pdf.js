function buildChildren(el){
    var html = "";
    el.children().each(function(index){
         if($(this).prop("tagName").toLowerCase() !== "cq"  && $(this).prop("tagName").toLowerCase() !== "script" && $(this).html().trim() != ""){
            var tag = $(this).prop("tagName").toLowerCase();
             html = html + "<" + tag + ">";
             if(($(this).children("img").length > 0) || ($(this).children().length > 0 && tag ==="li") || ($(this).children("a").length > 0)){
                  html = html + $(this).html().trim();
              }
             else if($(this).children().length > 0 && tag !=="p"){
                 html = html + buildChildren($(this));
             }
             else{
                 html = html + $(this).html().trim();
             }
             html = html + "</" + tag +">";
         }

    });
    return html;
}
function buildPdfHtml(){
    var html = "";
    $("#mainContent .par").children().each(function(index){
        $(this).inlineStyler();
        if($(this).prop("tagName").toLowerCase() !== "cq" && $(this).prop("tagName").toLowerCase() !== "script" && !$(this).attr("class").includes("title") && $(this).html().trim() != ""){
            var tag = $(this).prop("tagName").toLowerCase();
            var styles = $(this).attr("style");
            if(styles.length > 0){
                html = html + "<" + tag + " style='"+styles+"'>";
            }else
                html = html + "<" + tag + ">";
            //If element has child nodes.
            if($(this).children().length > 0){
                html = html + buildChildren($(this));
            }
            else{
                html = html + $(this).html().trim();
            }

            html = html + "</" + tag +">";
            html = html + "~";
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
    var html = encodeURI(buildPdfHtml());
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded; charset=utf-8');
    xhr.send($.param({pageHtml: html, path: $("#pdfLink").attr("pagepath"), title: $("#pdfLink").attr("title")}));
    return returner;

});
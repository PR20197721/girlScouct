//Iterate through children and build html + styles, recursive
function buildChildren(el){
    var html = "";
    el.children().each(function(index){
         if($(this).prop("tagName").toLowerCase() !== "cq"  && $(this).prop("tagName").toLowerCase() !== "script" && $(this).html().trim() != "" && $(this).attr("id") !== "pdfGen"){
            var tag = $(this).prop("tagName").toLowerCase();
             var styles = $(this).attr("style");
             if(typeof styles !== typeof undefined && styles.length > 0){
                 html = html + "<" + tag + " style='"+styles+"'>";
             }else
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
//Iterate through initial elements under .mainContent .Par, build elements html as well as children's html
function buildPdfHtml(){
    var html = "";
    var mainContent = $("#mainContent .par");
    mainContent.find(".title").css("color","#008000");
    mainContent.children().each(function(index){
        var element = $(this).clone();
        element.inlineStyler();
        $(this).find("a").each(function(){
            $(this).css("background-color", "white");
            $(this).css("text-decoration", "none");
            $(this).css("color","#00ae58");
        });
        if($(this).prop("tagName").toLowerCase() !== "cq" && $(this).prop("tagName").toLowerCase() !== "script"  && $(this).html().trim() != ""){
            var tag = $(this).prop("tagName").toLowerCase();
            var styles = element.attr("style");
            if(typeof styles !== typeof undefined && styles.length > 0){
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
    //Add image size attributes
    $("#mainContent").find("img").each(function(){
        $(this).attr("height", this.clientHeight)
        $(this).attr("width", this.clientWidth)
    });
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/etc/servlets/pdf/page-pdf.html', true);
    xhr.responseType = 'arraybuffer';
    var returner = new Promise(function(res, rej) {
        xhr.onload = function () {
            if (this.status === 200) {
                var filename = "GirlScoutsGeneratedPdf";
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
    xhr.send($.param({pageHtml: html, path: $("#pdfLink").attr("pdfpath"), title: $("#pdfLink").attr("title")}));
    return returner;

});
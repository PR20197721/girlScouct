$(document).ready(function() {
    var cPaths = $('#pagecount').data('councilPaths');
    cPaths = cPaths.slice(1, -1).split(","); 
    looper = $.Deferred().resolve();

    $.each(cPaths, function(i, data) {
        looper = looper.then(function() {
            return $.ajax({
                method: 'GET',
                url: cPaths[i].trim(),
                timeout: 5000,
                dataType: 'html',
                beforeSend : function(){
                    $(".pagesList:last").after('<div class="button_loader"></div>');
                    $('.button_loader').after('<div id="spinner_text">loading...</div>');
                }

            })
            .done(function(response) {
                var sections = response.split("###");
                var listData = "<!-- " + sections[1] + sections[2] + sections[3] + sections[4] + " -->";
                var tableData = "<!-- " + sections[6] + " -->";
                $("tbody").append(tableData);
                $('td:first-child').each(function() {
                    title = $(this).text();
                });
                $(".pagesList").append("<h3>" + title + "</h3>");
                $(".pagesList").append(listData);
                $(".pagelist").hide();
				        $(".button_loader").remove();
                $("#spinner_text").remove();

            })
            .fail(function(jqXHR, textStatus, errorThrown ){
                if(textStatus === "timeout")
                    console.log('request timed out');
            })
        });
    });

});

$(document).on('click', '.showlist', function(e) {
    toggle(this.id);
})

function toggle(id) {
    var text = $("#" + id).text();
    if (text == "Show List") {
        $("#" + id).text("Show less");
        $("#" + id + "List").show();
    } else {
        $("#" + id).text("Show List");
        $("#" + id + "List").hide();
    }
}

function copy() {
    var emailLink = document.querySelector('#pagelistTable');
    var range = document.createRange();
    range.selectNode(emailLink);
    window.getSelection().addRange(range);

    try {
        var successful = document.execCommand('copy');
        var msg = successful ? 'Copied. Please paste to excel' : 'Copy failed. Please copy manually';
        alert(msg);
    } catch (err) {
        alert('Oops, unable to copy');
    }

    window.getSelection().removeAllRanges();
}
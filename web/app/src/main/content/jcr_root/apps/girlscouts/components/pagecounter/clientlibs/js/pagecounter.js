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

$(document).ready(function () {
    $(".pagelist").hide();
    $(".showlist").click(function () {
        toggle(this.id);
    });
});

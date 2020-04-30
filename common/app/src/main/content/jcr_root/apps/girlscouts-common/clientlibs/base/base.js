//GSDO-1030-Fix for Anchor Scrolling with base tag
$(window).on('load', function() {
    // if runmode is not author
    if (!($('#currentPagePath').data('run-mode'))) {
        var hashObj = $('body a[href^="#"]');
        hashObj.each(function() {
            var newhref = $('#currentPagePath').data('page-path') + ".html" + $(this).attr('href');
            $(this).attr('href', newhref);
        });
    }
});
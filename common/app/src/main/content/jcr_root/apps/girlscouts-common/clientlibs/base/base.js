//GSDO-1030-Fix for Anchor Scrolling with base tag
$(window).on('load', function() {
    // if runmode is not author
    if (!($('#currentPagePath').data('run-mode'))) {
        console.log("Does anchor tag href starts with '#' ?"+ $('body a[href^="#"]'));
        var hashObj = $('body a[href^="#"]');
        hashObj.each(function() {
            console.log("PAGEPATH:"+$('#currentPagePath').data('page-path') + ".html" + $(this).attr('href'));
            var newhref = $('#currentPagePath').data('page-path') + ".html" + $(this).attr('href');
            $(this).attr('href', newhref);
        });
    }
});
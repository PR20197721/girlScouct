// State field is mandatory is US is selected.
$(document).ready(function() {
    $('select[name="Country"]').on('change', function(){
        var star = $($('select[name="State"]').parent().parent().find('.form_leftcolmark')[0]);

        if ($(this).val() == 'US') {
            star.show();
        } else {
            star.hide();
        }
    });
});
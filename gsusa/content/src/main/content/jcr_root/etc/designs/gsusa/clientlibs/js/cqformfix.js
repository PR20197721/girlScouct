// form fix for error message line spacing.
// Please check error message of the permission-request form for its effect
$('.form_error').prev().hide();

//State field is mandatory is US is selected.
$(document).ready(function() {
    var countryField = $('select[name="Country"]');
    var stateField = $('select[name="State"]');
    var star = $(stateField.parent().parent().find('.form_leftcolmark')[0]);

    function adjustStateField() {
        var stateVal = stateField.val();
        if (countryField.val() == 'US') {
        	stateField.children().first().attr('value', '');
            if (stateVal == undefined || stateVal == null || stateVal == 'N/A') {
                stateField.val('');
            }
            star.show();
        } else {
        	stateField.children().first().attr('value', 'N/A');
            if (stateVal == undefined || stateVal == null || stateVal == '') {
                stateField.val('N/A');
            }
            star.hide();
        }
    }

    countryField.on('change', function(){
    	adjustStateField();
    });
    adjustStateField();
});
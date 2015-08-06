// form fix for error message line spacing.
// Please check error message of the permission-request form for its effect
$('.form_error').prev().hide();

//State field is mandatory is US is selected.
$(document).ready(function() {
    var countryField = $('select[name="Country"]');
    var stateField = $('select[name="State"]');
    var zipField = $('select[name="zip"]');

    function adjustField() {
        var stateVal = stateField.val();
        var zipVal = zipField.val();
        if (countryField.val() == 'US') {
        	stateField.children().first().attr('value', '');
            if (stateVal == undefined || stateVal == null || stateVal == 'N/A') {
                stateField.val('');
            }
            if (zipVal == undefield || zipVal == null || zipVal == 'N/A') {
            	zipField.val('');
            }
        } else {
        	stateField.children().first().attr('value', 'N/A');
            if (stateVal == undefined || stateVal == null || stateVal == '') {
                stateField.val('00000');
            }
            if (zipVal == undefield || zipVal == null || zipVal == '') {
            	zipField.val('');
            }
        }
    }

    countryField.on('change', function(){
    	adjustField();
    });
    adjustField();
});
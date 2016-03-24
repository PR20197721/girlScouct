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
            if (zipVal == undefined || zipVal == null || zipVal == '00000') {
                zipField.val('');
            }
            $('input.GS-state-field-hidden').remove();
            $('input.GS-zip-field-hidden').remove();
        } else {
            stateField.children().first().attr('value', 'N/A');
            countryField.after('<input type="hidden" class="GS-state-field-hidden" name="State" value="N/A">');
            countryField.after('<input type="hidden" class="GS-zip-field-hidden" name="zip" value="00000">');
        }
    }

    countryField.on('change', function(){
        adjustField();
    });
    adjustField();
});

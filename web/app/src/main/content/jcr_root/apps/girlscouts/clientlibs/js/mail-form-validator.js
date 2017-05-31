
CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormEmailFieldValidator : function(v, f) {
		return CQ.Ext.form.VTypes.email(v);
	}
});

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormConfSubjectFieldValidator : function(v, f) {
		if(isConfirmationEmailSet()){
			if(v == null || v.trim().length < 1)
			return false;
		}
		return true;
	}
});

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormConfMailFromFieldValidator : function(v, f) {
	if(isConfirmationEmailSet()){
		return CQ.Ext.form.VTypes.email(v);
	}
	return true;
	}
});

function validateMailtoMultiField(field){
	if((field.items.getCount()-1) < 1){
		field.markInvalid();
		return false;
	}
	field.clearInvalid();
	return true;
}

function isConfirmationEmailSet(){
	var confEmailCheckBoxes = $(".form_row [id^=confirmation_email_]:input");
	if(confEmailCheckBoxes != null && confEmailCheckBoxes.length > 0){
		for(i=0;i<confEmailCheckBoxes.length;i++){
			if(confEmailCheckBoxes[i].value == "true"){
				return true;
			}
		}
	}
	return false;
}
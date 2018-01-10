
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
	var textfields = field.findByType("textfield");
	if(textfields == null || textfields.length < 1){
		field.addItem();
		textfields = field.findByType("textfield");
		for(i=0; i<textfields.length; i++){
			if(!textfields[i].isValid()){
				return false;
			}
		}
	}
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

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	fileUploadSizeLimitValidator : function(v, f) {
		if(v != null && v.trim().length > 0){
			return CQ.Ext.form.VTypes.digits(v);
		}else{
			return true;
		}
	}
});
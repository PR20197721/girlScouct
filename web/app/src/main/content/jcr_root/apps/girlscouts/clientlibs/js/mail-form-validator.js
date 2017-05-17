
CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormEmailFieldValidator : function(v, f) {
		return CQ.Ext.form.VTypes.email(v);
	}
});

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormSubjectFieldValidator : function(v, f) {
		if(v == null || v.trim().length < 1)
			return false;
		else return true;
	}
});

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormConfSubjectFieldValidator : function(v, f) {
		var confCheckbox = f.findParentByType("dialog").getField("./disableConfirmation");
		if(typeof confCheckbox != 'undefined'){
			var isConfDisabled = confCheckbox.checked;
			if(!isConfDisabled){
				if(v == null || v.trim().length < 1)
				return false;
			}
		}
		return true;
	}
});

CQ.Ext.apply(CQ.Ext.form.VTypes, {
	mailFormConfMailFromFieldValidator : function(v, f) {
		var confCheckbox = f.findParentByType("dialog").getField("./disableConfirmation");
		if(typeof confCheckbox != 'undefined'){
			var isConfDisabled = confCheckbox.checked;
			if(!isConfDisabled){
				return CQ.Ext.form.VTypes.email(v);
			}
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

function validateConfMailtoMultiField(field){
	var confCheckbox = field.findParentByType("dialog").getField("./disableConfirmation");
	if(typeof confCheckbox != 'undefined'){
		var isConfDisabled = confCheckbox.checked;
		if(!isConfDisabled && field.items.getCount() <=1){
			field.markInvalid();
			return false;
		}
	}
	field.clearInvalid();
	return true;
}
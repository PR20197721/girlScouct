gsusa.functions.ToggleParsys = function(componentPath) {
    this.componentPath = componentPath;

    this.setComponentPath = function(componentPath) {
        this.componentPath = componentPath;
    }

    this.hideParsys = function() {
        if (this.componentPath) {
            var parsysComp = CQ.WCM.getEditable(this.componentPath);

            if (parsysComp) {
                parsysComp.hide();
            }
        }
    };

    this.showParsys = function() {
        if (this.componentPath) {
            var parsysComp = CQ.WCM.getEditable(this.componentPath);

            if (parsysComp) {
                parsysComp.show();
            }
        }
    };
}

gsusa.functions.ToggleParsysAll = {};
gsusa.functions.ToggleParsysAll.refs = new Array();
gsusa.functions.ToggleParsysAll.toggleAll = function(show) {
	var refs = gsusa.functions.ToggleParsysAll.refs;
    for (var i = 0; i < refs.length; i++) {
    	var ref = refs[i];
        if (show) {
           ref.showParsys();
        } else {
            ref.hideParsys();
        }
    }
};
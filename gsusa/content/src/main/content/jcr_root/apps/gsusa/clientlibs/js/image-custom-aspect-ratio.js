/*
 * Copyright 1997-2010 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
gsusa.components.Html5SmartImageAspectRatio = CQ.Ext.extend(CQ.html5.form.SmartImage, {
	toolClicked: function(tool) {
        var prevTool;
        var toolButton = tool.buttonComponent;
        if (toolButton.pressed) {
            var isFirstTimeCall = false;
            if (this.toolComponents == null) {
                this.toolComponents = { };
            }
            if (!this.toolComponents[tool.toolId]) {
                this.toolComponents[tool.toolId] = {
                    isVisible: false,
                    toolRef: tool
                };
                isFirstTimeCall = true;
            }
            var toolDef = this.toolComponents[tool.toolId];
            // hide all other tools' components
            prevTool = this.hideTools(tool.toolId);
            // render (if necessary) and show tools' components
            if (tool.userInterface && (!tool.userInterface.rendered)) {
                tool.userInterface.render(CQ.Util.getRoot());
            }
            if (prevTool) {
                prevTool.onDeactivation();
            }
            if (tool.userInterface) {
                tool.userInterface.show();
                toolDef.isVisible = true;
                if (!(tool.userInterface.saveX && tool.userInterface.saveY)) {
                    var height = tool.userInterface.getSize().height;
                    var pos = this.getPosition();
                    var toolbarPosX = pos[0];
                    var toolbarPosY = pos[1] - (height + 4);
                    if (toolbarPosX < 0) {
                        toolbarPosX = 0;
                    }
                    if (toolbarPosY < 0) {
                        toolbarPosY = 0;
                    }
                    tool.userInterface.setPosition(toolbarPosX, toolbarPosY);
                } else {
                    tool.userInterface.setPosition(
                            tool.userInterface.saveX, tool.userInterface.saveY);
                }
            }
            tool.onActivation();
        } else {
            prevTool = this.hideTools();
            if (prevTool) {
                prevTool.onDeactivation();
            }
            this.imagePanel.drawImage();
        }
        if (tool.toolId === "smartimageCrop") {
	    	tool.userInterface.changeAspectRatio(116,100);
	    }
    },
    getCropData: function() {
    	if (this.toolComponents != null) {
	    	if (this.toolComponents["smartimageCrop"] != null) {
	    		console.info(this.toolComponents["smartimageCrop"]);
	    		return this.toolComponents["smartimageCrop"].toolRef.serialize();
	    	}
	    } else {
	    	return null;
	    }
    }

});

CQ.Ext.reg("html5smartimageAR", gsusa.components.Html5SmartImageAspectRatio);

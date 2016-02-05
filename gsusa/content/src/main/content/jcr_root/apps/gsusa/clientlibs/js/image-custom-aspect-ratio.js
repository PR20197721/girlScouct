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
	
    crops: {},
    ui: {},

    constructor: function (config) {
        config = config || {};
 
        var aRatios = {
            "2To1AspectRatio": {
            	"value": "1.166,1",
            	"text": "1.166:1",
                "checked": true
            }
        };
 
        var defaults = { "cropConfig": { "aspectRatios": aRatios } };
        config = CQ.Util.applyDefaults(config, defaults);
 
        gsusa.components.Html5SmartImageAspectRatio.superclass.constructor.call(this, config);
    },
 
    initComponent: function () {
        gsusa.components.Html5SmartImageAspectRatio.superclass.initComponent.call(this);
 
        var imgTools = this.imageToolDefs;
        var cropTool;
 
        if(imgTools){
            for(var x = 0; x < imgTools.length; x++){
                if(imgTools[x].toolId == 'smartimageCrop'){
                    cropTool = imgTools[x];
                    break;
                }
            }
        }
 
        if(!cropTool){
            return;
        }
 
        for(var x in this.crops){
            if(this.crops.hasOwnProperty(x)){
                var field = new CQ.Ext.form.Hidden({
                    id: x,
                    name: "./" + x
                });
 
                this.add(field);
 
                field = new CQ.Ext.form.Hidden({
                    name: "./" + x + "Text",
                    value: this.crops[x].text
                });
 
                this.add(field);
            }
        }
 
        var userInterface = cropTool.userInterface;
        ui = userInterface;
 
        this.on("loadimage", function(){
            if ( typeof ui.cropRect !== "undefined") {
                ui.onRatioChanged("1,1.1667", "1.1667");
            }
        });



        cropTool.workingArea.on("contentchange", function(changeDef){
            var aRatios = userInterface.aspectRatioMenu.findByType("menucheckitem");
            var aRatioChecked;
            
            if(aRatios){
                for(var x = 0; x < aRatios.length; x++){
                    if(aRatios[x].checked === true){
                        aRatioChecked = aRatios[x];
                        break;
                    }
                }
            }
 
            if(!aRatioChecked){
                return;
            }
        }, this);
    },
 
    getCropKey: function(text){
        for(var x in this.crops){
            if(this.crops.hasOwnProperty(x)){
                if(this.crops[x].text == text){
                    return x;
                }
            }
        }
 
        return null;
    },
 
    getRect: function (radio) {
        var ratioStr = "";
        var aspectRatio = "1.166,1"; //hardcoding it
 
        if ((aspectRatio != null) && (aspectRatio != "0,0")) {
            ratioStr = "/" + aspectRatio;
        }
 
        if (ui.cropRect == null) {
            return ratioStr;
        }
 
        return ui.cropRect.x + "," + ui.cropRect.y + "," + (ui.cropRect.x + ui.cropRect.width) + ","
            + (ui.cropRect.y + ui.cropRect.height) + ratioStr;
    },
 
    setCoords: function (cropTool, cords) {
        cropTool.initialValue = cords;
        cropTool.onActivation();
    }
});

CQ.Ext.reg("html5smartimageAR", gsusa.components.Html5SmartImageAspectRatio);


CQ.Ext.ns("ExperienceAEM.ImageMultiField");

ExperienceAEM.ImageMultiField.Panel = CQ.Ext.extend(CQ.Ext.Panel, {
    initComponent: function () {
        ExperienceAEM.ImageMultiField.Panel.superclass.initComponent.call(this);

        var multifield = this.findParentByType('imagemultifield'),
            image = this.find('xtype', 'imagemultifieldsmartimage')[0],
            smrtImage = this.find('xtype', 'smrtimage'),
            imageName = multifield.nextImageName, itemName,
            changeParams = ["cropParameter", "fileNameParameter","fileReferenceParameter",
                "mapParameter","rotateParameter" ];
        
       
        if(!imageName){
            imageName = image.name;

            if (!imageName) {
                imageName = "demo";
            } else if(imageName.indexOf("./") === 0) {
                imageName = imageName.substr(2); //get rid of ./
            }

            multifield.nextImageNum = multifield.nextImageNum + 1;
            imageName = this.name + "/" + imageName + "-" + multifield.nextImageNum;
        }
        image.name = imageName;
        
        CQ.Ext.each(changeParams, function(cItem){
            if(image[cItem]){
                image[cItem] = imageName + "/" +
                    ( image[cItem].indexOf("./") === 0 ? image[cItem].substr(2) : image[cItem]);
            }
        });

        CQ.Ext.each(image.imageToolDefs, function(toolDef){
            toolDef.transferFieldName = imageName + toolDef.transferFieldName.substr(1);
            toolDef.transferField.name = toolDef.transferFieldName;
        });
        
        
        
        CQ.Ext.each(this.items.items, function(item)
        {
            if(image.xtype == item.xtype){ //skip the imagemultifieldsmartimage we already handled its name
                return;
            }
            /*if(item.xtype == "smrtimage"){
            	return;
            }*/
            itemName = item.name ? item.name : item.id;
            item.name = imageName + "/" + ( itemName.indexOf("./") === 0 ? itemName.substr(2) : itemName ) ;
        });
        
        this.add(new CQ.Ext.form.Hidden({
            name: image.name + "/sling:resourceType",
            value: image.imageSlingResourceType
           // name: smrtImage.name+"/sling:resourceType",
            //value: smrtImage.imageSlingresourceType
        }));
        
      /*  CQ.Ext.each(smrtImage,function(s){
        	
        	console.log("What is the name of the image" +s.name);
        	console.log("what is the path of the image" +s.fileReferenceParameter);
        	smrtImageName = multifield.nextImageName, itemName
        	/*if(!smrtImageName){
        		smrtImageName = s.name;

                if (!smrtImageName) {
                	smrtImageName = "demo";
                } else if(smrtImageName.indexOf("./") === 0) {
                	smrtImageName = smrtImageName.substr(2); //get rid of ./
                }

                multifield.nextImageNum = multifield.nextImageNum + 1;
                smrtImageName = this.name + "/" + imageName + "-" + multifield.nextImageNum;
            }*/
           /* s.name = smrtImageName;
            
            CQ.Ext.each(changeParams, function(cItem){
            	c = cItem;
                if(s[cItem]){
                    s[cItem] = smrtImageName + "/small-image" + "/" +
                        ( s[cItem].indexOf("./") === 0 ? s[cItem].substr(2) : s[cItem]);
                }
            });

            CQ.Ext.each(s.imageToolDefs, function(toolDef){
                toolDef.transferFieldName = smrtImageName + toolDef.transferFieldName.substr(1);
                toolDef.transferField.name = toolDef.transferFieldName;
            });
            this.add(new CQ.Ext.form.Hidden({
                name: s.name + "/sling:resourceType",
                value: s.imageSlingResourceType
               // name: smrtImage.name+"/sling:resourceType",
                //value: smrtImage.imageSlingresourceType
            }));
            
        	
        	
        	
        });*/
   
        
    },

   setValue: function (record) {
        var multifield = this.findParentByType('imagemultifield'),
            image = this.find('xtype', 'imagemultifieldsmartimage')[0],
            smrtimage = this.find('xtype', 'smrtimage'),
            recCopy = CQ.Util.copyObject(record), value,
            imagePath = multifield.path + "/" + image.name,
            imgRec = recCopy.get(image.name), x, fileRefParam;
            
    
            if(imgRec){
            CQ.Ext.each(this.items.items, function(item){
                if(image.xtype == item.xtype){
                    return;
                }
                value = imgRec[item.name.substr(item.name.lastIndexOf("/") + 1)];
                if(value){
                    item.setValue(value);
                }
            	});
            }
            for(x in imgRec){
            	if(imgRec.hasOwnProperty(x)){
            		recCopy.data[x] = imgRec[x];
            	}
            }
            recCopy.data[this.name.substr(2)] = undefined;
            fileRefParam = image.fileReferenceParameter;
            console.log("fileRefParam" +fileRefParam);
	        
	        image.fileReferenceParameter = fileRefParam.substr(fileRefParam.lastIndexOf("/") + 1);
	        image.processRecord(recCopy, imagePath);
	        image.fileReferenceParameter = fileRefParam;
	        
	        CQ.Ext.each(smrtimage, function(simage){
	        	smrtimagePath = multifield.path + "/" + simage.name;
	        	smrtfileRefParam = simage.fileReferenceParameter;
	        	smrtImgRec = recCopy.get(simage.name), x, fileRefParam; 
	        	
	            recCopy.data[this.name.substr(2)] = undefined;
	            console.log("smrtfileRefParam" +smrtfileRefParam);
	            simage.fileReferenceParameter = smrtfileRefParam.substr(smrtfileRefParam.lastIndexOf("/"));
	            console.log("smrtimage.fileReferenceParameter" +simage.fileReferenceParameter);
	            temp = smrtimagePath.substr(0,smrtimagePath.lastIndexOf("/"));
	            simage.processRecord(recCopy, temp);
	            simage.fileReferenceParameter = temp+smrtfileRefParam.substr(smrtfileRefParam.lastIndexOf("/"));
	        	
	        });
	      },

    validate: function(){
        return true;
    }
});

CQ.Ext.reg("imagemultifieldpanel", ExperienceAEM.ImageMultiField.Panel);

ExperienceAEM.ImageMultiField.SmartImage = CQ.Ext.extend(CQ.html5.form.SmartImage, {
    syncFormElements: function() {
        if(!this.fileNameField.getEl().dom){
            return;
        }

        ExperienceAEM.ImageMultiField.SmartImage.superclass.syncFormElements.call(this);

        if (this.moveParameter) {
            this.moveParameter.getEl().dom.name = this.name + CQ.Sling.MOVE_SUFFIX;
        }
    },

    afterRender: function() {
        ExperienceAEM.ImageMultiField.SmartImage.superclass.afterRender.call(this);

        var dialog = this.findParentByType('dialog'),
            target = this.dropTargets[0], multifield, dialogZIndex;

        if (dialog && dialog.el && target.highlight) {
            dialogZIndex = parseInt(dialog.el.getStyle("z-index"), 10);

            if (!isNaN(dialogZIndex)) {
                target.highlight.zIndex = dialogZIndex + 1;
            }
        }

        multifield = this.findParentByType('multifield');
        multifield.dropTargets.push(target);

        this.dropTargets = undefined;
    },

    onFileSelected: function(uploadField, files) {
        var imageName = this.name;
        this.name = "./" + this.name.substring(this.name.lastIndexOf("/") + 1);
        ExperienceAEM.ImageMultiField.SmartImage.superclass.onFileSelected.call(this, uploadField, files);
        this.name = imageName;
    }
});

CQ.Ext.reg('imagemultifieldsmartimage', ExperienceAEM.ImageMultiField.SmartImage);

CQ.Ext.override(CQ.form.SmartImage.ImagePanel, {
    addCanvasClass: function(clazz) {
        var imageCanvas = CQ.Ext.get(this.imageCanvas);

        if(imageCanvas){
            imageCanvas.addClass(clazz);
        }
    },

    removeCanvasClass: function(clazz) {
        var imageCanvas = CQ.Ext.get(this.imageCanvas);

        if(imageCanvas){
            imageCanvas.removeClass(clazz);
        }
    }
});

CQ.Ext.override(CQ.form.SmartImage.Tool, {
    processRecord: function(record) {
        var iniValue = record.get(this.transferFieldName);

        if(!iniValue && ( this.transferFieldName.indexOf("/") !== -1 )){
            iniValue = record.get(this.transferFieldName.substr(this.transferFieldName.lastIndexOf("/") + 1));
        }

        if (iniValue === null) {
            iniValue = "";
        }

        this.initialValue = iniValue;
    }
});

CQ.Ext.override(CQ.form.MultiField.Item, {
    reorder: function(item) {
        if(item.field && item.field.xtype === "imagemultifieldpanel"){
            var c = this.ownerCt, iIndex = c.items.indexOf(item), tIndex = c.items.indexOf(this);

            if(iIndex < tIndex){ //user clicked up
                c.insert(c.items.indexOf(item), this);
                this.getEl().insertBefore(item.getEl());
            }else{//user clicked down
                c.insert(c.items.indexOf(this), item);
                this.getEl().insertAfter(item.getEl());
            }

            c.doLayout();
        }else{
            item.field.setValue(this.field.getValue());
            this.field.setValue(item.field.getValue());
        }
    }
});

ExperienceAEM.ImageMultiField.MultiField = CQ.Ext.extend(CQ.form.MultiField , {
    Record: CQ.data.SlingRecord.create([]),
    nextImageNum: 0,
    nextImageName: undefined,

    initComponent: function() {
        ExperienceAEM.ImageMultiField.MultiField.superclass.initComponent.call(this);

        var imagesOrder = new CQ.Ext.form.Hidden({
            name: this.getName() + "/order"
        }), dialog;

        this.add(imagesOrder);

        dialog = this.findParentByType('dialog');

        dialog.on('beforesubmit', function(){
            var imagesInOrder = this.find('xtype','imagemultifieldsmartimage'),
                order = [];

            CQ.Ext.each(imagesInOrder , function(image){
                order.push(image.name.substr(image.name.lastIndexOf("/") + 1));
            });

            imagesOrder.setValue(JSON.stringify(order));
        },this);

        this.dropTargets = [];

},

    addItem: function(value){
        if(!value){
            value = new this.Record({},{});
        }
        ExperienceAEM.ImageMultiField.MultiField.superclass.addItem.call(this, value);

    },

   processRecord: function(record, path) {
        if (this.fireEvent('beforeloadcontent', this, record, path) !== false) {
            this.items.each(function(item) {
                if(item.field && item.field.xtype === "imagemultifieldpanel"){
                    this.remove(item, true);
                }
            }, this);

            var images = record.get(this.getName()), oName, oValue, iNames, highNum, val;
            this.nextImageNum = 0;

            if (images) {
                oName = this.getName() + "/order";
                oValue = record.get(oName) ? record.get(oName) : "";
                iNames = JSON.parse(oValue);

                CQ.Ext.each(iNames, function(iName){
                    val = parseInt(iName.substr(iName.indexOf("-") + 1), 10);

                    if(!highNum || highNum < val){
                        highNum = val;
                    }

                    this.nextImageName = this.getName() + "/" + iName;
                    this.addItem(record);
                }, this);

                this.nextImageNum = highNum;
            }

            this.nextImageName = undefined;

            this.fireEvent('loadcontent', this, record, path);
        }
    }
});

CQ.Ext.reg('imagemultifield', ExperienceAEM.ImageMultiField.MultiField);

ExperienceAEM.ImageMultiField.SmrtImage = CQ.Ext.extend(CQ.html5.form.SmartImage, {
    syncFormElements: function() {
        if(!this.fileNameField.getEl().dom){
            return;
        }

        ExperienceAEM.ImageMultiField.SmrtImage.superclass.syncFormElements.call(this);

        if (this.moveParameter) {
            this.moveParameter.getEl().dom.name = this.name + CQ.Sling.MOVE_SUFFIX;
        }
    },

    afterRender: function() {
        ExperienceAEM.ImageMultiField.SmrtImage.superclass.afterRender.call(this);

        var dialog = this.findParentByType('dialog'),
            target = this.dropTargets[0], multifield, dialogZIndex;

        if (dialog && dialog.el && target.highlight) {
            dialogZIndex = parseInt(dialog.el.getStyle("z-index"), 10);

            if (!isNaN(dialogZIndex)) {
                target.highlight.zIndex = dialogZIndex + 1;
            }
        }

        multifield = this.findParentByType('multifield');
        multifield.dropTargets.push(target);

        this.dropTargets = undefined;
    },

    onFileSelected: function(uploadField, files) {
        var imageName = this.name;
        this.name = "./" + this.name.substring(this.name.lastIndexOf("/") + 1);
        ExperienceAEM.ImageMultiField.SmrtImage.superclass.onFileSelected.call(this, uploadField, files);
        this.name = imageName;
    }
});

CQ.Ext.reg('smrtimage', ExperienceAEM.ImageMultiField.SmrtImage);
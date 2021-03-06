/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2017 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
;(function ($, ns, channel, document, window, undefined) {
	"use strict";

	var registry = $(window).adaptTo("foundation-registry");

	// Redefine "required" behavior based on custom data attribute
	registry.register("foundation.adapters", {
		type: "foundation-field",
		selector: "coral-fileupload.cq-FileUpload",
		adapter: function(el) {
			return {
				isRequired: function() {
					return el.dataset.cqFileuploadRequired !== undefined;
				},
				setRequired: function(required) {
					if (required) {
						el.dataset.cqFileuploadRequired = "";
					} else {
						delete el.dataset.cqFileuploadRequired;
					}
				},
				getName: function(){
					return el.name;
				},
				setName: function(name){
					if(!el.initialName){
						el.initialName = el.name + '';
						el.fu._inputs.filereference.initialName = el.fu._inputs.filereference.name + '';
						el.fu._inputs.filename.initialName = el.fu._inputs.filereference.name + '';
						el.fu._inputs.filemovefrom.initialName = el.fu._inputs.filemovefrom.name + '';
					}

					if(name == el.initialName){
						el.fu._inputs.filereference.name = el.fu._inputs.filereference.initialName + '';
						el.fu._inputs.filename.name = el.fu._inputs.filereference.initialName + '';
						el.fu._inputs.filemovefrom.name = el.fu._inputs.filemovefrom.initialName + '';
					}else{
						var baseName = name.substring(0, name.lastIndexOf('/'));
						el.fu._inputs.filereference.name = baseName + '/' + el.fu._inputs.filereference.initialName;
						el.fu._inputs.filename.name = baseName + '/' + el.fu._inputs.filename.initialName;
						el.fu._inputs.filemovefrom.name = name.replace(el.initialName, '') + el.fu._inputs.filemovefrom.initialName;
					}
					el.fu._element.name = name;
					el.name = name;
				}
			};
		}
	});

	registry.register("foundation.validation.validator", {
		selector: "coral-fileupload.cq-FileUpload[data-cq-fileupload-required]",
		validate: function(element) {
			// A file is actually being uploaded
			if (element.uploadQueue.length) {
				return;
			}
			// Specific to the authoring fileupload: the element is marked with a special class once
			// the file has been uploaded OR after an asset reference has been dragged into the fileupload
			if (element.classList.contains("is-filled")) {
				return;
			}

			return Granite.I18n.get("Please fill out this field.");
		}
	});

}(Granite.$, Granite, jQuery(document), document, this));

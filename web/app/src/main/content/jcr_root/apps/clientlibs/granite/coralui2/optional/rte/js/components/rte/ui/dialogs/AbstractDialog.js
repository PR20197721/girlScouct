/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2013 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

(function($) {

    CUI.rte.ui.cui.AbstractDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractBaseDialog,

        config: null,

        dialogHelper: null,

        range: null,

        mask: null,

        /**
         * @private
         */
        editorKernel: null,

        initializeEdit: function(editorKernel, objToEdit, applyFn) {
            this.objToEdit = objToEdit;
            this.applyFn = applyFn;
            // TODO adjust to custom config (post 5.6.1)
            this.fromModel();
        },

        getFieldByType: function(name) {
            var $field = this.$dialog.find("*[data-type=\"" + name + "\"]");
            if ($field.length > 0) {
                return $field;
            }
            return undefined;
        },

        /**
         * Gets a dialog parameter by its name.
         * @param {String} name The parameter's name
         * @return {Object} The parameter's value; null if no such parameter is defined
         */
        getParameter: function(name) {
            var params = this.config.parameters;
            if (params && params[name]) {
                return params[name];
            }
            return undefined;
        },

        apply: function() {
            if (this.validate()) {
                this.toModel();
                this.hide();
                if (this.applyFn) {
                    this.applyFn(this.editorKernel.getEditContext(), this.objToEdit);
                }
            }
        },

        initialize: function(config) {
            this.config = config;
        },

        preprocessModel: function() {
            // this method may be overridden by implementing dialogs to pre-process
            // the model before the fromModel()-methods are being executed
        },

        dlgFromModel: function() {
            // this method may be overridden by implementing dialogs to transfer basic data
            // from model to view
        },

        fromModel: function() {
            this.preprocessModel();
            // TODO handle additional fields (backwards compatibility)
            this.dlgFromModel();
        },

        validate: function() {
            // may be overridden by implementing dialog
            return true;
        },

        dlgToModel: function() {
            // this method may be overridden by implementing dialogs to transfer basic data
            // from view to model
        },

        postprocessModel: function() {
            // this method may be overridden by implementing dialogs to post-process
            // the model after all toModel()-methods have been executed
        },

        toModel: function() {
            this.dlgToModel();
            // TODO handle additional fields (backwards compatibility)
            this.postprocessModel();
        }

    });

})(window.jQuery);
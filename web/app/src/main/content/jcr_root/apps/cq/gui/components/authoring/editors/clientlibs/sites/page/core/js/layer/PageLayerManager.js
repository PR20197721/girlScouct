/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2013 Adobe Systems Incorporated
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
/**
 * @param {jQuery} $ is the jQuery object to be used
 * @param {Object} ns is the namespace object to be used
 * @param {jQuery} channel is the object to be used as an event bus
 * @param {HTMLElement} window global window object
 */
;(function ($, ns, channel, window, undefined) {
    "use strict";

    /**
     * Namespace for the editor's current layer cookie
     *
     * @const
     * @type {string}
     */
    var LAYER_NAMESPACE = 'cq-editor-layer';

    /**
     * The name of the Timewarp layer.
     *
     * @type {string}
     * @constant
     * @private
     */
    var TIMEWARP_LAYER = "Timewarp";

    /**
     * Layer Manager of the Page Editor
     *
     * @class
     * @extends Granite.author.LayerManager
     */
    ns.PageLayerManager = ns.util.extendClass(ns.LayerManager, {

        /**
         * Returns "Scaffolding" if present as a selector in the ContentFrame location or "Timewarp" if Timewarp is enabled.
         *
         * @override
         */
        getCurrentLayerName: function() {
            if (ns.ContentFrame.currentLocation().indexOf(".scaffolding.") > 0) {
                return "Scaffolding";
            } else if (ns.Timewarp.isActive()) {
                return TIMEWARP_LAYER;
            }

            return ns.PageLayerManager.super_.getCurrentLayerName.call(this);
        },

        /**
         * Sets the current active Layer.
         *
         * @override
         * @param {string} layerName - Name of the layer to be set as the current one
         */
        setCurrentLayer: function (layerName) {
            var prevLayer = ns.preferences.cookie.get(LAYER_NAMESPACE);

            ns.PageLayerManager.super_.setCurrentLayer.call(this, layerName);

            // don't persist scaffolding or timewarp layer in cookie: reset it to previous value
            if (layerName === "Scaffolding" || layerName === TIMEWARP_LAYER) {
                ns.preferences.cookie.set(LAYER_NAMESPACE, prevLayer);
            }
        },

        /**
         * Returns the name of the default layer of the page editor
         *
         * @override
         * @param {boolean} [ignoreScaffolding] - Should the Scafforlding layer be ignored
         * @returns {string} - Name of the default layer
         */
        getDefaultLayer: function(ignoreScaffolding) {
            var layerName;

            // scaffolding is not available for 404"s
            if (ignoreScaffolding !== true && ns.ContentFrame.currentLocation().indexOf(".scaffolding.") > 0) {
                layerName = "Scaffolding";
            } else if (!this.getCurrentLayerName() || this.getCurrentLayerName() === "Scaffolding") {
                layerName = this.wcmMode === "preview" ? "Preview" : "Edit";
            } else {
                layerName = this.getCurrentLayerName();
            }

            return layerName;
        },

        /**
         * Prepares the layer before the load.
         *
         * @param name {String} name of the layer
         * @returns {Boolean} true, if activation of layer is required, false otherwise
         */
        beforeLoadLayer : function (name) {
            var activateLayer = true;
            var that = this;
            if (name === "Scaffolding") {
                // Switching into scaffolding is done entirely by selector in the URL.  This is required to work
                // with existing systems that specify only the selector.
                var location = ns.ContentFrame.currentLocation();

                if (ns.ContentFrame.currentLocation().indexOf(".scaffolding.") > 0) {
                    location.replace(".scaffolding.", ".");
                }               
                // 03-08-2018 commenting this out to force classic ui 
                // ns.ContentFrame.load(location.replace(".html", ".scaffolding.html"));
                window.top.location = "/cf#"+location.replace(".html", ".scaffolding.html");
                // Avoid activating the layer now
                // It will be activated later when its content will be available in the content frame
                // activateLayer = false;
            } else if (this._currentLayer && this._currentLayer.name === "Scaffolding") {
                // When switching out we must clear the selector or we'll just end up re-entering scaffolding mode
                ns.ContentFrame.load(ns.ContentFrame.currentLocation().replace(".scaffolding.", "."));

                // activate layer after the iframe has finished loaded
                activateLayer = false;
                ns.ContentFrame.iframe.one("load", function(e) {
                    that.activateLayer(name);
                });
            } else if (this.getCurrentLayerName() === TIMEWARP_LAYER && name != TIMEWARP_LAYER
                    && ns.Timewarp.isActive()) {
                // when switching from the timewarp layer to another one and Timewarp is active:
                // - don't immediately activate new layer
                activateLayer = false;
                // - persist layer in cookie so that 'init' will activate the correct layer
                ns.preferences.cookie.set(LAYER_NAMESPACE, name);
                // - exit Timewarp
                ns.Timewarp.exit();
            }
            return activateLayer;
        },

        /**
         * Initializes the LayerManager; called on {@link Document#event:cq-editor-loaded}
         * It first initializes the Layer Selector, and then activates the default Layer.
         *
         * @override
         */
        init: function () {
            // Some layers depend on page status, so this must be done after loading said status
            this.updateLayerSelector();

            var layerName = this.getCurrentLayerName() || this.getDefaultLayer.apply(this, arguments);

            // Detect a leave from scaffolding or timewarp layer and return to the previously set layer
            if ((layerName === "Scaffolding" && ns.ContentFrame.currentLocation().indexOf(".scaffolding.") === -1)
                || (layerName === TIMEWARP_LAYER && !ns.Timewarp.isActive())) {
                // Restore previous layer from cookie
                layerName = ns.preferences.cookie.get(LAYER_NAMESPACE);
            }

            // In case layerName doesn't resolve to an available layer
            if (!layerName || !this._layers[layerName] || !this._layers[layerName].isAvailable()) {

                // Fallback to "Edit" if available
                if (this._layers["Edit"] && this._layers["Edit"].isAvailable()) {
                    layerName = "Edit";
                } else {
                    // Fallback to "Preview" otherwise
                    layerName = "Preview";
                }
            }

            this.activateLayer(layerName);
        },

        /**
         * Specific logic to the page editor
         *
         * @override
         * @param name
         */
        loadLayer: function (name) {
            var activateLayer = this.beforeLoadLayer(name);
            if (activateLayer) {
                this.activateLayer(name);
            }
        },

        /**
         * Loads the layer that is persisted in the cookie.
         */
        loadPersistedLayer: function () {
            var layer = ns.preferences.cookie.get(LAYER_NAMESPACE);
            if (layer) {
                this.loadLayer(layer);
            }
        }

    });

}(jQuery, Granite.author, jQuery(document), this));
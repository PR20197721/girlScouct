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

    CUI.rte.ConfigUtils = function() {

        function getPluginDef(action) {
            if (action) {
                var sepPos = action.indexOf("#");
                if (sepPos > 0) {
                    var plugin = action.substring(0, sepPos);
                    var feature = action.substring(sepPos + 1);
                    return {
                        "plugin": plugin,
                        "feature": feature
                    };
                }
            }
            return null;
        }

        return {

            createFeaturesFromToolbar: function($container, $toolbar) {
                var featureDefs = [ ];
                // first, analyze the toolbar
                var $buttons = $toolbar.find("button.coral-RichText-toolbar-item");
                $buttons.each(function() {
                    var pluginDef = getPluginDef($(this).data("action"));
                    if (pluginDef) {
                        featureDefs.push(pluginDef);
                    }
                });
                // then, analyze popovers
                var $popovers = $container.find(
                        ".coral-Popover button.coral-RichText-toolbar-item");
                $popovers.each(function() {
                    var pluginDef = getPluginDef($(this).data("action"));
                    if (pluginDef) {
                        featureDefs.push(pluginDef);
                    }
                });
                return featureDefs;
            },

            getValidConfig: function(config) {
                var pluginConfigs;
                if (!config.hasOwnProperty("rtePlugins")) {
                    return config;
                }
                /*
                * Parses a comma separated list if values into an actual array
                *
                * @param value
                * @returns {*}
                */
                function parseConfigValue(value) {
                    if (value.indexOf(",") > -1) {
                        value = value.split(",");
                    }
        
                    if (!$.isArray(value)) {
                        value = [value];
                    }
        
                    return value;
                }
                
                pluginConfigs = config["rtePlugins"];
                for (var pluginConfig in pluginConfigs) {
                    if (pluginConfigs.hasOwnProperty(pluginConfig)) {
                        if (pluginConfigs[pluginConfig].features) {
                            var features = pluginConfigs[pluginConfig].features;
                            if (!(features instanceof Array) && features != "*") {
                                pluginConfigs[pluginConfig].features = parseConfigValue(features);
                            }
                        }
                    }
                }

                function validatePopovers(popovers) {
                    for (var popover in popovers) {
                       if (popovers.hasOwnProperty(popover) && popovers[popover].items) {
                           popovers[popover].items = popovers[popover].items instanceof Array
                                   || popovers[popover].items.indexOf(":") > 0
                                   ? popovers[popover].items : [popovers[popover].items];
                       }
                    }
                }

                var uiSettings = config["uiSettings"];
                if (uiSettings && uiSettings.cui) {
                    if (uiSettings.cui.inline && uiSettings.cui.inline.popovers) {
                        validatePopovers(uiSettings.cui.inline.popovers);
                    }
                    if (uiSettings.cui.fullscreen && uiSettings.cui.fullscreen.popovers) {
                        validatePopovers(uiSettings.cui.fullscreen.popovers);
                    }
                }
                return config;
            },

            mergeConfigAndFeatures: function(config, features) {
                if (!features || (features.length === 0)) {
                    return config;
                }
                var pluginConfig;
                if (config.hasOwnProperty("rtePlugins")) {
                    pluginConfig = config["rtePlugins"];
                } else {
                    pluginConfig = { };
                    config["rtePlugins"] = pluginConfig;
                }
                var featureCnt = features.length;
                for (var f = 0; f < featureCnt; f++) {
                    var feature = features[f];
                    var pluginId = feature.plugin;
                    var featureId = feature.feature;
                    var cfg, plgFeature;
                    if (!pluginConfig.hasOwnProperty(pluginId)) {
                        cfg = { };
                        pluginConfig[pluginId] = cfg;
                    } else {
                        cfg = pluginConfig[pluginId];
                    }
                    if (cfg.hasOwnProperty("features")) {
                        plgFeature = cfg["features"];
                        if (CUI.rte.Utils.isArray(plgFeature)) {
                            plgFeature.push(featureId)
                        } else {
                            if (plgFeature !== "*") {
                                plgFeature = [ featureId ];
                                cfg["features"] = plgFeature;
                            }
                        }
                    } else {
                        plgFeature = [ featureId ];
                        cfg["features"] = plgFeature;
                    }
                }
                return config;
            },

            loadConfigAndStartEditing: function(rte, $editable, configCallback) {

                $editable.addClass("is-initializing");

                var features;
                var $container = CUI.rte.UIUtils.getUIContainer($editable);
                var $toolbar = CUI.rte.UIUtils.getToolbar($editable);

                function processConfig(config, useFixedInlineToolbar) {
                    config = CUI.rte.ConfigUtils.getValidConfig(config);
                    if (configCallback) {
                        config = configCallback(config);
                    }
                    config = CUI.rte.ConfigUtils.mergeConfigAndFeatures(config, features);
                    rte.start(config, useFixedInlineToolbar);
                    $editable.removeClass("is-initializing");
                }

                if ($toolbar && ($toolbar.length > 0)) {
                    features = CUI.rte.ConfigUtils.createFeaturesFromToolbar($container,
                            $toolbar);
                }
                var config = { };
                var configObj = $editable.data("config");
                if (configObj) {
                    if (typeof configObj === "string") {
                        try {
                            config = $.parseJSON(configObj);
                        } catch (e) {
                            // use default config
                        }
                    } else {
                        config = configObj;
                    }
                    processConfig(config);
                } else {
                    var configPath = $editable.data("config-path");
                    var useFixedInlineToolbar = $editable.data("use-fixed-inline-toolbar");
                    if (configPath) {
                        $.ajax({
                            "url": configPath,
                            "dataType": "json",
                            "success": function(data) {
                                processConfig(data, useFixedInlineToolbar);
                            },
                            "error": function() {
                                processConfig({ }, useFixedInlineToolbar);
                            }
                        });
                    } else {
                        processConfig(config, useFixedInlineToolbar);
                    }
                }
                $editable.data("rteinstance", rte);
            }

        };

    }();

})(window.jQuery);
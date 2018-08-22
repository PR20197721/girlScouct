/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2012 Adobe Systems Incorporated
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

/**
 * @class CUI.rte.plugins.LinkPlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements links and anchors as a plugin.</p>
 * <p>The plugin ID is "<b>links</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>modifylink</b> - adds a button to create and modify links</li>
 *   <li><b>unlink</b> - adds a button to remove existing links</li>
 *   <li><b>anchor</b> - adds a button to define anchors</li>
 * </ul>
 */
/* global Class: true */
/* jshint strict: false */
(function (CUI) {
  //'use strict';
  CUI.rte.plugins.LinkPlugin = new Class({

    toString: 'LinkPlugin',

    extend: CUI.rte.plugins.Plugin,

    /**
     * @cfg {Boolean} trimLinkSelection
     * True if leading and trailing whitespace should removed from the selection (not from
     * the actual text/content!) before creating a new link (defaults to true).
     * @since 5.3
     */

    /**
     * @cfg {Object} linkDialogConfig
     * @since 5.3
     */

    /**
     * @cfg {Object} anchorDialogConfig
     * Configuration of the anchor dialog (defaults to { }). You may specify the same
     * config options as for a dialog in the toolkit used. Note that the default value
     * of null implies using a default dialog.
     * @since 5.3
     */

    /**
     * @cfg {String} anchorEditingStyle
     * Style String to be applied on the anchor to make it visible while editing RTE.
     * If this configuration is not provided, the CSS class {@link CUI.rte.Theme.ANCHOR_CLASS} will be used.
     * @since 6.2
     */

    /**
     * @private
     */
    linkDialog: null,

    /**
     * @private
     */
    anchorDialog: null,

    /**
     * @private
     */
    linkUI: null,

    /**
     * @private
     */
    removeLinkUI: null,

    /**
     * @private
     */
    anchorUI: null,

    dialogHelper: null,

    getFeatures: function () {
      return ['modifylink', 'unlink', 'anchor'];
    },

    /**
     * Creates a link using the internal link dialog.
     * @private
     */
    modifyLink: function (context) {
      var com = CUI.rte.Common;
      var dm = this.editorKernel.getDialogManager();
      // hide if dialog is already shown & configured acordingly
      if (dm.isShown(this.linkDialog) && dm.toggleVisibility(this.linkDialog)) {
        dm.hide(this.linkDialog);
        return;
      }
      var dh = CUI.rte.ui.DialogHelper;
      if (!this.linkDialog || dm.mustRecreate(this.linkDialog)) {
        var dialogHelper = dm.createDialogHelper();
        this.dialogHelper = dialogHelper;
        var linkRules = this.editorKernel.htmlRules.links;
        var dialogConfig = {
          'configVersion': 1,
          'defaultDialog': {
            'dialogClass': {
              'type': dh.TYPE_DIALOG
            }
          },
          'parameters': {
            'linkRules': linkRules,
            'editorKernel': this.editorKernel,
            'command': this.pluginId + '#modifylink'
          }
        };
        if (this.config.linkDialogConfig) {
          var addDialogConfig = this.config.linkDialogConfig;
          if (addDialogConfig.linkAttributes) {
            com.removeJcrData(addDialogConfig.linkAttributes);
            var linkAttribs = com.toArray(addDialogConfig.linkAttributes);
            dialogConfig.additionalFields = [];
            var attribCnt = linkAttribs.length;
            var fromModel = function (obj, field) {
              if (dialogHelper.getItemType(field) === dh.TYPE_HIDDEN) {
                return;
              }
              var attribName = dialogHelper.getItemName(field);
              var attribValue = com.getAttribute(obj.dom, attribName);
              if (attribValue) {
                dialogHelper.setItemValue(field, attribValue);
              } else {
                dialogHelper.setItemValue(field, '');
              }
            };
            var toModel = function (obj, field) {
              var attribName = dialogHelper.getItemName(field);
              if (!obj.attributes) {
                obj.attributes = {};
              }
              var value = dialogHelper.getItemValue(field);
              if (value && (value.length > 0)) {
                obj.attributes[attribName] = value;
              } else {
                obj.attributes[attribName] =
                  CUI.rte.commands.Link.REMOVE_ATTRIBUTE;
              }
            };
            for (var a = 0; a < attribCnt; a++) {
              var attrib = linkAttribs[a];
              var type = attrib.type || attrib.xtype;
              var attribName = attrib.attribute;
              var attribLabel = attrib.label || attrib.fieldLabel;
              var itemData = {
                'item': dialogHelper.createItem(type, attribName, attribLabel),
                'fromModel': fromModel,
                'toModel': toModel
              };
              delete attrib.attribute;
              delete attrib.type;
              delete attrib.xtype;
              delete attrib.label;
              delete attrib.fieldLabel;
              CUI.rte.Utils.applyDefaults(itemData.item, attrib);
              dialogConfig.additionalFields.push(itemData);
            }
            delete addDialogConfig.linkAttributes;
          }
          CUI.rte.Utils.applyDefaults(dialogConfig.dialogProperties, addDialogConfig);
        }
        if (linkRules.targetConfig) {
          if (linkRules.targetConfig.mode !== 'blank') {
            dialogConfig.disabledDefaultFields = ['targetBlank'];
          }
          if (linkRules.targetConfig.mode === 'manual') {
            if (!dialogConfig.additionalFields) {
              dialogConfig.additionalFields = {};
            }
            var targetItem = dialogHelper.createItem(dh.TYPE_TEXTFIELD, 'target',
              CUI.rte.Utils.i18n('plugins.link.anchorTitle'));
            dialogConfig.additionalFields.push({
              'item': targetItem,
              'fromModel': function (obj, field) {
                if (obj.dom && obj.dom['target']) {
                  dialogHelper.setItemValue(field, obj.dom['target']);
                }
              },
              'toModel': function (obj, field) {
                if (!obj.attributes) {
                  obj.attributes = {};
                }
                var value = dialogHelper.getItemValue(field);
                if (value && (value.length > 0)) {
                  obj.attributes['target'] = value;
                } else {
                  obj.attributes['target'] = null;
                }
              }
            });
          }
        }
        CUI.rte.Utils.applyDefaults(dialogConfig, this.config.linkDialogConfig || {});
        dialogHelper.configure(dialogConfig);
        var dialogClass = this.getDialogClass();
        if (dialogClass) {
          dialogHelper.registerDialog(dialogClass);
        }
        this.linkDialog = dialogHelper.create();
        dialogHelper.calculateInitialPosition();
      }
      var linkToEdit = null;
      var selectionDef = this.editorKernel.analyzeSelection();
      if (selectionDef.anchorCount === 1) {
        linkToEdit = selectionDef.anchors[0];
      }
      linkToEdit = linkToEdit || {};
      if (typeof linkToEdit.attributes === 'undefined') {
        linkToEdit.attributes = {};
      }
      this.linkDialog.initializeEdit(this.editorKernel, linkToEdit,
        CUI.rte.Utils.scope(this.applyLink, this));
      this.savedRange = CUI.rte.Selection.saveNativeSelection(context);
      dm.show(this.linkDialog);
    },

    getDialogClass: function() {
        return null;
    },

    applyLink: function (context) {
      var linkObj = this.linkDialog.objToEdit;
      if (linkObj) {
        var linkUrl = linkObj.href;
        linkUrl = linkUrl.replace(/'/g,"%27");
        var cssClass = linkObj.cssClass;
        var target = linkObj.target;
        CUI.rte.Selection.restoreNativeSelection(context, this.savedRange);
        this.editorKernel.relayCmd('modifylink', {
          'url': linkUrl,
          'css': cssClass,
          'target': target,
          'trimLinkSelection': this.config.trimLinkSelection,
          'attributes': linkObj.attributes
        });
      }
    },

    /**
     * Creates an anchor using the anchor dialog.
     * @private
     */
    modifyAnchor: function (context) {
      var dm = this.editorKernel.getDialogManager();
      if (dm.isShown(this.anchorDialog) && dm.toggleVisibility(this.anchorDialog)) {
        dm.hide(this.anchorDialog);
        return;
      }
      if (!this.anchorDialog || dm.mustRecreate(this.anchorDialog)) {
        var editorKernel = this.editorKernel;
        var plugin = this;
        var defaultConfig = {
          execute: function (value) {
            CUI.rte.Selection.restoreNativeSelection(context, plugin.savedRange);
            editorKernel.relayCmd('anchor', { anchorValue : value,
              pluginConfig : plugin.config});
          }
        };
        var dialogConfig;
        if (this.config.anchorDialogConfig) {
          dialogConfig = CUI.rte.Utils.copyObject(this.config.anchorDialogConfig);
        } else {
          dialogConfig = {};
        }
        dialogConfig.parameters = {
          'command': this.pluginId + '#anchor'
        };
        CUI.rte.Utils.applyDefaults(dialogConfig, defaultConfig);
        this.anchorDialog = dm.create(CUI.rte.ui.DialogManager.DLG_ANCHOR,
          dialogConfig);
      } else {
        this.anchorDialog.resetValues();
      }
      dm.prepareShow(this.anchorDialog);
      var selectionDef = this.editorKernel.analyzeSelection(context);
      if (selectionDef.namedAnchorCount === 1) {
        this.anchorDialog.setAnchor(selectionDef.namedAnchors[0]);
      }
      this.savedRange = CUI.rte.Selection.saveNativeSelection(context);
      dm.show(this.anchorDialog);
    },

    initializeUI: function (tbGenerator) {
      var plg = CUI.rte.plugins;
      if (this.isFeatureEnabled('modifylink')) {
        this.linkUI = tbGenerator.createElement('modifylink', this, false,
          this.getTooltip('modifylink'));
        tbGenerator.addElement('links', plg.Plugin.SORT_LINKS, this.linkUI, 10);
      }
      if (this.isFeatureEnabled('unlink')) {
        this.removeLinkUI = tbGenerator.createElement('unlink', this, false,
          this.getTooltip('unlink'));
        tbGenerator.addElement('links', plg.Plugin.SORT_LINKS, this.removeLinkUI, 20);
      }
      if (this.isFeatureEnabled('anchor')) {
        this.anchorUI = tbGenerator.createElement('anchor', this, true,
          this.getTooltip('anchor'));
        tbGenerator.addElement('links', plg.Plugin.SORT_LINKS, this.anchorUI, 30);
      }
    },

    notifyPluginConfig: function (pluginConfig) {
      pluginConfig = pluginConfig || {};
      CUI.rte.Utils.applyDefaults(pluginConfig, {
        'features': '*',
        'trimLinkSelection': true,
        'linkDialogConfig': {
          'targetConfig': {
            'mode': 'manual'
          }
        },
        'anchorDialogConfig': {
          // empty by default
        },
        'tooltips': {
          'modifylink': {
            'title': CUI.rte.Utils.i18n('plugins.link.linkTitle'),
            'text': CUI.rte.Utils.i18n('plugins.link.linkText')
          },
          'unlink': {
            'title': CUI.rte.Utils.i18n('plugins.link.unlinkTitle'),
            'text': CUI.rte.Utils.i18n('plugins.link.unlinkText')
          },
          'anchor': {
            'title': CUI.rte.Utils.i18n('plugins.link.anchorTitle'),
            'text': CUI.rte.Utils.i18n('plugins.link.anchorText')
          }
        }
      });
      this.config = pluginConfig;
    },

    execute: function (cmd, value, env) {
      if (cmd === 'modifylink') {
        this.modifyLink(env.editContext);
      } else if (cmd === 'anchor') {
        this.modifyAnchor(env.editContext);
      } else {
        this.editorKernel.relayCmd(cmd);
      }
    },

    updateState: function (selDef) {
      var hasSingleAnchor = selDef.anchorCount === 1;
      var hasNoAnchor = selDef.anchorCount === 0;
      var selectedNode = selDef.selectedDom;
      var isLinkableObject = false;
      if (selectedNode) {
        isLinkableObject = CUI.rte.Common.isTag(selectedNode,
          CUI.rte.plugins.LinkPlugin.LINKABLE_OBJECTS);
        // placeholder images are not linkable, so we need to check here explicitly
        isLinkableObject &=
          !CUI.rte.genhtml.GenericHtmlRules.isPlaceholder(selectedNode);
      }
      var isCreateLinkEnabled = hasSingleAnchor ||
        ((selDef.isSelection || isLinkableObject) && hasNoAnchor);
      if (this.linkUI) {
        this.linkUI.setDisabled(!isCreateLinkEnabled);
      }
      if (this.removeLinkUI) {
        this.removeLinkUI.setDisabled(!hasSingleAnchor);
      }
      if (this.anchorUI) {
        var hasSingleNamedAnchor = (selDef.namedAnchorCount === 1);
        this.anchorUI.setSelected(hasSingleNamedAnchor);
      }
    },

    isHeadless: function (cmd, value) {
      return (cmd === 'unlink');
    },

    getConfig: function () {
      return this.config;
    }
  });

  /**
   * Array with tag names that define objects (like images) that are linkable when selected
   * @private
   * @static
   * @final
   * @type String[]
   */
  CUI.rte.plugins.LinkPlugin.LINKABLE_OBJECTS = [
    'img'
  ];


// register plugin
  CUI.rte.plugins.PluginRegistry.register('links', CUI.rte.plugins.LinkPlugin);
}(window.CUI));

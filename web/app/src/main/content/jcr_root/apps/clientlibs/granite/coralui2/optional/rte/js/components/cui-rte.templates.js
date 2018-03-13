this["CUI"] = this["CUI"] || {};
this["CUI"]["rte"] = this["CUI"]["rte"] || {};
this["CUI"]["rte"]["Templates"] = this["CUI"]["rte"]["Templates"] || {};

this["CUI"]["rte"]["Templates"]["dlg-anchor"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"anchor\" class=\"coral-Popover coral-RichText-dialog\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"id\" value=\"\" class=\"coral-Textfield\">\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column is-hidden\">\r\n                <button data-type=\"delete\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.anchor.remove", options) : helperMissing.call(depth0, "rteI18n", "dialog.anchor.remove", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.anchor.remove", options) : helperMissing.call(depth0, "rteI18n", "dialog.anchor.remove", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--warning\">\r\n                    <i class=\"coral-Icon coral-Icon--delete coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-default"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, functionType="function", escapeExpression=this.escapeExpression, self=this, helperMissing=helpers.helperMissing;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n            <div class=\"coral-RichText-dialog-headerContainer\">\r\n                <h1>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.title); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h1>\r\n            </div>\r\n        ";
  return buffer;
  }

function program3(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n                    ";
  if (stack1 = helpers.rendered) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.rendered); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n                ";
  return buffer;
  }

  buffer += "<div data-rte-dialog=\"default\" class=\"coral-Popover coral-RichText-dialog\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        ";
  stack1 = helpers['if'].call(depth0, (depth0 && depth0.title), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        <div class=\"coral-RichText-dialog-rowContainer\">\r\n            <div class=\"coral-RichText-dialog-row\">\r\n                ";
  stack1 = helpers.each.call(depth0, (depth0 && depth0.dialogItems), {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right\">\r\n                <button type=\"button\" data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right\">\r\n                <button type=\"button\" data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-find"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"find\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--find\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"find\" value=\"\" class=\"coral-Textfield\">\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <label class=\"coral-Checkbox\">\r\n                    <input type=\"checkbox\" name=\"matchCase\" value=\"false\" data-type=\"matchCase\" class=\"coral-Checkbox-input\">\r\n                    <span class=\"coral-Checkbox-checkmark\"></span>\r\n                    <span class=\"coral-Checkbox-description\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialogs.find.matchCase", options) : helperMissing.call(depth0, "rteI18n", "dialogs.find.matchCase", options)))
    + "</span>\r\n                </label>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"execFind\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.find.find", options) : helperMissing.call(depth0, "rteI18n", "dialog.find.find", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.find.find", options) : helperMissing.call(depth0, "rteI18n", "dialog.find.find", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--search coral-Icon--sizeS coral-RichText--white\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-image"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"image\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--imageprops\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--alt\"><label> <input type=\"text\" data-type=\"alt\" placeholder=\"Alt Text\" class=\"coral-Textfield\"/> </label> </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <span class=\"coral-Select coral-RichText-dialog-column--imageAlignment\">\r\n                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                        <span class=\"coral-Select-button-text\">No Alignment</span>\r\n                    </button>\r\n                    <select class=\"coral-Select-select\">\r\n                        <option value=\"none\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "plugins.image.noAlign", options) : helperMissing.call(depth0, "rteI18n", "plugins.image.noAlign", options)))
    + "</option>\r\n                        <option value=\"left\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "plugins.image.alignLeft", options) : helperMissing.call(depth0, "rteI18n", "plugins.image.alignLeft", options)))
    + "</option>\r\n                        <option value=\"right\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "plugins.image.alignRight", options) : helperMissing.call(depth0, "rteI18n", "plugins.image.alignRight", options)))
    + "</option>\r\n                        <option value=\"inherit\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "plugins.image.alignInherit", options) : helperMissing.call(depth0, "rteI18n", "plugins.image.alignInherit", options)))
    + "</option>\r\n                    </select>\r\n                </span>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button type=\"button\" data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button type=\"button\" data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-link"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\r\n                <span class=\"coral-PathBrowser coral-RichText-pathbrowser\" data-placeholder=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.path", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.path", options)))
    + "\" data-root-path=\""
    + escapeExpression(((stack1 = ((stack1 = (depth0 && depth0.pathbrowser)),stack1 == null || stack1 === false ? stack1 : stack1.rootPath)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">\r\n                    <span class=\"coral-InputGroup\">\r\n                        <input class=\"coral-InputGroup-input js-coral-pathbrowser-input coral-Textfield\" type=\"text\" name=\"href\">\r\n                        <span class=\"coral-InputGroup-button\">\r\n                            <button class=\"coral-Button coral-Button--secondary coral-Button--square js-coral-pathbrowser-button\" type=\"button\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.path_picker", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.path_picker", options)))
    + "\">\r\n                                <i class=\"coral-Icon coral-Icon--sizeS coral-Icon--folderSearch\"></i>\r\n                            </button>\r\n                        </span>\r\n                    </span>\r\n                </span>\r\n            ";
  return buffer;
  }

function program3(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\r\n                <input class=\"coral-Textfield coral-RichText-pathbrowser\" name=\"href\" type=\"text\" placeholder=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.path", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.path", options)))
    + "\" />\r\n            ";
  return buffer;
  }

  buffer += "<div data-rte-dialog=\"link\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--link\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n            ";
  stack1 = helpers['if'].call(depth0, (depth0 && depth0.pathbrowserPicker), {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\"><label> <input type=\"text\" data-type=\"title\" placeholder=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.titleFieldPlaceHolder", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.titleFieldPlaceHolder", options)))
    + "\" class=\"coral-Textfield\"> </label> </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <span class=\"coral-Select coral-RichText-dialog-column--targetField\">\r\n                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                        <span class=\"coral-Select-button-text\"></span>\r\n                    </button>\r\n                    <select class=\"coral-Select-select\">\r\n                        <option value=\"\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.target", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.target", options)))
    + "</option>\r\n                        <option value=\"_self\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.same_tab", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.same_tab", options)))
    + "</option>\r\n                        <option value=\"_blank\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.new_tab", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.new_tab", options)))
    + "</option>\r\n                        <option value=\"_parent\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.parent_frame", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.parent_frame", options)))
    + "</option>\r\n                        <option value=\"_top\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.link.top_frame", options) : helperMissing.call(depth0, "rteI18n", "dialog.link.top_frame", options)))
    + "</option>\r\n                    </select>\r\n                </span>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--rightAligned\">\r\n                <button type=\"button\" data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n                <button type=\"button\" data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-pasteplaintext"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"pasteplaintext\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--pasteplaintext\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <textarea class=\"coral-Textfield coral-Textfield--multiline\" placeholder=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.pastePlainText.pasteAreaPlaceHolder", options) : helperMissing.call(depth0, "rteI18n", "dialog.pastePlainText.pasteAreaPlaceHolder", options)))
    + "\" rows=\"4\"></textarea>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right coral-RichText-dialog-column--actionButtons\">\r\n                <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n                <button data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-pastewordhtml"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"pastewordhtml\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--pastewordhtml\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <iframe></iframe>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right coral-RichText-dialog-column--actionButtons\">\r\n                <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n                <button data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>\r\n";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-replace"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"replace\" class=\"coral-Popover coral-RichText-dialog\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"find\" value=\"\" class=\"coral-Textfield coral-RichText--long coral-RichText--narrow\">\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"execFind\" tabindex=\"-1\" class=\"coral-RichText-dialogTextButton coral-Button coral-Button--primary\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.replace.findButton", options) : helperMissing.call(depth0, "rteI18n", "dialog.replace.findButton", options)))
    + "</button>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"replace\" value=\"\" class=\"coral-Textfield coral-RichText--long coral-RichText--narrow\">\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"execReplace\" tabindex=\"-1\" class=\"coral-RichText-dialogTextButton coral-Button coral-Button--primary\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.replace.replaceButton", options) : helperMissing.call(depth0, "rteI18n", "dialog.replace.replaceButton", options)))
    + "</button>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-fullSizeCell\">\r\n                <div class=\"coral-RichText-dialog-innerTable\">\r\n                    <div class=\"coral-RichText-dialog-columnContainer\">\r\n                        <div class=\"coral-RichText-dialog-column\">\r\n                            <label class=\"coral-Checkbox\">\r\n                                <input type=\"checkbox\" name=\"matchCase\" value=\"false\" data-type=\"matchCase\" class=\"coral-Checkbox-input\">\r\n                                <span class=\"coral-Checkbox-checkmark\"></span>\r\n                                <span class=\"coral-Checkbox-description\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialogs.replace.matchcase", options) : helperMissing.call(depth0, "rteI18n", "dialogs.replace.matchcase", options)))
    + "</span>\r\n                            </label>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-column coral-RichText--right\">\r\n                            <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                                <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                            </button>\r\n                        </div>\r\n                    </div>\r\n                </div>\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button data-type=\"execReplaceAll\" tabindex=\"-1\" class=\"coral-RichText-dialogTextButton coral-Button coral-Button--primary\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.replace.replaceAllButton", options) : helperMissing.call(depth0, "rteI18n", "dialog.replace.replaceAllButton", options)))
    + "</button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-specialchars"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"specialchars\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--specialchars\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-Richtext-dialog-column--specialchars\">\r\n                <div class=\"coral-RichText-specialchars-selector\">\r\n                    <div class=\"coral-RichText-dialog-innerTable\">\r\n                    </div>\r\n                </div>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right\">\r\n                <button data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-spellchecker"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n                <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--spellchecker\">\r\n                    <button ";
  stack1 = helpers['if'].call(depth0, (depth0 && depth0.original), {hash:{},inverse:self.program(4, program4, data),fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += " tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button ";
  stack1 = helpers['if'].call(depth0, (depth0 && depth0.original), {hash:{},inverse:self.program(8, program8, data),fn:self.program(6, program6, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">"
    + escapeExpression(((stack1 = (depth0 && depth0.display)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</button>\r\n                </div>\r\n            ";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += " data-value=\""
    + escapeExpression(((stack1 = (depth0 && depth0.original)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" data-type=\"replaceWord\" ";
  return buffer;
  }

function program4(depth0,data) {
  
  
  return " disabled=\"disabled\" ";
  }

function program6(depth0,data) {
  
  var buffer = "";
  return buffer;
  }

function program8(depth0,data) {
  
  
  return "is-disabled";
  }

  buffer += "<div data-rte-dialog=\"spellchecker\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--spellchecker\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            ";
  stack1 = helpers.each.call(depth0, (depth0 && depth0.suggestions), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-tableandcellprops"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"tableandcellprops\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--tableandcellprops\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-TabPanel coral-RichText-dialog-tabPanel--tableandcellprops\" data-init=\"tabs\">\r\n            <nav class=\"coral-TabPanel-navigation\">\r\n                <a class=\"coral-TabPanel-tab is-active\" data-toggle=\"tab\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.cellProps", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.cellProps", options)))
    + "</a>\r\n                <a class=\"coral-TabPanel-tab\" data-toggle=\"tab\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.tableProps", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.tableProps", options)))
    + "</a>\r\n            </nav>\r\n            <div class=\"coral-TabPanel-content\">\r\n                <section class=\"coral-TabPanel-pane is-active\">\r\n                    <div class=\"coral-RichText-dialog-table\">\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--rightAligned\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.width", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.width", options)))
    + "&nbsp;&nbsp;&nbsp;&nbsp;\r\n                                <input type=\"text\" data-type=\"cell-width\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n                                <span class=\"coral-Form-fieldinfo coral-Icon coral-Icon--infoCircle coral-Icon--sizeS\" data-init=\"quicktip\" data-quicktip-type=\"info\"\r\n                                      data-quicktip-arrow=\"left\" data-quicktip-content=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.widthToolTip", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.widthToolTip", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.widthToolTip", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.widthToolTip", options)))
    + "\"\r\n                                      tabindex=\"0\">\r\n                                </span>\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <span class=\"coral-Select coral-RichText-dialog-select--cellHorizontalAlignment\">\r\n                                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                                        <span class=\"coral-Select-button-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneAlignHor", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneAlignHor", options)))
    + "</span>\r\n                                    </button>\r\n                                    <select class=\"coral-Select-select\">\r\n                                        <option value=\"none\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneAlignHor", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneAlignHor", options)))
    + "</option>\r\n                                        <option value=\"left\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.leftAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.leftAlign", options)))
    + "</option>\r\n                                        <option value=\"center\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.centerAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.centerAlign", options)))
    + "</option>\r\n                                        <option value=\"right\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.rightAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.rightAlign", options)))
    + "</option>\r\n                                    </select>\r\n                                </span>\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--rightAligned coral-RichText-dialog-columnContainer--borderBottom\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.height", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.height", options)))
    + "&nbsp;&nbsp;&nbsp;\r\n                                <input type=\"text\" data-type=\"cell-height\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n                                <span class=\"coral-Form-fieldinfo coral-Icon coral-Icon--infoCircle coral-Icon--sizeS\" data-init=\"quicktip\" data-quicktip-type=\"info\"\r\n                                      data-quicktip-arrow=\"left\" data-quicktip-content=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.heightToolTip", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.heightToolTip", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.heightToolTip", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.heightToolTip", options)))
    + "\"\r\n                                      tabindex=\"0\">\r\n                                </span>\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-columnContainer--borderBottom\">\r\n                                <span class=\"coral-Select coral-RichText-dialog-select--cellVerticalAlignment\">\r\n                                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                                        <span class=\"coral-Select-button-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneAlignVer", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneAlignVer", options)))
    + "</span>\r\n                                    </button>\r\n                                    <select class=\"coral-Select-select\">\r\n                                        <option value=\"none\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneAlignVer", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneAlignVer", options)))
    + "</option>\r\n                                        <option value=\"top\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.topAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.topAlign", options)))
    + "</option>\r\n                                        <option value=\"middle\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.middleAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.middleAlign", options)))
    + "</option>\r\n                                        <option value=\"bottom\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.bottomAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.bottomAlign", options)))
    + "</option>\r\n                                        <option value=\"baseline\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.baselineAlign", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.baselineAlign", options)))
    + "</option>\r\n                                    </select>\r\n                                </span>\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n                    <div class=\"coral-RichText-dialog-table\">\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--borderAbove coral-RichText-dialog-column--cellType\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.cellType", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.cellType", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-column--borderAbove\">\r\n                                <span class=\"coral-Select coral-RichText-dialog-select--cellType\">\r\n                                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                                        <span class=\"coral-Select-button-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.cellType", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.cellType", options)))
    + "</span>\r\n                                    </button>\r\n                                    <select class=\"coral-Select-select\">\r\n                                        <option value=\"td\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.dataCell", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.dataCell", options)))
    + "</option>\r\n                                        <option value=\"th\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.headerCell", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.headerCell", options)))
    + "</option>\r\n                                    </select>\r\n                                </span>\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer coral-RichText-dialog-columnContainer--headerAttribute\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.headerAttrib", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.headerAttrib", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <input type=\"text\" data-type=\"cell-headers\" value=\"\" class=\"coral-Textfield coral-RichText--large\">\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer coral-RichText-dialog-columnContainer--idAttribute\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.idAttrib", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.idAttrib", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <input type=\"text\" data-type=\"cell-id\" value=\"\" class=\"coral-Textfield coral-RichText--small coral-RichText-Textfield--cellId\">\r\n                                <label class=\"coral-Form-fieldlabel\" for=\"coral-RichText-Switch--hiddenHeader\"></label>\r\n                                <span class=\"coral-Switch coral-RichText-dialog-Switch--hiddenHeader\">\r\n                                    <input class=\"coral-Switch-input\" data-type=\"cell-hiddenheader\" value=\"true\" id=\"coral-RichText-Switch--hiddenHeader\" type=\"checkbox\">\r\n                                    <span class=\"coral-Switch-offLabel\"></span><span class=\"coral-Switch-onLabel\">&nbsp;&nbsp;&nbsp;";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.hiddenHeader", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.hiddenHeader", options)))
    + "</span>\r\n                                </span>\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer coral-RichText-dialog-columnContainer--scopeAttribute\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.scopeAttrib", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.scopeAttrib", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <span class=\"coral-Select coral-RichText-dialog-select--scopeAttribute\">\r\n                                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                                        <span class=\"coral-Select-button-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneScopeAttrib", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneScopeAttrib", options)))
    + "</span>\r\n                                    </button>\r\n                                    <select class=\"coral-Select-select\">\r\n                                        <option value=\"none\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.noneScopeAttrib", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.noneScopeAttrib", options)))
    + "</option>\r\n                                        <option value=\"row\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.rowScope", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.rowScope", options)))
    + "</option>\r\n                                        <option value=\"col\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.columnScope", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.columnScope", options)))
    + "</option>\r\n                                    </select>\r\n                                </span>\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n                </section>\r\n                <section class=\"coral-TabPanel-pane\">\r\n                    <div class=\"coral-RichText-dialog-table\">\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.width", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.width", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <input type=\"text\" data-type=\"width\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.cellPadding", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.cellPadding", options)))
    + "&nbsp;<input type=\"text\" data-type=\"cellpadding\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.border", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.border", options)))
    + "&nbsp;<input type=\"text\" data-type=\"border\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.height", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.height", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <input type=\"text\" data-type=\"height\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.cellSpacing", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.cellSpacing", options)))
    + "&nbsp;&nbsp;<input type=\"text\" data-type=\"cellspacing\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n                            </div>\r\n                        </div>\r\n                        <div class=\"coral-RichText-dialog-columnContainer\">\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableAndCellProps.caption", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableAndCellProps.caption", options)))
    + "\r\n                            </div>\r\n                            <div class=\"coral-RichText-dialog-column\">\r\n                                <input type=\"text\" data-type=\"caption\" value=\"\" class=\"coral-Textfield coral-RichText--large\">\r\n                            </div>\r\n                        </div>\r\n                    </div>\r\n                </section>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer coral-RichText-dialog-columnContainer--actionButtons\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <button type=\"button\" data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n                <button type=\"button\" data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["dlg-tableprops"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div data-rte-dialog=\"tableprops\" class=\"coral-Popover coral-RichText-dialog coral-RichText-dialog--tableprops\">\r\n    <div class=\"coral-Popover-content coral-RichText-padding\">\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.columns", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.columns", options)))
    + "\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"columns\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.width", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.width", options)))
    + "&nbsp;&nbsp;<input type=\"text\" data-type=\"width\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.cellPadding", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.cellPadding", options)))
    + "&nbsp;<input type=\"text\" data-type=\"cellpadding\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.rows", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.rows", options)))
    + "\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"rows\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.height", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.height", options)))
    + "&nbsp;<input type=\"text\" data-type=\"height\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.cellSpacing", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.cellSpacing", options)))
    + "&nbsp;&nbsp;<input type=\"text\" data-type=\"cellspacing\" value=\"\" class=\"coral-Textfield coral-RichText--small\">\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.border", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.border", options)))
    + "\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column\">\r\n                <input type=\"text\" data-type=\"border\" value=\"\" class=\"coral-Textfield coral-RichText--small\">&nbsp;\r\n                <span class=\"coral-Select coral-RichText-dialog-column--headerField\">\r\n                    <button type=\"button\" class=\"coral-Select-button coral-MinimalButton\">\r\n                        <span class=\"coral-Select-button-text\">No Header</span>\r\n                    </button>\r\n                    <select class=\"coral-Select-select\">\r\n                        <option value=\"none\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.noHeader", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.noHeader", options)))
    + "</option>\r\n                        <option value=\"top\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.rowHeader", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.rowHeader", options)))
    + "</option>\r\n                        <option value=\"left\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.colHeader", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.colHeader", options)))
    + "</option>\r\n                        <option value=\"topleft\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.rowAndColHeader", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.rowAndColHeader", options)))
    + "</option>\r\n                    </select>\r\n                </span>\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-columnContainer--borderBottom\">\r\n                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.tableProps.caption", options) : helperMissing.call(depth0, "rteI18n", "dialog.tableProps.caption", options)))
    + "\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column coral-RichText-dialog-columnContainer--borderBottom\">\r\n                <input type=\"text\" data-type=\"caption\" value=\"\" class=\"coral-Textfield coral-RichText--large\">\r\n            </div>\r\n        </div>\r\n        <div class=\"coral-RichText-dialog-columnContainer\">\r\n            <div class=\"coral-RichText-dialog-column\">\r\n            </div>\r\n            <div class=\"coral-RichText-dialog-column coral-RichText--right coral-RichText-dialog-column--actionButtons\">\r\n                <button type=\"button\" data-type=\"cancel\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.cancel", options) : helperMissing.call(depth0, "rteI18n", "dialog.cancel", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square\">\r\n                    <i class=\"coral-Icon coral-Icon--close coral-Icon--sizeS\"></i>\r\n                </button>\r\n                <button type=\"button\" data-type=\"apply\" title=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" aria-label=\"";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.rteI18n || (depth0 && depth0.rteI18n)),stack1 ? stack1.call(depth0, "dialog.apply", options) : helperMissing.call(depth0, "rteI18n", "dialog.apply", options)))
    + "\" tabindex=\"-1\" class=\"coral-RichText-dialogButton coral-Button coral-Button--square coral-Button--primary\">\r\n                    <i class=\"coral-Icon coral-Icon--check coral-Icon--sizeS\"></i>\r\n                </button>\r\n            </div>\r\n        </div>\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["item-textarea"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n    <label for=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.id); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">";
  if (stack1 = helpers.label) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.label); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "</label><br>\r\n";
  return buffer;
  }

  stack1 = helpers['if'].call(depth0, (depth0 && depth0.label), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n<textarea name=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.id); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" id=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.id); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" class=\"coral-RichText--medium\"></textarea>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["paraformat-pulldown"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\r\n    <li><button data-action=\"paraformat#";
  stack2 = ((stack1 = (depth0 && depth0.tag)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\"><span>";
  stack2 = ((stack1 = (depth0 && depth0.description)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</span><i class=\"coral-Icon coral-Icon--sizeS\"></i></button></li>\r\n";
  return buffer;
  }

  buffer += "<ul class=\"coral-RichText-toolbar-list coral-RichText-toolbar-list--checkable\">\r\n";
  stack1 = helpers.each.call(depth0, depth0, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</ul>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["popover-item"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<button type=\"button\" title=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" aria-label=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" data-action=\"";
  if (stack1 = helpers.plugin) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.plugin); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "#";
  if (stack1 = helpers.command) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.command); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" tabindex=\"-1\" class=\"coral-RichText-toolbar-item is-disabled ";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.icon); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1);
  if (stack1 = helpers.addClasses) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.addClasses); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></button>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["popover-trigger"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<button type=\"button\" title=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" aria-label=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" data-action=\"";
  if (stack1 = helpers.ref) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.ref); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" tabindex=\"-1\" class=\"coral-RichText-toolbar-item ";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.icon); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + " coral-RichText--trigger";
  if (stack1 = helpers.addClasses) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.addClasses); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></button>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["popover"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n            ";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  return buffer;
  }

  buffer += "<div data-id=\"";
  if (stack1 = helpers.ref) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.ref); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" class=\"coral-Popover coral-RichText-popover\">\r\n    <div class=\"coral-Popover-content\">\r\n        ";
  stack1 = helpers.each.call(depth0, (depth0 && depth0.popoverItems), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    </div>\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["separator"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"coral-RichText-toolbar-separator\"></div>";
  });

this["CUI"]["rte"]["Templates"]["styles-pulldown"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\r\n    <li><button data-action=\"styles#";
  stack2 = ((stack1 = (depth0 && depth0.cssName)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\"><span>";
  stack2 = ((stack1 = (depth0 && depth0.text)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</span><i class=\"coral-Icon coral-Icon--sizeS\"></i></button></li>\r\n";
  return buffer;
  }

  buffer += "<ul class=\"coral-RichText-toolbar-list coral-RichText-toolbar-list--checkable\">\r\n";
  stack1 = helpers.each.call(depth0, depth0, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</ul>\r\n";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["tb-container"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\r\n<div data-type=\""
    + escapeExpression(((stack1 = (depth0 && depth0.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">\r\n    ";
  stack2 = ((stack1 = (depth0 && depth0.toolbar)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n    ";
  stack2 = helpers.each.call(depth0, (depth0 && depth0.popovers), {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n</div>\r\n";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        ";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    ";
  return buffer;
  }

  stack1 = helpers.each.call(depth0, (depth0 && depth0.toolbars), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["toolbar-item"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<button type=\"button\" title=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" aria-label=\"";
  if (stack1 = helpers.tooltip) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.tooltip); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" data-action=\"";
  if (stack1 = helpers.plugin) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.plugin); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "#";
  if (stack1 = helpers.command) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.command); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\" tabindex=\"-1\" class=\"coral-RichText-toolbar-item disabled ";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.icon); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1);
  if (stack1 = helpers.addClasses) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.addClasses); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></button>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["toolbar"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n    ";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n";
  return buffer;
  }

  buffer += "<div class=\"coral-RichText-toolbar\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 && depth0.toolbarItems), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</div>";
  return buffer;
  });

this["CUI"]["rte"]["Templates"]["ui-space"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div data-type=\"";
  if (stack1 = helpers.mode) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = (depth0 && depth0.mode); stack1 = typeof stack1 === functionType ? stack1.call(depth0, {hash:{},data:data}) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></div>";
  return buffer;
  });
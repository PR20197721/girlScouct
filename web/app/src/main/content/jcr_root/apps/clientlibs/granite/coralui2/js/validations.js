/*
 ADOBE CONFIDENTIAL

 Copyright 2013 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */
(function($, CUI) {
  // This is the place where the validation rules and messages are defined.
  // But only for validation that is generic or make sense at Coral level.
  // Otherwise please put your validation in your own file at your level.

  "use strict";


  if (document.documentElement.classList.contains("skipCoral2Validation") || document.head.querySelector(".skipCoral2Validation")) {
    return;
  }


  var DEBOUNCE_TIMEOUT = 500;

  var debounce = function(func, wait, immediate) {
    var timeout;

    return function() {
      var context = this;
      var args = arguments;
      var later = function() {
        timeout = null;

        if (!immediate) {
          func.apply(context, args);
        }
      };
      var callNow = immediate && !timeout;

      clearTimeout(timeout);
      timeout = setTimeout(later, wait);

      if (callNow) {
        func.apply(context, args);
      }
    };
  };


  // Register all the messages for validation
  // IMPORTANT: the order is important, where the last one will be checked first; when in doubt check the source of jquery-message

  $.message.register({
    selector: "*",
    message: function(el, key, params) {
      if (key === "validation.required") {
        return Granite.I18n.get("Please fill out this field.");
      }
    }
  });


  // Register all the validation rules
  // IMPORTANT: the order is important, where the last one will be checked first; when in doubt check the source of jquery-validator


  // NOTE about browsers behaviours
  // IE9 doesn't trigger "input" event when the character is deleted, thus error can only be validated fully upon form submission
  // IE10 recognizes aria-required attribute, hence the error state is shown straight away (based on current logic)

  function handleValidation(el) {
    el.checkValidity();
    el.updateErrorUI();
  }

  // Cancel the native invalid event (which is triggered by the browser supporting native validation)
  // to show our own UI instead
  $(document).on("cui-contentloaded", function(e) {
    $(":submittable", e.target).on("invalid", function(e) {
      if (e.isJqueryValidator) return;

      e.preventDefault();
      handleValidation($(this));
    });
  });


  var SIMPLE_ERROR_KEY = "coral-validations.internal.simple.error";

  var showErrorTooltip = function(el, message) {
    var error = el.data(SIMPLE_ERROR_KEY);

    if (error) {
      var tooltip = error.data("tooltip");
      tooltip.set("content", message);

      if (!error.parent().length) {
        el.after(error);
        tooltip.show();
      }
      return;
    }

    error = $(document.createElement("span"));
    el.data(SIMPLE_ERROR_KEY, error);

    el.after(error);

    var tooltip = new CUI.Tooltip({
      element: error,
      target: el,
      content: message,
      type: "error",
      arrow: "bottom"
    });
  };

  var hideErrorTooltip = function(el) {
    var error = el.data(SIMPLE_ERROR_KEY);

    if (!error) {
      return;
    }

    var tooltip = error.data("tooltip");
    tooltip.hide();
    error.detach();
  };


  $.validator.register({
    selector: "*",
    show: function(el, message) {
      el.attr("aria-invalid", "true").addClass("is-invalid");
      showErrorTooltip(el, message);
    },
    clear: function(el) {
      el.removeAttr("aria-invalid").removeClass("is-invalid");
      hideErrorTooltip(el);
    }
  });


  var compositeFieldSelector = [".coral-InputGroup-input",
    ".coral-DecoratedTextfield-input",
    ".js-coral-Autocomplete-textfield",
    ".js-coral-pathbrowser-input",
    ".coral-Checkbox-input",
    ".coral-Switch-input",
    ".coral-Select-select",
    ".coral-FileUpload-input"].join(", ");

  // WARNING: The order is important, put the more specific one at the top of the array.
  var compositeFieldRootMap = [
    [".js-coral-Autocomplete-textfield", ".coral-Autocomplete"],
    [".js-coral-pathbrowser-input", ".coral-PathBrowser"],
    [".coral-InputGroup-input", ".coral-InputGroup"],
    [".coral-DecoratedTextfield-input", ".coral-DecoratedTextfield"],
    [".coral-Checkbox-input", ".coral-Checkbox"],
    [".coral-Switch-input", ".coral-Switch"],
    [".coral-Select-select", ".coral-Select"],
    [".coral-FileUpload-input", ".coral-FileUpload"]
  ];

  var getCompositeRoot = function(el) {
    var mapper;

    compositeFieldRootMap.some(function(v) {
      if (el.is(v[0])) {
        mapper = v;
        return true;
      }
      return false;
    });

    if (mapper) {
      return el.closest(mapper[1]);
    }
  };

  $.validator.register({
    selector: compositeFieldSelector,
    show: function(el, message) {
      el.attr("aria-invalid", "true").addClass("is-invalid");
      showErrorTooltip(getCompositeRoot(el), message);
    },
    clear: function(el) {
      el.removeAttr("aria-invalid").removeClass("is-invalid");
      hideErrorTooltip(getCompositeRoot(el));
    }
  });


  var fieldErrorEl = $(document.createElement("span"))
    .addClass("coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS")
    .attr({
      "data-init": "quicktip",
      "data-quicktip-type": "error"
    });
  var FIELD_ERROR_KEY = "coral-validations.internal.field.error";

  var formFieldSelector = [".coral-Form-fieldwrapper .coral-Form-field",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-InputGroup .coral-InputGroup-input",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-DecoratedTextfield .coral-DecoratedTextfield-input",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-Autocomplete .js-coral-Autocomplete-textfield",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-PathBrowser .js-coral-pathbrowser-input",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-Checkbox .coral-Checkbox-input",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-Switch .coral-Switch-input",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-Select .coral-Select-select",
      ".coral-Form-fieldwrapper .coral-Form-field.coral-FileUpload .coral-FileUpload-input"].join(", ");

  $.validator.register({
    selector: formFieldSelector,
    show: function(el, message) {
      el.attr("aria-invalid", "true").addClass("is-invalid");

      var field = el.closest(".coral-Form-field");
      var info = field.nextAll(".coral-Form-fieldinfo");

      info.addClass("u-coral-screenReaderOnly");

      var error = el.data(FIELD_ERROR_KEY);

      if (error) {
        error.data("quicktipContent", message);

        if (!error.parent().length) {
          field.after(error);
        }
        return;
      }

      var arrow = info.data("quicktipArrow");
      if (!arrow) {
        var wrapper = field.closest(".coral-Form-fieldwrapper");
        if (wrapper.hasClass("coral-Form-fieldwrapper--singleline")) {
          arrow = "left";
        } else {
          arrow = wrapper.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";
        }
      }

      error = fieldErrorEl.clone()
        .attr("data-quicktip-arrow", arrow)
        .attr("data-quicktip-content", message)
        .insertAfter(field);

      el.data(FIELD_ERROR_KEY, error);
    },
    clear: function(el) {
      el.removeAttr("aria-invalid").removeClass("is-invalid");

      var field = el.closest(".coral-Form-field");

      var error = el.data(FIELD_ERROR_KEY);

      if (error) {
        var tooltip = error.next(".coral-Tooltip").data("tooltip");
        if (tooltip) {
          tooltip.hide();
        }
        error.detach();
      }

      field.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
    }
  });


  // Validator for required of input, textarea, select
  $.validator.register({
    selector: "input, textarea, select",
    validate: function(el) {
      var isRequired = el.prop("required") === true ||
        (el.prop("required") === undefined && el.attr("required") !== undefined) ||
        el.attr("aria-required") === "true";

      if (!isRequired) {
        return;
      }

      var valid = false;

      if (el.is("[type=checkbox], [type=radio]")) {
        valid = el.prop("checked");
      } else {
        valid = el.val().length > 0;
      }

      if (!valid) {
        return el.message("validation.required");
      }
    }
  });

  // Driver for input, textarea, select

  var validateHandler = function(e) {
    handleValidation($(this));
  };

  $(document).on("change", "input, textarea, select", validateHandler);
  $(document).on("input", "input, textarea", debounce(validateHandler, 500));


  // Validator for required of role=listbox
  $.validator.register({
    selector: "[role=listbox]",
    validate: function(el) {
      if (el.attr("aria-required") !== "true") {
        return;
      }

      var selected = false;
      el.find("[role=option]").each(function() {
        if ($(this).attr("aria-selected") === "true") {
          selected = true;
          return false;
        }
      });

      if (!selected) {
        return el.message("validation.required");
      }
    }
  });


  // Validator for coral-Select
  $.validator.register({
    selector: ".coral-Select-select",
    show: function(el, message) {
      el.prev(".coral-Select-button").addClass("is-invalid");
      return $.validator.CONTINUE;
    },
    clear: function(el, result) {
      el.prev(".coral-Select-button").removeClass("is-invalid");
      return $.validator.CONTINUE;
    }
  });


  // Driver for coral-Select
  $(document).on("selected", ".coral-Select", function(e) {
    handleValidation($(this).find(".coral-Select-select"));
  });


  // Driver for coral-Multifield
  var original = CUI.Multifield.prototype._addListeners;

  CUI.Multifield.prototype._addListeners = function() {
    original.apply(this, arguments);

    this.$element.on("click", ".js-coral-Multifield-remove", function(e) {
      handleValidation($(e.delegateTarget));
    });

    this.$element.on("click", ".js-coral-Multifield-add", function(e) {
      var el = $(e.delegateTarget);

      el.find(".js-coral-Multifield-input").attr({
        role: "option",
        "aria-selected": "true"
      });

      handleValidation(el);
    });
  };


  // Validator for coral-NumberInput
  $.validator.register({
    selector: ".js-coral-NumberInput-input",
    show: function(el, message) {
      el.closest(".coral-NumberInput").data("numberInput").set("hasError", true);
      return $.validator.CONTINUE;
    },
    clear: function(el, result) {
      el.closest(".coral-NumberInput").data("numberInput").set("hasError", false);
      return $.validator.CONTINUE;
    }
  });


  // Validator for coral-DatePicker
  $.validator.register({
    selector: ".js-coral-DatePicker-input",
    show: function(el, message) {
      el.closest(".coral-DatePicker").data("datepicker").set("hasError", true);
      return $.validator.CONTINUE;
    },
    clear: function(el, result) {
      el.closest(".coral-DatePicker").data("datepicker").set("hasError", false);
      return $.validator.CONTINUE;
    }
  });


  // Driver for coral-Selector
  $(document).on("change", ".coral-Selector-input", function(e) {
    var el = $(this);
    var selector = el.closest(".coral-Selector");

    if (el.is("[type=radio]")) {
      selector.find(".coral-Selector-input").attr("aria-selected", "false");
    }

    el.attr("aria-selected", "" + this.checked);

    handleValidation(selector);
  });


  // Driver for coral-RadioGroup
  $(document).on("change", ".coral-RadioGroup", function(e) {
    var group = $(this);
    var input = $(e.target);

    if (input.is("[type=radio]")) {
      group.find("input[type=radio]").attr("aria-selected", "false");
    }

    input.attr("aria-selected", "" + input.prop("checked"));

    handleValidation(group);
  });

  // TODO this is a tempororary code to set the aria-selected according to checked state of radio
  $(document).on("cui-contentloaded", function(e) {
    $(".coral-RadioGroup input[type=radio]", e.target).each(function() {
      $(this).attr("aria-selected", "" + this.checked);
    });
  });


  // Validator for required for coral-FileUpload
  $.validator.register({
    selector: ".coral-FileUpload-input",
    validate: function(el) {
      if (el.attr("aria-required") !== "true") {
        return;
      }

      var upload = el.closest(".coral-FileUpload").data("fileUpload");

      if (upload.uploadQueue.length === 0) {
        return el.message("validation.required");
      }
    }
  });

  // Driver for coral-FileUpload
  $(document).on("queuechanged", ".coral-FileUpload", function(e) {
    handleValidation($(this));
  });


  // Validator for required for coral-ColorPicker
  $.validator.register({
    selector: ".coral-ColorPicker",
    validate: function(el) {
      if (el.attr("aria-required") !== "true") {
        return;
      }
      if (!el.children("input[type=hidden]").val()) {
        return el.message("validation.required");
      }
    }
  });

  // Driver for coral-ColorPicker

  var originalConstruct = CUI.Colorpicker.prototype.construct;

  CUI.Colorpicker.prototype.construct = function() {
    originalConstruct.apply(this, arguments);

    // A workaround for make [role=listview] validtor to always be valid
    // $.validator works only for native or proper ARIA, and CUI.Colorpicker doesn't have ARIA.

    this.$element.attr("role", "listbox");

    $(document.createElement("meta")).attr({
      role: "option",
      "aria-selected": "true"
    }).appendTo(this.$element);
  };

  var originalSetColor = CUI.Colorpicker.prototype._setColor;

  CUI.Colorpicker.prototype._setColor = function() {
    originalSetColor.apply(this, arguments);
    handleValidation(this.$element);
  };


  // Validator for required for coral-Autocomplete
  $.validator.register({
    selector: ".coral-Autocomplete",
    validate: function(el) {
      // value is not required, so validation is skipped
      if (el.attr("aria-required") !== "true") {
        return;
      }

      var autocomplete = el.data("autocomplete");

      // validates both multiple and non-multiple options
      if (autocomplete.getValue().length === 0) {
        return el.message("validation.required");
      }
    },
    show: function(el, message) {
      el.find(".js-coral-Autocomplete-textfield").first().addClass("is-invalid");
      return $.validator.CONTINUE;
    },
    clear: function(el, result) {
      el.find(".js-coral-Autocomplete-textfield").first().removeClass("is-invalid");
      return $.validator.CONTINUE;
    }
  });

  // Driver for coral-Autocomplete

  $(document).on("change:value", ".coral-Autocomplete", function(e) {
    var el = $(this);

    // Also run validators bound to the textfield
    if (!el.data("autocomplete").options.multiple) {
      var textfield = el.find(".js-coral-Autocomplete-textfield");
      handleValidation(textfield);
    }

    handleValidation(el);
  });

  var acValidateHandler = function(e) {
    var el = $(this);
    var ac = el.closest(".coral-Autocomplete");

    if (ac.data("autocomplete").options.multiple) {
      return;
    }

    handleValidation(ac);
  };

  $(document).on("change", ".js-coral-Autocomplete-textfield", acValidateHandler);
  $(document).on("input", ".js-coral-Autocomplete-textfield", debounce(acValidateHandler, DEBOUNCE_TIMEOUT));


  // Validator UI for the TabPanel,
  // where the tab is valid/invalid according the validity of the fields inside the panel.

  /**
   * Gets the Tab and Pane that contain the provided element.
   *
   * @param {jQuery} el
   *    The field that failed the validation.
   *
   * @returns {Object} the tab and pane object that match where the element was located.
   *
   * @private
   */
  function getRelatedTabAndPane(el) {
    // we first fine the pane where it is located
    var panel = el.closest(".coral-TabPanel-pane");
    // index where the item is located
    var panelIndex = panel.index();
    // parent tabpanel
    var tabPanel = panel.closest(".coral-TabPanel");
    // gets the corresponding tab based on the panel index
    var tab = tabPanel.find("> .coral-TabPanel-navigation > .coral-TabPanel-tab").eq(panelIndex);

    return {
      tab: tab,
      pane: panel
    };
  }

  function pushInvalid(container, invalid) {
    var key = "coral-TabPanel.internal.invalids";
    
    var invalids = container.data(key);
    if (invalids === undefined) {
      invalids = [];
      container.data(key, invalids);
    }

    if (invalids.indexOf(invalid) < 0) {
      invalids.push(invalid);
    }
  }
  
  $(document).on("invalid", ".coral-TabPanel", function(e) {
    var result = getRelatedTabAndPane($(e.target));

    result.tab.addClass("is-invalid");
    pushInvalid(result.pane, e.target);
  });
  
  $(document).on("valid", ".coral-TabPanel", function(e) {
    var result = getRelatedTabAndPane($(e.target));

    var currentTab = result.tab;
    var currentPanel = result.pane;
    
    var enable = function() {
      if ($.validator.isValid(currentPanel, true)) {
        currentTab.removeClass("is-invalid");
      }
    };
    
    var invalids = currentPanel.data("coral-TabPanel.internal.invalids");
    
    if (!invalids) {
      enable();
      return;
    }
    
    var i = invalids.indexOf(e.target);
    if (i >= 0) {
      invalids.splice(i, 1);
    }
    
    if (invalids.length === 0) {
      enable();
      return;
    }
    
    // check if the invalids are belong to the panel (they can be moved meanwhile)
    // if all of them are outside the panel, then enable the binded element
    
    var invalid = false;
    
    var j = invalids.length;
    while (j--) {
      if (currentPanel.has(invalids[j]).length) {
        invalid = true;
        break;
      }
      invalids.splice(j, 1);
    }

    if (!invalid) {
      enable();
    }
  });
})(jQuery, CUI);

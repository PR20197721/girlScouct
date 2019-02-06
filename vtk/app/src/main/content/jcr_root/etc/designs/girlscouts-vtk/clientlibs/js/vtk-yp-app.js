/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 23);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var bind = __webpack_require__(11);

/*global toString:true*/

// utils is a library of generic helper functions non-specific to axios

var toString = Object.prototype.toString;

/**
 * Determine if a value is an Array
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Array, otherwise false
 */
function isArray(val) {
  return toString.call(val) === '[object Array]';
}

/**
 * Determine if a value is an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an ArrayBuffer, otherwise false
 */
function isArrayBuffer(val) {
  return toString.call(val) === '[object ArrayBuffer]';
}

/**
 * Determine if a value is a FormData
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an FormData, otherwise false
 */
function isFormData(val) {
  return (typeof FormData !== 'undefined') && (val instanceof FormData);
}

/**
 * Determine if a value is a view on an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a view on an ArrayBuffer, otherwise false
 */
function isArrayBufferView(val) {
  var result;
  if ((typeof ArrayBuffer !== 'undefined') && (ArrayBuffer.isView)) {
    result = ArrayBuffer.isView(val);
  } else {
    result = (val) && (val.buffer) && (val.buffer instanceof ArrayBuffer);
  }
  return result;
}

/**
 * Determine if a value is a String
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a String, otherwise false
 */
function isString(val) {
  return typeof val === 'string';
}

/**
 * Determine if a value is a Number
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Number, otherwise false
 */
function isNumber(val) {
  return typeof val === 'number';
}

/**
 * Determine if a value is undefined
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if the value is undefined, otherwise false
 */
function isUndefined(val) {
  return typeof val === 'undefined';
}

/**
 * Determine if a value is an Object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Object, otherwise false
 */
function isObject(val) {
  return val !== null && typeof val === 'object';
}

/**
 * Determine if a value is a Date
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Date, otherwise false
 */
function isDate(val) {
  return toString.call(val) === '[object Date]';
}

/**
 * Determine if a value is a File
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a File, otherwise false
 */
function isFile(val) {
  return toString.call(val) === '[object File]';
}

/**
 * Determine if a value is a Blob
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Blob, otherwise false
 */
function isBlob(val) {
  return toString.call(val) === '[object Blob]';
}

/**
 * Determine if a value is a Function
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Function, otherwise false
 */
function isFunction(val) {
  return toString.call(val) === '[object Function]';
}

/**
 * Determine if a value is a Stream
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Stream, otherwise false
 */
function isStream(val) {
  return isObject(val) && isFunction(val.pipe);
}

/**
 * Determine if a value is a URLSearchParams object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a URLSearchParams object, otherwise false
 */
function isURLSearchParams(val) {
  return typeof URLSearchParams !== 'undefined' && val instanceof URLSearchParams;
}

/**
 * Trim excess whitespace off the beginning and end of a string
 *
 * @param {String} str The String to trim
 * @returns {String} The String freed of excess whitespace
 */
function trim(str) {
  return str.replace(/^\s*/, '').replace(/\s*$/, '');
}

/**
 * Determine if we're running in a standard browser environment
 *
 * This allows axios to run in a web worker, and react-native.
 * Both environments support XMLHttpRequest, but not fully standard globals.
 *
 * web workers:
 *  typeof window -> undefined
 *  typeof document -> undefined
 *
 * react-native:
 *  typeof document.createElement -> undefined
 */
function isStandardBrowserEnv() {
  return (
    typeof window !== 'undefined' &&
    typeof document !== 'undefined' &&
    typeof document.createElement === 'function'
  );
}

/**
 * Iterate over an Array or an Object invoking a function for each item.
 *
 * If `obj` is an Array callback will be called passing
 * the value, index, and complete array for each item.
 *
 * If 'obj' is an Object callback will be called passing
 * the value, key, and complete object for each property.
 *
 * @param {Object|Array} obj The object to iterate
 * @param {Function} fn The callback to invoke for each item
 */
function forEach(obj, fn) {
  // Don't bother if no value provided
  if (obj === null || typeof obj === 'undefined') {
    return;
  }

  // Force an array if not already something iterable
  if (typeof obj !== 'object' && !isArray(obj)) {
    /*eslint no-param-reassign:0*/
    obj = [obj];
  }

  if (isArray(obj)) {
    // Iterate over array values
    for (var i = 0, l = obj.length; i < l; i++) {
      fn.call(null, obj[i], i, obj);
    }
  } else {
    // Iterate over object keys
    for (var key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        fn.call(null, obj[key], key, obj);
      }
    }
  }
}

/**
 * Accepts varargs expecting each argument to be an object, then
 * immutably merges the properties of each object and returns result.
 *
 * When multiple objects contain the same key the later object in
 * the arguments list will take precedence.
 *
 * Example:
 *
 * ```js
 * var result = merge({foo: 123}, {foo: 456});
 * console.log(result.foo); // outputs 456
 * ```
 *
 * @param {Object} obj1 Object to merge
 * @returns {Object} Result of all merge properties
 */
function merge(/* obj1, obj2, obj3, ... */) {
  var result = {};
  function assignValue(val, key) {
    if (typeof result[key] === 'object' && typeof val === 'object') {
      result[key] = merge(result[key], val);
    } else {
      result[key] = val;
    }
  }

  for (var i = 0, l = arguments.length; i < l; i++) {
    forEach(arguments[i], assignValue);
  }
  return result;
}

/**
 * Extends object a by mutably adding to it the properties of object b.
 *
 * @param {Object} a The object to be extended
 * @param {Object} b The object to copy properties from
 * @param {Object} thisArg The object to bind function to
 * @return {Object} The resulting value of object a
 */
function extend(a, b, thisArg) {
  forEach(b, function assignValue(val, key) {
    if (thisArg && typeof val === 'function') {
      a[key] = bind(val, thisArg);
    } else {
      a[key] = val;
    }
  });
  return a;
}

module.exports = {
  isArray: isArray,
  isArrayBuffer: isArrayBuffer,
  isFormData: isFormData,
  isArrayBufferView: isArrayBufferView,
  isString: isString,
  isNumber: isNumber,
  isObject: isObject,
  isUndefined: isUndefined,
  isDate: isDate,
  isFile: isFile,
  isBlob: isBlob,
  isFunction: isFunction,
  isStream: isStream,
  isURLSearchParams: isURLSearchParams,
  isStandardBrowserEnv: isStandardBrowserEnv,
  forEach: forEach,
  merge: merge,
  extend: extend,
  trim: trim
};


/***/ }),
/* 1 */
/***/ (function(module, exports) {

module.exports = React;

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = __webpack_require__(29);
exports.ULR = 'URL';
function getYearPlan() {
    var level = "" + ________app________;
    return axios_1.default.get(window.location.origin + '/content/vtkcontent/en/year-plan-library/' + level + '/_jcr_content/content/middle/par.1.json')
        .then(function (data) {
        return parseJSONVTK(data.data);
    });
}
exports.getYearPlan = getYearPlan;
function getPDF() {
    var level = "" + ________app________;
    return axios_1.default.get(window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/' + level + '.1.json')
        .then(function (d) {
        for (var a in d.data) {
            if (a.match(/.pdf/)) {
                return window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/' + level + '/' + a;
            }
        }
    });
}
exports.getPDF = getPDF;
function getMeetings(url) {
    return axios_1.default.get(window.location.origin + '/content/girlscouts-vtk/en/vtk.vtkyearplan.html?ypp=' + url).then(function (data) {
        return parseMeetings(data.data);
    });
}
exports.getMeetings = getMeetings;
function parseJSONVTK(json) {
    var parts = [];
    var currentCategory = 0;
    var counting = 0;
    var OtoR = {
        header: {},
        Category: [],
        bottom: {},
        customizedYearPlanContent: {}
    };
    for (var part in json) {
        if (part === "jcr:primaryType" || part === "sling:resourceType") {
            continue;
        }
        else {
            counting++;
        }
        if (json[part].hasOwnProperty('level')) {
            if (json[part]['level'] === "Grade") {
                parts.push(json[part]);
            }
            else if (json[part]['level'] === "Category") {
                json[part]['categories'] = [];
                parts.push(json[part]);
                currentCategory = parts.length - 1;
            }
        }
        else {
            parts[currentCategory]['categories'].push(json[part]);
        }
        if (json[part].hasOwnProperty('linkText')) {
            OtoR.customizedYearPlanContent['linkText'] = json[part]['linkText'];
            OtoR.customizedYearPlanContent['title'] = json[part]['title'];
        }
    }
    parts.forEach(function (elemen, idx) {
        if (idx == 0 && elemen.level == "Grade") {
            OtoR['header'] = elemen;
        }
        if (idx > 0 && idx < parts.length - 1 && elemen.hasOwnProperty('categories')) {
            OtoR['Category'].push(elemen);
        }
        if (idx == parts.length - 1) {
            OtoR['bottom'] = elemen;
        }
    });
    return OtoR;
}
exports.parseJSONVTK = parseJSONVTK;
function parseMeetings(json) {
    var meetings_ = {
        desc: json.desc,
        name: json.name,
        meetings: json.meetingEvents
    };
    return meetings_;
}
exports.parseMeetings = parseMeetings;
exports.modal = (function () {
    var topics = {};
    var hOP = topics.hasOwnProperty;
    return {
        subscribe: function (topic, listener) {
            // Create the topic's object if not yet created
            if (!hOP.call(topics, topic))
                topics[topic] = [];
            // Add the listener to queue
            var index = topics[topic].push(listener) - 1;
            // Provide handle back for removal of topic
            return {
                remove: function () {
                    delete topics[topic][index];
                }
            };
        },
        publish: function (topic, info) {
            // If the topic doesn't exist, or there's no listeners in queue, just leave
            if (!hOP.call(topics, topic))
                return;
            // Cycle through topics queue, fire!
            topics[topic].forEach(function (item) {
                item(info != undefined ? info : {});
            });
        },
        print: function () {
            return topics;
        }
    };
})();
exports.urlPath = '/content/dam/girlscouts-vtkcontent/images/explore/';


/***/ }),
/* 3 */
/***/ (function(module, exports) {

/*
	MIT License http://www.opensource.org/licenses/mit-license.php
	Author Tobias Koppers @sokra
*/
// css base code, injected by the css-loader
module.exports = function(useSourceMap) {
	var list = [];

	// return the list of modules as css string
	list.toString = function toString() {
		return this.map(function (item) {
			var content = cssWithMappingToString(item, useSourceMap);
			if(item[2]) {
				return "@media " + item[2] + "{" + content + "}";
			} else {
				return content;
			}
		}).join("");
	};

	// import a list of modules into the list
	list.i = function(modules, mediaQuery) {
		if(typeof modules === "string")
			modules = [[null, modules, ""]];
		var alreadyImportedModules = {};
		for(var i = 0; i < this.length; i++) {
			var id = this[i][0];
			if(typeof id === "number")
				alreadyImportedModules[id] = true;
		}
		for(i = 0; i < modules.length; i++) {
			var item = modules[i];
			// skip already imported module
			// this implementation is not 100% perfect for weird media query combinations
			//  when a module is imported multiple times with different media queries.
			//  I hope this will never occur (Hey this way we have smaller bundles)
			if(typeof item[0] !== "number" || !alreadyImportedModules[item[0]]) {
				if(mediaQuery && !item[2]) {
					item[2] = mediaQuery;
				} else if(mediaQuery) {
					item[2] = "(" + item[2] + ") and (" + mediaQuery + ")";
				}
				list.push(item);
			}
		}
	};
	return list;
};

function cssWithMappingToString(item, useSourceMap) {
	var content = item[1] || '';
	var cssMapping = item[3];
	if (!cssMapping) {
		return content;
	}

	if (useSourceMap && typeof btoa === 'function') {
		var sourceMapping = toComment(cssMapping);
		var sourceURLs = cssMapping.sources.map(function (source) {
			return '/*# sourceURL=' + cssMapping.sourceRoot + source + ' */'
		});

		return [content].concat(sourceURLs).concat([sourceMapping]).join('\n');
	}

	return [content].join('\n');
}

// Adapted from convert-source-map (MIT)
function toComment(sourceMap) {
	// eslint-disable-next-line no-undef
	var base64 = btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap))));
	var data = 'sourceMappingURL=data:application/json;charset=utf-8;base64,' + base64;

	return '/*# ' + data + ' */';
}


/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

/*
	MIT License http://www.opensource.org/licenses/mit-license.php
	Author Tobias Koppers @sokra
*/
var stylesInDom = {},
	memoize = function(fn) {
		var memo;
		return function () {
			if (typeof memo === "undefined") memo = fn.apply(this, arguments);
			return memo;
		};
	},
	isOldIE = memoize(function() {
		// Test for IE <= 9 as proposed by Browserhacks
		// @see http://browserhacks.com/#hack-e71d8692f65334173fee715c222cb805
		// Tests for existence of standard globals is to allow style-loader 
		// to operate correctly into non-standard environments
		// @see https://github.com/webpack-contrib/style-loader/issues/177
		return window && document && document.all && !window.atob;
	}),
	getElement = (function(fn) {
		var memo = {};
		return function(selector) {
			if (typeof memo[selector] === "undefined") {
				memo[selector] = fn.call(this, selector);
			}
			return memo[selector]
		};
	})(function (styleTarget) {
		return document.querySelector(styleTarget)
	}),
	singletonElement = null,
	singletonCounter = 0,
	styleElementsInsertedAtTop = [],
	fixUrls = __webpack_require__(47);

module.exports = function(list, options) {
	if(typeof DEBUG !== "undefined" && DEBUG) {
		if(typeof document !== "object") throw new Error("The style-loader cannot be used in a non-browser environment");
	}

	options = options || {};
	options.attrs = typeof options.attrs === "object" ? options.attrs : {};

	// Force single-tag solution on IE6-9, which has a hard limit on the # of <style>
	// tags it will allow on a page
	if (typeof options.singleton === "undefined") options.singleton = isOldIE();

	// By default, add <style> tags to the <head> element
	if (typeof options.insertInto === "undefined") options.insertInto = "head";

	// By default, add <style> tags to the bottom of the target
	if (typeof options.insertAt === "undefined") options.insertAt = "bottom";

	var styles = listToStyles(list, options);
	addStylesToDom(styles, options);

	return function update(newList) {
		var mayRemove = [];
		for(var i = 0; i < styles.length; i++) {
			var item = styles[i];
			var domStyle = stylesInDom[item.id];
			domStyle.refs--;
			mayRemove.push(domStyle);
		}
		if(newList) {
			var newStyles = listToStyles(newList, options);
			addStylesToDom(newStyles, options);
		}
		for(var i = 0; i < mayRemove.length; i++) {
			var domStyle = mayRemove[i];
			if(domStyle.refs === 0) {
				for(var j = 0; j < domStyle.parts.length; j++)
					domStyle.parts[j]();
				delete stylesInDom[domStyle.id];
			}
		}
	};
};

function addStylesToDom(styles, options) {
	for(var i = 0; i < styles.length; i++) {
		var item = styles[i];
		var domStyle = stylesInDom[item.id];
		if(domStyle) {
			domStyle.refs++;
			for(var j = 0; j < domStyle.parts.length; j++) {
				domStyle.parts[j](item.parts[j]);
			}
			for(; j < item.parts.length; j++) {
				domStyle.parts.push(addStyle(item.parts[j], options));
			}
		} else {
			var parts = [];
			for(var j = 0; j < item.parts.length; j++) {
				parts.push(addStyle(item.parts[j], options));
			}
			stylesInDom[item.id] = {id: item.id, refs: 1, parts: parts};
		}
	}
}

function listToStyles(list, options) {
	var styles = [];
	var newStyles = {};
	for(var i = 0; i < list.length; i++) {
		var item = list[i];
		var id = options.base ? item[0] + options.base : item[0];
		var css = item[1];
		var media = item[2];
		var sourceMap = item[3];
		var part = {css: css, media: media, sourceMap: sourceMap};
		if(!newStyles[id])
			styles.push(newStyles[id] = {id: id, parts: [part]});
		else
			newStyles[id].parts.push(part);
	}
	return styles;
}

function insertStyleElement(options, styleElement) {
	var styleTarget = getElement(options.insertInto)
	if (!styleTarget) {
		throw new Error("Couldn't find a style target. This probably means that the value for the 'insertInto' parameter is invalid.");
	}
	var lastStyleElementInsertedAtTop = styleElementsInsertedAtTop[styleElementsInsertedAtTop.length - 1];
	if (options.insertAt === "top") {
		if(!lastStyleElementInsertedAtTop) {
			styleTarget.insertBefore(styleElement, styleTarget.firstChild);
		} else if(lastStyleElementInsertedAtTop.nextSibling) {
			styleTarget.insertBefore(styleElement, lastStyleElementInsertedAtTop.nextSibling);
		} else {
			styleTarget.appendChild(styleElement);
		}
		styleElementsInsertedAtTop.push(styleElement);
	} else if (options.insertAt === "bottom") {
		styleTarget.appendChild(styleElement);
	} else {
		throw new Error("Invalid value for parameter 'insertAt'. Must be 'top' or 'bottom'.");
	}
}

function removeStyleElement(styleElement) {
	styleElement.parentNode.removeChild(styleElement);
	var idx = styleElementsInsertedAtTop.indexOf(styleElement);
	if(idx >= 0) {
		styleElementsInsertedAtTop.splice(idx, 1);
	}
}

function createStyleElement(options) {
	var styleElement = document.createElement("style");
	options.attrs.type = "text/css";

	attachTagAttrs(styleElement, options.attrs);
	insertStyleElement(options, styleElement);
	return styleElement;
}

function createLinkElement(options) {
	var linkElement = document.createElement("link");
	options.attrs.type = "text/css";
	options.attrs.rel = "stylesheet";

	attachTagAttrs(linkElement, options.attrs);
	insertStyleElement(options, linkElement);
	return linkElement;
}

function attachTagAttrs(element, attrs) {
	Object.keys(attrs).forEach(function (key) {
		element.setAttribute(key, attrs[key]);
	});
}

function addStyle(obj, options) {
	var styleElement, update, remove, transformResult;

	// If a transform function was defined, run it on the css
	if (options.transform && obj.css) {
	    transformResult = options.transform(obj.css);
	    
	    if (transformResult) {
	    	// If transform returns a value, use that instead of the original css.
	    	// This allows running runtime transformations on the css.
	    	obj.css = transformResult;
	    } else {
	    	// If the transform function returns a falsy value, don't add this css. 
	    	// This allows conditional loading of css
	    	return function() {
	    		// noop
	    	};
	    }
	}

	if (options.singleton) {
		var styleIndex = singletonCounter++;
		styleElement = singletonElement || (singletonElement = createStyleElement(options));
		update = applyToSingletonTag.bind(null, styleElement, styleIndex, false);
		remove = applyToSingletonTag.bind(null, styleElement, styleIndex, true);
	} else if(obj.sourceMap &&
		typeof URL === "function" &&
		typeof URL.createObjectURL === "function" &&
		typeof URL.revokeObjectURL === "function" &&
		typeof Blob === "function" &&
		typeof btoa === "function") {
		styleElement = createLinkElement(options);
		update = updateLink.bind(null, styleElement, options);
		remove = function() {
			removeStyleElement(styleElement);
			if(styleElement.href)
				URL.revokeObjectURL(styleElement.href);
		};
	} else {
		styleElement = createStyleElement(options);
		update = applyToTag.bind(null, styleElement);
		remove = function() {
			removeStyleElement(styleElement);
		};
	}

	update(obj);

	return function updateStyle(newObj) {
		if(newObj) {
			if(newObj.css === obj.css && newObj.media === obj.media && newObj.sourceMap === obj.sourceMap)
				return;
			update(obj = newObj);
		} else {
			remove();
		}
	};
}

var replaceText = (function () {
	var textStore = [];

	return function (index, replacement) {
		textStore[index] = replacement;
		return textStore.filter(Boolean).join('\n');
	};
})();

function applyToSingletonTag(styleElement, index, remove, obj) {
	var css = remove ? "" : obj.css;

	if (styleElement.styleSheet) {
		styleElement.styleSheet.cssText = replaceText(index, css);
	} else {
		var cssNode = document.createTextNode(css);
		var childNodes = styleElement.childNodes;
		if (childNodes[index]) styleElement.removeChild(childNodes[index]);
		if (childNodes.length) {
			styleElement.insertBefore(cssNode, childNodes[index]);
		} else {
			styleElement.appendChild(cssNode);
		}
	}
}

function applyToTag(styleElement, obj) {
	var css = obj.css;
	var media = obj.media;

	if(media) {
		styleElement.setAttribute("media", media)
	}

	if(styleElement.styleSheet) {
		styleElement.styleSheet.cssText = css;
	} else {
		while(styleElement.firstChild) {
			styleElement.removeChild(styleElement.firstChild);
		}
		styleElement.appendChild(document.createTextNode(css));
	}
}

function updateLink(linkElement, options, obj) {
	var css = obj.css;
	var sourceMap = obj.sourceMap;

	/* If convertToAbsoluteUrls isn't defined, but sourcemaps are enabled
	and there is no publicPath defined then lets turn convertToAbsoluteUrls
	on by default.  Otherwise default to the convertToAbsoluteUrls option
	directly
	*/
	var autoFixUrls = options.convertToAbsoluteUrls === undefined && sourceMap;

	if (options.convertToAbsoluteUrls || autoFixUrls){
		css = fixUrls(css);
	}

	if(sourceMap) {
		// http://stackoverflow.com/a/26603875
		css += "\n/*# sourceMappingURL=data:application/json;base64," + btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap)))) + " */";
	}

	var blob = new Blob([css], { type: "text/css" });

	var oldSrc = linkElement.href;

	linkElement.href = URL.createObjectURL(blob);

	if(oldSrc)
		URL.revokeObjectURL(oldSrc);
}


/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(process) {

var utils = __webpack_require__(0);
var normalizeHeaderName = __webpack_require__(44);

var PROTECTION_PREFIX = /^\)\]\}',?\n/;
var DEFAULT_CONTENT_TYPE = {
  'Content-Type': 'application/x-www-form-urlencoded'
};

function setContentTypeIfUnset(headers, value) {
  if (!utils.isUndefined(headers) && utils.isUndefined(headers['Content-Type'])) {
    headers['Content-Type'] = value;
  }
}

function getDefaultAdapter() {
  var adapter;
  if (typeof XMLHttpRequest !== 'undefined') {
    // For browsers use XHR adapter
    adapter = __webpack_require__(7);
  } else if (typeof process !== 'undefined') {
    // For node use HTTP adapter
    adapter = __webpack_require__(7);
  }
  return adapter;
}

var defaults = {
  adapter: getDefaultAdapter(),

  transformRequest: [function transformRequest(data, headers) {
    normalizeHeaderName(headers, 'Content-Type');
    if (utils.isFormData(data) ||
      utils.isArrayBuffer(data) ||
      utils.isStream(data) ||
      utils.isFile(data) ||
      utils.isBlob(data)
    ) {
      return data;
    }
    if (utils.isArrayBufferView(data)) {
      return data.buffer;
    }
    if (utils.isURLSearchParams(data)) {
      setContentTypeIfUnset(headers, 'application/x-www-form-urlencoded;charset=utf-8');
      return data.toString();
    }
    if (utils.isObject(data)) {
      setContentTypeIfUnset(headers, 'application/json;charset=utf-8');
      return JSON.stringify(data);
    }
    return data;
  }],

  transformResponse: [function transformResponse(data) {
    /*eslint no-param-reassign:0*/
    if (typeof data === 'string') {
      data = data.replace(PROTECTION_PREFIX, '');
      try {
        data = JSON.parse(data);
      } catch (e) { /* Ignore */ }
    }
    return data;
  }],

  timeout: 0,

  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',

  maxContentLength: -1,

  validateStatus: function validateStatus(status) {
    return status >= 200 && status < 300;
  }
};

defaults.headers = {
  common: {
    'Accept': 'application/json, text/plain, */*'
  }
};

utils.forEach(['delete', 'get', 'head'], function forEachMehtodNoData(method) {
  defaults.headers[method] = {};
});

utils.forEach(['post', 'put', 'patch'], function forEachMethodWithData(method) {
  defaults.headers[method] = utils.merge(DEFAULT_CONTENT_TYPE);
});

module.exports = defaults;

/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(12)))

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
var data_1 = __webpack_require__(2);
__webpack_require__(52);
;
;
var YplanTrack = /** @class */ (function (_super) {
    __extends(YplanTrack, _super);
    function YplanTrack() {
        var _this = _super.call(this) || this;
        _this.state = {
            isOpen: false,
            meetings: {
                desc: '',
                meetings: [],
                name: ''
            },
            imgSrc: ''
        };
        return _this;
    }
    YplanTrack.prototype.openPreview = function () {
        var _a = this.props, track = _a.track, last = _a.last, first = _a.first, isnew = _a.isnew;
        this.props.showPreview({ track: track, last: last, first: first, isnew: isnew });
    };
    YplanTrack.prototype.imgErrorHandler = function (e) {
        this.setState({ imgSrc: "" + data_1.urlPath + 'placeholder' + ".png" });
    };
    YplanTrack.prototype.componentWillMount = function () {
        var _a = this.props.track.split('###')[0].split('/'), _ = _a[0], content = _a[1], path = _a[2], template = _a[3], yearPlan = _a[4], level = _a[5], track = _a[6];
        this.setState({ 'imgSrc': "" + data_1.urlPath + level.toLowerCase() + "_" + track.toLowerCase() + ".png" });
    };
    YplanTrack.prototype.componentDidMount = function () {
        var el = this.refs.one;
        var wordArray = el.innerHTML.split(' ');
        while (el.scrollHeight > el.offsetHeight) {
            wordArray.pop();
            el.innerHTML = wordArray.join(' ') + '...';
        }
    };
    YplanTrack.prototype.render = function () {
        var _this = this;
        return (React.createElement("div", { className: "_year-plan-box " + (!(________app1________ !== this.props.track.split('###')[0]) ? ' __selected' : '') },
            React.createElement("div", { className: "__top" },
                React.createElement("p", { ref: "one", style: (!(________app1________ !== this.props.track.split('###')[0])) ? { fontWeight: 'bold', fontSize: '18px', lineHeight: '20px', height: '46px', overflow: 'hidden', margin: '0px' } : { height: '46px', overflow: 'hidden', margin: '0px' } },
                    this
                        .props
                        .track
                        .split('###')[1],
                    " ",
                    " ",
                    React.createElement("span", { style: { color: '#FAA61A', fontWeight: 'bold' } }, (this.props.isnew == 'isnew') ? ' NEW' : null))),
            React.createElement("img", { src: this.state.imgSrc, onError: function (e) { return _this.imgErrorHandler(e); }, style: {} }),
            React.createElement("div", null,
                React.createElement("a", { onClick: function () { return _this.openPreview(); }, className: "btn button " + ((________app1________ !== this.props.track.split('###')[0]) ? '' : 'selected'), style: { width: '100%' } }, "" + (!(________app1________ !== this.props.track.split('###')[0]) ? 'SELECTED - ' : ' '),
                    "PREVIEW"))));
    };
    return YplanTrack;
}(React.Component));
exports.default = YplanTrack;
function selectPlan(name, url, store) {
    store({
        name: name,
        url: url,
        is_show_meeting_lib: (url != '' || ________app________ == 'senior' || ________app________ == 'ambassador' || ________app________ == 'cadette')
            ? false : true
    }, function () {
        data_1.modal.publish('pop-select', 'open');
    });
}
exports.selectPlan = selectPlan;


/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(process) {

var utils = __webpack_require__(0);
var settle = __webpack_require__(36);
var buildURL = __webpack_require__(39);
var parseHeaders = __webpack_require__(45);
var isURLSameOrigin = __webpack_require__(43);
var createError = __webpack_require__(10);
var btoa = (typeof window !== 'undefined' && window.btoa && window.btoa.bind(window)) || __webpack_require__(38);

module.exports = function xhrAdapter(config) {
  return new Promise(function dispatchXhrRequest(resolve, reject) {
    var requestData = config.data;
    var requestHeaders = config.headers;

    if (utils.isFormData(requestData)) {
      delete requestHeaders['Content-Type']; // Let the browser set it
    }

    var request = new XMLHttpRequest();
    var loadEvent = 'onreadystatechange';
    var xDomain = false;

    // For IE 8/9 CORS support
    // Only supports POST and GET calls and doesn't returns the response headers.
    // DON'T do this for testing b/c XMLHttpRequest is mocked, not XDomainRequest.
    if (process.env.NODE_ENV !== 'test' &&
        typeof window !== 'undefined' &&
        window.XDomainRequest && !('withCredentials' in request) &&
        !isURLSameOrigin(config.url)) {
      request = new window.XDomainRequest();
      loadEvent = 'onload';
      xDomain = true;
      request.onprogress = function handleProgress() {};
      request.ontimeout = function handleTimeout() {};
    }

    // HTTP basic authentication
    if (config.auth) {
      var username = config.auth.username || '';
      var password = config.auth.password || '';
      requestHeaders.Authorization = 'Basic ' + btoa(username + ':' + password);
    }

    request.open(config.method.toUpperCase(), buildURL(config.url, config.params, config.paramsSerializer), true);

    // Set the request timeout in MS
    request.timeout = config.timeout;

    // Listen for ready state
    request[loadEvent] = function handleLoad() {
      if (!request || (request.readyState !== 4 && !xDomain)) {
        return;
      }

      // The request errored out and we didn't get a response, this will be
      // handled by onerror instead
      // With one exception: request that using file: protocol, most browsers
      // will return status as 0 even though it's a successful request
      if (request.status === 0 && !(request.responseURL && request.responseURL.indexOf('file:') === 0)) {
        return;
      }

      // Prepare the response
      var responseHeaders = 'getAllResponseHeaders' in request ? parseHeaders(request.getAllResponseHeaders()) : null;
      var responseData = !config.responseType || config.responseType === 'text' ? request.responseText : request.response;
      var response = {
        data: responseData,
        // IE sends 1223 instead of 204 (https://github.com/mzabriskie/axios/issues/201)
        status: request.status === 1223 ? 204 : request.status,
        statusText: request.status === 1223 ? 'No Content' : request.statusText,
        headers: responseHeaders,
        config: config,
        request: request
      };

      settle(resolve, reject, response);

      // Clean up request
      request = null;
    };

    // Handle low level network errors
    request.onerror = function handleError() {
      // Real errors are hidden from us by the browser
      // onerror should only fire if it's a network error
      reject(createError('Network Error', config));

      // Clean up request
      request = null;
    };

    // Handle timeout
    request.ontimeout = function handleTimeout() {
      reject(createError('timeout of ' + config.timeout + 'ms exceeded', config, 'ECONNABORTED'));

      // Clean up request
      request = null;
    };

    // Add xsrf header
    // This is only done if running in a standard browser environment.
    // Specifically not if we're in a web worker, or react-native.
    if (utils.isStandardBrowserEnv()) {
      var cookies = __webpack_require__(41);

      // Add xsrf header
      var xsrfValue = (config.withCredentials || isURLSameOrigin(config.url)) && config.xsrfCookieName ?
          cookies.read(config.xsrfCookieName) :
          undefined;

      if (xsrfValue) {
        requestHeaders[config.xsrfHeaderName] = xsrfValue;
      }
    }

    // Add headers to the request
    if ('setRequestHeader' in request) {
      utils.forEach(requestHeaders, function setRequestHeader(val, key) {
        if (typeof requestData === 'undefined' && key.toLowerCase() === 'content-type') {
          // Remove Content-Type if data is undefined
          delete requestHeaders[key];
        } else {
          // Otherwise add header to the request
          request.setRequestHeader(key, val);
        }
      });
    }

    // Add withCredentials to request if needed
    if (config.withCredentials) {
      request.withCredentials = true;
    }

    // Add responseType to request if needed
    if (config.responseType) {
      try {
        request.responseType = config.responseType;
      } catch (e) {
        if (request.responseType !== 'json') {
          throw e;
        }
      }
    }

    // Handle progress if needed
    if (typeof config.onDownloadProgress === 'function') {
      request.addEventListener('progress', config.onDownloadProgress);
    }

    // Not all browsers support upload events
    if (typeof config.onUploadProgress === 'function' && request.upload) {
      request.upload.addEventListener('progress', config.onUploadProgress);
    }

    if (config.cancelToken) {
      // Handle cancellation
      config.cancelToken.promise.then(function onCanceled(cancel) {
        if (!request) {
          return;
        }

        request.abort();
        reject(cancel);
        // Clean up request
        request = null;
      });
    }

    if (requestData === undefined) {
      requestData = null;
    }

    // Send the request
    request.send(requestData);
  });
};

/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(12)))

/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * A `Cancel` is an object that is thrown when an operation is canceled.
 *
 * @class
 * @param {string=} message The message.
 */
function Cancel(message) {
  this.message = message;
}

Cancel.prototype.toString = function toString() {
  return 'Cancel' + (this.message ? ': ' + this.message : '');
};

Cancel.prototype.__CANCEL__ = true;

module.exports = Cancel;


/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


module.exports = function isCancel(value) {
  return !!(value && value.__CANCEL__);
};


/***/ }),
/* 10 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var enhanceError = __webpack_require__(35);

/**
 * Create an Error with the specified message, config, error code, and response.
 *
 * @param {string} message The error message.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 @ @param {Object} [response] The response.
 * @returns {Error} The created error.
 */
module.exports = function createError(message, config, code, response) {
  var error = new Error(message);
  return enhanceError(error, config, code, response);
};


/***/ }),
/* 11 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


module.exports = function bind(fn, thisArg) {
  return function wrap() {
    var args = new Array(arguments.length);
    for (var i = 0; i < args.length; i++) {
      args[i] = arguments[i];
    }
    return fn.apply(thisArg, args);
  };
};


/***/ }),
/* 12 */
/***/ (function(module, exports) {

// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };


/***/ }),
/* 13 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __assign = (this && this.__assign) || Object.assign || function(t) {
    for (var s, i = 1, n = arguments.length; i < n; i++) {
        s = arguments[i];
        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
            t[p] = s[p];
    }
    return t;
};
Object.defineProperty(exports, "__esModule", { value: true });
__webpack_require__(51);
var React = __webpack_require__(1);
var data = __webpack_require__(2);
var category_1 = __webpack_require__(15);
var vtk_gray_1 = __webpack_require__(21);
var vtk_popup_1 = __webpack_require__(22);
var year_plan_track_1 = __webpack_require__(6);
var head_1 = __webpack_require__(16);
var meetings_1 = __webpack_require__(19);
;
;
var VtkMainYp = /** @class */ (function (_super) {
    __extends(VtkMainYp, _super);
    function VtkMainYp() {
        var _this = _super.call(this) || this;
        _this.meetingsPick = {};
        _this.state = {
            pdf: '',
            data: {
                name: '',
                url: '',
                is_show_meeting_lib: true
            },
            meeting: {
                desc: '',
                name: '',
                meetings: []
            },
            track: '',
            showTracks: false,
            showPreview: false
        };
        return _this;
    }
    VtkMainYp.prototype.store = function (state, func) {
        this.setState({
            data: state
        }, func);
    };
    VtkMainYp.prototype.reset = function () {
        this.setState({
            data: {
                name: '',
                url: '',
                is_show_meeting_lib: true
            }
        });
    };
    VtkMainYp.prototype.componentDidMount = function () {
        var _this = this;
        data
            .getPDF()
            .then(function (url) {
            _this.setState({ pdf: url });
        });
    };
    VtkMainYp.prototype.clickHander = function () {
        year_plan_track_1.selectPlan('Custom Year Plan', '', this.store.bind(this));
    };
    VtkMainYp.prototype.openTracks = function () {
        this.setState({
            showTracks: !this.state.showTracks,
            showPreview: false
        });
    };
    VtkMainYp.prototype.scrollto = function () {
        $('html, body').animate({ scrollTop: document.getElementsByClassName('__meetings')[0].getBoundingClientRect().top + window.scrollY }, 'slow');
    };
    VtkMainYp.prototype.showPreview = function (props) {
        var _this = this;
        if (this.meetingsPick.hasOwnProperty(props.track)) {
            this.setState({
                meeting: this.meetingsPick[props.track],
                track: props.track
            });
        }
        else {
            data.getMeetings(props.track.split('###')[0]).then(function (meeting) {
                _this.meetingsPick[props.track] = meeting;
                _this.setState({
                    meeting: meeting,
                    track: props.track
                });
            }).catch(function () {
                console.log('ERROR');
            });
        }
        this.setState({
            showPreview: !this.state.showPreview
        }, this.scrollto);
    };
    VtkMainYp.prototype.closePreview = function () {
        this.setState({
            showPreview: false
        }, this.scrollto);
    };
    VtkMainYp.prototype.render = function () {
        var _this = this;
        var _a = this.props.data, header = _a.header, bottom = _a.bottom, customizedYearPlanContent = _a.customizedYearPlanContent;
        var title = header.title, subtitle = header.subtitle;
        function renderChild(state) {
            return (________isYearPlan________ == false)
                ? React.createElement("div", { className: state.data.name },
                    React.createElement("p", null,
                        React.createElement("b", null,
                            "You have selected the Year Plan below for ",
                            ________app________
                                .charAt(0)
                                .toUpperCase() + ________app________.slice(1),
                            (________troopName________.match(/troop/i))
                                ? null
                                : 'Troop',
                            '  ', ________troopName________ + ".",
                            '  ',
                            " Is this correct?")),
                    React.createElement("table", { style: {
                            width: '70%'
                        } },
                        React.createElement("tbody", null,
                            React.createElement("tr", null,
                                React.createElement("td", null, "Troop Year Plan"),
                                React.createElement("td", null, "" + state.data.name)))),
                    React.createElement("table", { style: {
                            width: '70%',
                            margin: '0 auto'
                        } },
                        React.createElement("tbody", null,
                            React.createElement("tr", null,
                                React.createElement("td", { style: {
                                        textAlign: 'center'
                                    } },
                                    React.createElement("div", { className: "btn button", style: {
                                            width: '100%'
                                        }, onClick: function () {
                                            data
                                                .modal
                                                .publish('pop-select', 'close');
                                        } }, "NO, CANCEL")),
                                React.createElement("td", { style: {
                                        textAlign: 'center'
                                    } },
                                    React.createElement("div", { className: "btn button", style: {
                                            width: '100%'
                                        }, onClick: function () {
                                            chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib);
                                            if (state.data.name === 'Custom Year Plan') {
                                                data
                                                    .modal
                                                    .publish('pop-select', 'close');
                                            }
                                        } }, "YES, SELECT"))))))
                : React.createElement("div", { className: state.data.name },
                    React.createElement("p", null,
                        React.createElement("b", null,
                            "You want to replace your current Year Plan with the new Year Plan listed below for ", "" + (________app________
                            .charAt(0)
                            .toUpperCase() + ________app________.slice(1)),
                            (________troopName________.match(/troop/i))
                                ? null
                                : "Troop",
                            ' ', ________troopName________ + ".",
                            ' ',
                            "Is this correct?")),
                    React.createElement("table", null,
                        React.createElement("tbody", null,
                            React.createElement("tr", null,
                                React.createElement("td", null, "Current Year Plan:"),
                                React.createElement("td", null, "" + ________currentYearPlanName________,
                                    React.createElement("br", null),
                                    React.createElement("b", null, "IMPORTANT:"),
                                    ' ',
                                    React.createElement("span", { style: {
                                            color: '#FAA61A'
                                        } },
                                        React.createElement("b", null, "Any customizations you made will be lost.")))),
                            React.createElement("tr", null,
                                React.createElement("td", null, "New Year Plan"),
                                React.createElement("td", null, "" + state.data.name)))),
                    React.createElement("table", { style: {
                            width: '70%',
                            margin: '0 auto'
                        } },
                        React.createElement("tbody", null,
                            React.createElement("tr", null,
                                React.createElement("td", { style: {
                                        textAlign: 'center'
                                    } },
                                    React.createElement("div", { className: "btn button", style: {
                                            width: '100%'
                                        }, onClick: function () {
                                            data
                                                .modal
                                                .publish('pop-select', 'close');
                                        } }, "NO, CANCEL")),
                                React.createElement("td", { style: {
                                        textAlign: 'center'
                                    } },
                                    React.createElement("div", { className: "btn button", style: {
                                            width: '100%'
                                        }, onClick: function () {
                                            chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib);
                                            if (state.data.name === 'Custom Year Plan') {
                                                data
                                                    .modal
                                                    .publish('pop-select', 'close');
                                            }
                                        } }, "YES, SELECT"))))));
        }
        var renderActions = function () { return (React.createElement("div", { className: "columns small-24 medium-10", style: { textAlign: 'center' } },
            React.createElement("button", { onClick: function () { return _this.closePreview(); }, className: "btn button btn-line" }, "CLOSE PREVIEW"),
            React.createElement("button", { className: "btn button btn-default " + ((________currentYearPlanName________ === _this.state.meeting.name) ? ' selected inactive' : ''), onClick: function () {
                    if (!(________currentYearPlanName________ === _this.state.meeting.name)) {
                        year_plan_track_1.selectPlan(_this.state.track.split('###')[1], _this.state.track.split('###')[0], _this.store.bind(_this));
                    }
                } }, (________currentYearPlanName________ === _this.state.meeting.name) ? 'SELECTED' : 'SELECT TRACK'))); };
        return (React.createElement("div", null,
            React.createElement("div", { className: "__padding" },
                React.createElement("div", { className: "columns small-22 medium-20 small-centered medium-centered", style: {
                        padding: '0px'
                    } },
                    React.createElement("h2", { className: "" }, title),
                    React.createElement("div", { className: "row" },
                        React.createElement("div", { className: "small-24  columns" },
                            React.createElement("p", { style: {
                                    'marginBottom': '30px'
                                } }, subtitle))))),
            (________app1________ && ________currentYearPlanName________ || ________currentYearPlanName________ === 'Custom Year Plan')
                ? React.createElement("div", { className: "__padding" },
                    React.createElement("div", { className: "columns small-20 small-centered" },
                        React.createElement("div", { className: "_intro " },
                            React.createElement("p", { style: { fontSize: '25px', fontWeight: 'bold' } },
                                React.createElement("span", null,
                                    React.createElement("i", { style: { color: 'orange', float: 'initial', display: 'inline-block', fontSize: '50px', marginBottom: '10px' }, className: "icon-check" })),
                                "The Troop's Year Plan is set",
                                React.createElement("br", null),
                                " ",
                                React.createElement("span", { style: { fontSize: '18px' } }, ________currentYearPlanName________)),
                            React.createElement("p", null, "To add, delete, or change a meeting, go to your Year Plan and click on the date square"),
                            React.createElement("a", { href: "/content/girlscouts-vtk/en/vtk.html", className: "btn button btn-default", style: { maxWidth: '300px' } }, "View my year plan"),
                            " ",
                            React.createElement("br", null),
                            " ",
                            React.createElement("br", null),
                            React.createElement("p", null, "To start over with a new Year Plan, use the choices below"))))
                : null,
            React.createElement("div", { className: "__padding" },
                React.createElement("div", { className: "_main_boxes columns small-22 medium-20 small-centered medium-centered " + ((this.state.showTracks)
                        ? '__OPEN'
                        : '__CLOSE') },
                    React.createElement("div", { onClick: function () { return _this.clickHander(); }, className: "columns  medium-24 large-12 _box_wrap" },
                        React.createElement("div", { className: "_box __library" },
                            React.createElement("div", { className: "__img" }),
                            React.createElement("h3", null, "Select Your Own"),
                            React.createElement("p", null, "Search or filter to select the badges and awards that fit the style of your troop."),
                            React.createElement("a", { className: "btn button", style: {
                                    width: '100%'
                                } }, "start adding Petals, Badges or Journeys"))),
                    React.createElement("div", { onClick: function () { return _this.openTracks(); }, className: "columns medium-24 large-12 _box_wrap" },
                        React.createElement("div", { className: "_box __tracks" },
                            React.createElement("div", { className: "__img " }),
                            React.createElement("h3", null, "Pre-selected Tracks"),
                            React.createElement("p", null, "Not sure what to pick? These tracks get your troop Year Plan started and let you add choices as well."),
                            React.createElement("a", { className: "btn button", style: {
                                    width: '100%'
                                } }, "view popular tracks"))),
                    React.createElement("br", null),
                    React.createElement("br", null))),
            React.createElement("div", { className: "_back_box_tracks " + ((this.state.showTracks)
                    ? '__OPEN'
                    : '__CLOSE'), style: {
                    display: 'inline-block'
                } }, (!this.state.showPreview)
                ? React.createElement("div", { className: "__categories_main" },
                    React.createElement(head_1.default, null),
                    " ",
                    (this.state.showTracks)
                        ? React.createElement("div", { className: "__categories-wrap" },
                            this
                                .props
                                .data
                                .Category
                                .map(function (cat, idx, arr) {
                                return React.createElement("div", { key: 'category-' + idx },
                                    React.createElement(category_1.default, __assign({}, cat, { store: _this
                                            .store
                                            .bind(_this), idx: idx, showPreview: _this
                                            .showPreview
                                            .bind(_this) })));
                            }),
                            React.createElement("div", { className: "row", style: { clear: 'both' } },
                                React.createElement("div", { className: "columns small-20 small-centered" },
                                    this.state.pdf
                                        ? React.createElement("div", { className: "columns small-24 medium-10" },
                                            React.createElement("i", { className: "icon-pdf-file-extension" }),
                                            React.createElement("a", { style: { paddingTop: '7px', display: 'inline-block' }, target: "_blank", href: this.state.pdf }, "Pre-Selected Tracks Overview "))
                                        : null,
                                    React.createElement("div", { className: this.state.pdf ? "columns small-24 medium-4 end" : 'columns small-24', style: !this.state.pdf ? { textAlign: 'center' } : {} },
                                        React.createElement("button", { onClick: function () { return _this.openTracks(); }, className: 'btn button btn-line' }, "CLOSE")))))
                        : null)
                : React.createElement("div", { className: "__list__tracks columns small-24" },
                    React.createElement(head_1.default, null),
                    React.createElement("div", { className: "columns small-22 small-centered" },
                        React.createElement("div", { className: "__meetings" },
                            React.createElement("div", { className: "columns medium-22 small-22 small-centered medium-centered" },
                                React.createElement("p", null,
                                    React.createElement("b", null, "Pre-selected Year Plan Track")),
                                React.createElement("p", null, this.state.meeting.desc),
                                React.createElement("div", { style: { display: 'inline-block', width: '100%' } },
                                    React.createElement("div", { className: "columns small-24 medium-14" },
                                        React.createElement("p", { style: { fontSize: '18px' } },
                                            React.createElement("b", null, this.state.meeting.name))),
                                    renderActions()),
                                React.createElement("br", null)),
                            React.createElement("div", { className: "columns medium-22 small-24 medium-centered" },
                                React.createElement(meetings_1.default, { meetings: this.state.meeting.meetings }),
                                React.createElement("br", null))),
                        React.createElement("div", { className: "columns small-22 small-centered" },
                            React.createElement("div", { className: "columns small-24 medium-13 medium-offset-1" },
                                React.createElement("p", { style: { padding: '0 30px 0 0' } }, "If you select this track, the meetings you see here will automatically fill your Year Plan calendar. You can add, change or delete meetings at any time.")),
                            renderActions())))),
            React.createElement(vtk_gray_1.default, null),
            React.createElement(vtk_popup_1.default, { name: "pop-select", title: "SELECT YEAR PLAN" }, renderChild(this.state))));
    };
    return VtkMainYp;
}(React.Component));
exports.default = VtkMainYp;


/***/ }),
/* 14 */
/***/ (function(module, exports) {

module.exports = ReactDOM;

/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __assign = (this && this.__assign) || Object.assign || function(t) {
    for (var s, i = 1, n = arguments.length; i < n; i++) {
        s = arguments[i];
        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
            t[p] = s[p];
    }
    return t;
};
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
var header_1 = __webpack_require__(17);
var year_plan_track_1 = __webpack_require__(6);
;
;
var Category = /** @class */ (function (_super) {
    __extends(Category, _super);
    function Category() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Category.prototype.render = function () {
        var _this = this;
        return (React.createElement("div", { className: "columns small-24" },
            React.createElement("div", { className: "__categories" },
                React.createElement(header_1.default, { title: this.props.title, subTitle: this.props.subtitle }),
                React.createElement("div", { className: "row" },
                    React.createElement("div", { className: "columns small-20 small-centered" }, this.props.categories.map(function (track, idx, array) {
                        return React.createElement(year_plan_track_1.default, __assign({ key: 'YplanTrack' + idx + track.track }, track, { first: idx == 0 && _this.props.idx == 0, last: array.length - 1 == idx, store: _this.props.store, showPreview: _this.props.showPreview }));
                    }))))));
    };
    return Category;
}(React.Component));
exports.default = Category;


/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
__webpack_require__(48);
var Head = /** @class */ (function (_super) {
    __extends(Head, _super);
    function Head() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Head.prototype.render = function () {
        return (React.createElement("div", null,
            React.createElement("div", { className: "columns small-24 small-centered" },
                React.createElement("div", { className: "columns large-offset-16 medium-offset-11 small-offset-9  large-2 medium-2 small-3 end", style: { textAlign: 'center' } },
                    React.createElement("span", { className: "_triangle" }))),
            React.createElement("div", { className: "__level-logo columns small-22 small-centered" },
                React.createElement("div", { className: "small-8 small-offset-16 medium-4 medium-offset-18 large-6 large-offset-18 end" },
                    React.createElement("div", { style: { padding: '10px 0 0px 0', clear: 'both' } }),
                    React.createElement("img", { className: "bg-logo logo-" + ________app________, src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/GS_" + ________app________ + ".png", style: { width: 'auto', height: '43px', float: 'right' } })))));
    };
    return Head;
}(React.Component));
exports.default = Head;


/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
;
;
var Header = /** @class */ (function (_super) {
    __extends(Header, _super);
    function Header() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Header.prototype.render = function () {
        return (React.createElement("div", { className: "row" },
            React.createElement("div", { className: "columns small-20 small-centered", style: { padding: '0px' } },
                React.createElement("div", { className: "__header" },
                    React.createElement("span", { style: { fontSize: '18px' } },
                        React.createElement("b", null, this.props.title)),
                    ' ',
                    this.props.subTitle))));
    };
    return Header;
}(React.Component));
exports.default = Header;


/***/ }),
/* 18 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
__webpack_require__(49);
var React = __webpack_require__(1);
var tree_1 = __webpack_require__(20);
;
;
var Meeting = /** @class */ (function (_super) {
    __extends(Meeting, _super);
    function Meeting() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Meeting.prototype.render = function () {
        var _this = this;
        var showClickRequiment = '';
        var _onClick = function () { };
        if (this.props.meetingInfo.req) {
            showClickRequiment = "_requirement_modal";
            _onClick = function (e) {
                requirementsModal({ reqTitle: _this.props.meetingInfo.reqTitle, id: _this.props.meetingInfo.id, name: _this.props.meetingInfo.name, req: _this.props.meetingInfo.req }, _this.props.anyOutdoorActivityInMeetingAvailable, _this.props.anyOutdoorActivityInMeeting);
            };
        }
        return (React.createElement("div", { className: "meeting" },
            React.createElement("div", { className: "square-small show-for-small-only" },
                React.createElement("p", null, "Meeting"),
                React.createElement("p", { className: 'postion' }, this.props.idx + 1)),
            React.createElement("div", { className: "arrowGreen-small show-for-small-only" }),
            React.createElement("div", { className: "square show-for-medium-up" },
                React.createElement("p", null, "Meeting"),
                React.createElement("p", { className: 'postion' }, this.props.idx + 1)),
            React.createElement("div", { className: "arrowGreen show-for-medium-up" }),
            React.createElement("div", { className: "body" },
                React.createElement("div", { className: "small-24 column" },
                    React.createElement("div", { className: (this.props.meetingInfo.activities && this.props.meetingInfo.activities.length > 0) ? "_text small-text-center medium-text-left small-24  medium-18 column" : "_text small-text-center medium-text-left small-24  medium-21 column", style: { fontSize: '14px' } },
                        React.createElement("div", { className: "truncate" }, this.props.meetingInfo.name.toUpperCase()),
                        React.createElement("div", { className: "truncate" }, this.props.meetingInfo.cat),
                        React.createElement("div", { className: "truncate" }, this.props.meetingInfo.blurb)),
                    (this.props.meetingInfo.activities && this.props.meetingInfo.activities.length > 0) ?
                        React.createElement("div", { className: "small-24 medium-3 column small-text-center", style: { textAlign: "center" } }, (this.props.anyOutdoorActivityInMeetingAvailable) ? React.createElement("img", { src: tree_1.tree, style: { 'width': '60px', 'height': '60px' }, alt: "" }) : null) : null,
                    React.createElement("div", { className: "small-24 medium-3 column small-text-center", style: { textAlign: 'center' } },
                        React.createElement("img", { onClick: function (e) { return _onClick(e); }, className: showClickRequiment, src: "/content/dam/girlscouts-vtk/local/icon/meetings/" + this.props.meetingInfo.id + ".png", style: { 'width': '60px', 'height': '60px' }, alt: "" }))))));
    };
    return Meeting;
}(React.Component));
exports.default = Meeting;


/***/ }),
/* 19 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __assign = (this && this.__assign) || Object.assign || function(t) {
    for (var s, i = 1, n = arguments.length; i < n; i++) {
        s = arguments[i];
        for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
            t[p] = s[p];
    }
    return t;
};
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
var meeting_1 = __webpack_require__(18);
;
;
var Meetings = /** @class */ (function (_super) {
    __extends(Meetings, _super);
    function Meetings() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Meetings.prototype.render = function () {
        return (React.createElement("div", { className: "list-meetings" }, (this.props.meetings.length) ? this.props.meetings.map(function (meeting, idx) { return React.createElement(meeting_1.default, __assign({ key: meeting.meetingInfo.position + '-meeting-' + idx, idx: idx }, meeting)); }) : null));
    };
    return Meetings;
}(React.Component));
exports.default = Meetings;


/***/ }),
/* 20 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
exports.tree = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABGdBTUEAALGPC/xhBQAACjFpQ0NQSUNDIFByb2ZpbGUAAEiJnZZ3VFPZFofPvTe9UJIQipTQa2hSAkgNvUiRLioxCRBKwJAAIjZEVHBEUZGmCDIo4ICjQ5GxIoqFAVGx6wQZRNRxcBQblklkrRnfvHnvzZvfH/d+a5+9z91n733WugCQ/IMFwkxYCYAMoVgU4efFiI2LZ2AHAQzwAANsAOBws7NCFvhGApkCfNiMbJkT+Be9ug4g+fsq0z+MwQD/n5S5WSIxAFCYjOfy+NlcGRfJOD1XnCW3T8mYtjRNzjBKziJZgjJWk3PyLFt89pllDznzMoQ8GctzzuJl8OTcJ+ONORK+jJFgGRfnCPi5Mr4mY4N0SYZAxm/ksRl8TjYAKJLcLuZzU2RsLWOSKDKCLeN5AOBIyV/w0i9YzM8Tyw/FzsxaLhIkp4gZJlxTho2TE4vhz89N54vFzDAON40j4jHYmRlZHOFyAGbP/FkUeW0ZsiI72Dg5ODBtLW2+KNR/Xfybkvd2ll6Ef+4ZRB/4w/ZXfpkNALCmZbXZ+odtaRUAXesBULv9h81gLwCKsr51Dn1xHrp8XlLE4ixnK6vc3FxLAZ9rKS/o7/qfDn9DX3zPUr7d7+VhePOTOJJ0MUNeN25meqZExMjO4nD5DOafh/gfB/51HhYR/CS+iC+URUTLpkwgTJa1W8gTiAWZQoZA+J+a+A/D/qTZuZaJ2vgR0JZYAqUhGkB+HgAoKhEgCXtkK9DvfQvGRwP5zYvRmZid+8+C/n1XuEz+yBYkf45jR0QyuBJRzuya/FoCNCAARUAD6kAb6AMTwAS2wBG4AA/gAwJBKIgEcWAx4IIUkAFEIBcUgLWgGJSCrWAnqAZ1oBE0gzZwGHSBY+A0OAcugctgBNwBUjAOnoAp8ArMQBCEhcgQFVKHdCBDyByyhViQG+QDBUMRUByUCCVDQkgCFUDroFKoHKqG6qFm6FvoKHQaugANQ7egUWgS+hV6ByMwCabBWrARbAWzYE84CI6EF8HJ8DI4Hy6Ct8CVcAN8EO6ET8OX4BFYCj+BpxGAEBE6ooswERbCRkKReCQJESGrkBKkAmlA2pAepB+5ikiRp8hbFAZFRTFQTJQLyh8VheKilqFWoTajqlEHUJ2oPtRV1ChqCvURTUZros3RzugAdCw6GZ2LLkZXoJvQHeiz6BH0OPoVBoOhY4wxjhh/TBwmFbMCsxmzG9OOOYUZxoxhprFYrDrWHOuKDcVysGJsMbYKexB7EnsFO459gyPidHC2OF9cPE6IK8RV4FpwJ3BXcBO4GbwS3hDvjA/F8/DL8WX4RnwPfgg/jp8hKBOMCa6ESEIqYS2hktBGOEu4S3hBJBL1iE7EcKKAuIZYSTxEPE8cJb4lUUhmJDYpgSQhbSHtJ50i3SK9IJPJRmQPcjxZTN5CbiafId8nv1GgKlgqBCjwFFYr1Ch0KlxReKaIVzRU9FRcrJivWKF4RHFI8akSXslIia3EUVqlVKN0VOmG0rQyVdlGOVQ5Q3mzcovyBeVHFCzFiOJD4VGKKPsoZyhjVISqT2VTudR11EbqWeo4DUMzpgXQUmmltG9og7QpFYqKnUq0Sp5KjcpxFSkdoRvRA+jp9DL6Yfp1+jtVLVVPVb7qJtU21Suqr9XmqHmo8dVK1NrVRtTeqTPUfdTT1Lepd6nf00BpmGmEa+Rq7NE4q/F0Dm2OyxzunJI5h+fc1oQ1zTQjNFdo7tMc0JzW0tby08rSqtI6o/VUm67toZ2qvUP7hPakDlXHTUegs0PnpM5jhgrDk5HOqGT0MaZ0NXX9dSW69bqDujN6xnpReoV67Xr39An6LP0k/R36vfpTBjoGIQYFBq0Gtw3xhizDFMNdhv2Gr42MjWKMNhh1GT0yVjMOMM43bjW+a0I2cTdZZtJgcs0UY8oyTTPdbXrZDDazN0sxqzEbMofNHcwF5rvNhy3QFk4WQosGixtMEtOTmcNsZY5a0i2DLQstuyyfWRlYxVtts+q3+mhtb51u3Wh9x4ZiE2hTaNNj86utmS3Xtsb22lzyXN+5q+d2z31uZ27Ht9tjd9Oeah9iv8G+1/6Dg6ODyKHNYdLRwDHRsdbxBovGCmNtZp13Qjt5Oa12Oub01tnBWex82PkXF6ZLmkuLy6N5xvP48xrnjbnquXJc612lbgy3RLe9blJ3XXeOe4P7Aw99D55Hk8eEp6lnqudBz2de1l4irw6v12xn9kr2KW/E28+7xHvQh+IT5VPtc99XzzfZt9V3ys/eb4XfKX+0f5D/Nv8bAVoB3IDmgKlAx8CVgX1BpKAFQdVBD4LNgkXBPSFwSGDI9pC78w3nC+d3hYLQgNDtoffCjMOWhX0fjgkPC68JfxhhE1EQ0b+AumDJgpYFryK9Issi70SZREmieqMVoxOim6Nfx3jHlMdIY61iV8ZeitOIE8R1x2Pjo+Ob4qcX+izcuXA8wT6hOOH6IuNFeYsuLNZYnL74+BLFJZwlRxLRiTGJLYnvOaGcBs700oCltUunuGzuLu4TngdvB2+S78ov508kuSaVJz1Kdk3enjyZ4p5SkfJUwBZUC56n+qfWpb5OC03bn/YpPSa9PQOXkZhxVEgRpgn7MrUz8zKHs8yzirOky5yX7Vw2JQoSNWVD2Yuyu8U02c/UgMREsl4ymuOWU5PzJjc690iecp4wb2C52fJNyyfyffO/XoFawV3RW6BbsLZgdKXnyvpV0Kqlq3pX668uWj2+xm/NgbWEtWlrfyi0LiwvfLkuZl1PkVbRmqKx9X7rW4sVikXFNza4bKjbiNoo2Di4ae6mqk0fS3glF0utSytK32/mbr74lc1XlV992pK0ZbDMoWzPVsxW4dbr29y3HShXLs8vH9sesr1zB2NHyY6XO5fsvFBhV1G3i7BLsktaGVzZXWVQtbXqfXVK9UiNV017rWbtptrXu3m7r+zx2NNWp1VXWvdur2DvzXq/+s4Go4aKfZh9OfseNkY39n/N+rq5SaOptOnDfuF+6YGIA33Njs3NLZotZa1wq6R18mDCwcvfeH/T3cZsq2+nt5ceAockhx5/m/jt9cNBh3uPsI60fWf4XW0HtaOkE+pc3jnVldIl7Y7rHj4aeLS3x6Wn43vL7/cf0z1Wc1zleNkJwomiE59O5p+cPpV16unp5NNjvUt675yJPXOtL7xv8GzQ2fPnfM+d6ffsP3ne9fyxC84Xjl5kXey65HCpc8B+oOMH+x86Bh0GO4cch7ovO13uGZ43fOKK+5XTV72vnrsWcO3SyPyR4etR12/eSLghvcm7+ehW+q3nt3Nuz9xZcxd9t+Se0r2K+5r3G340/bFd6iA9Puo9OvBgwYM7Y9yxJz9l//R+vOgh+WHFhM5E8yPbR8cmfScvP174ePxJ1pOZp8U/K/9c+8zk2Xe/ePwyMBU7Nf5c9PzTr5tfqL/Y/9LuZe902PT9VxmvZl6XvFF/c+At623/u5h3EzO577HvKz+Yfuj5GPTx7qeMT59+A/eE8/vsbQFrAAAAIGNIUk0AAHomAACAhAAA+gAAAIDoAAB1MAAA6mAAADqYAAAXcJy6UTwAAAAGYktHRAD/AP8A/6C9p5MAAAAJcEhZcwAACxMAAAsTAQCanBgAAAAHdElNRQfgChEVJQ30KlS4AAAGv0lEQVR42u2aa4gVZRjHf3POetZrq4WKtpSXpYtmeVlSSVMzwjDNT5oRRX2pjEJJqCjLtK0QSaQgo1JLu2gQpmUtFlFEmmXQhaC09VJL6eZ6N93O7vTh/F/O0zTnrLrrmdnjvjDMnPedmfPcn//zvAPt49weyRj8fxLwz0Xhe838LmoL8KT1McB0YAdwLCohRKX5C4C9EsR7UVpBVFZ3m5g/qfNIzScKSUwiQkHcrHOjztPPBStwAu8H7Jfm3XEA6F/sQijReZmYPgHsBtL6/UTgvqL0/ZGG4WpgnNzAB/YAPYvRChwzHYGvxew/wDWa/8i4wvMxAWlnRftPhTAKMN7MHwYqYhCkWz3wDTcpbwfQXfMpndcbISwqJitw5r/RMDglJNiNNrFhH3BhMcQCp8GZhvnXA8yPB+7X9Vpz3zNt3Qqc5noCNWKqzmgWoAvwm9b6A1eZjGBxQaIta3+R0er8gN9foiLIB+7S3BvFkBGcxgYC9WJmq5l3DF0M/K31OQYnNGjuGDC4LVqBI3alGEkD9+VgpFpo8Aoz97axglfamgCcdicbJraLyemmFngfuEG/ewVcY6SAkrOCQW1FCC7wlQE/iYFa4Etdj9b6C/r9XIjVeCGxYElbiQWOwCWG+KXALuAHrV0GHNXa3QHNXwqcr+tKEwsOGxeJrRWUmDrfMb8euCOA7haYWuB68/wwxYqZZu5N86534gyMnOYHkG1zNUijq/T7Rt2zwaC93uYdszT/uJkbAPxlOkdDWtsKEq3k941AZzHbCziugPcz8CPwAfCJ7v9c53clrJSJGwB9TOVYI4E5N5kRRytwQnzRmOvHwAhgktGadZUKoDTgOnMDTRHP1AguI+wFyuMUCxwRFwEHAy0ue6zMoznPpMI5epcz/wpggoHLPvBQnDKCI74rsElobxWwDthpiP7VEJxPCG5UmTgSFOa8uKVEz5iyI6oU+NYQvdgQnVRKS4S8pzSAE5oULOtl/ssk7NjFAS/gEksN83tMbkfB0QeuDvFlJ8BBpnnysponZXksJlY4YIzRnm/gb0JM/KL5iQGmE8ZCAB41Qqwy98YSCHmGwM2G8FeVCe7V+pOarzFW4eUJrlXmXc/GGQmGaa0WeFjXC7VeE2CmxAhgLrAC6JQHVs8IaaXFhvlJMnvX0ZlNtgVeKVzgGBkXYKSDEc6KEE1vMh2ly+OYBsuU6hyD6zS/CnhJ16OE9ZcHGHTn5eb52YEiqR/wh2msdI2LOzgtLDTEfyONho3+hikvcO5NtnM8P4TB64AjWl8T8nxkzI8yfT0fWC0NLhXR5Mj5ubDEcIMHPGAocBMwVpblA4dUaEVmBTbqf0F2ozOI3OplvkGg1BwSxACidA54/WBrxIKWRtMOqgIxWkOCSKmIcfv/acOsH3KukBB/F1ONQoxJ+f9WVYqeGipf6X2RfWDlTG+IzH21avm1IQ3NqWpxDQzxf/eejaZUdoq5xfQF79Rc5zghwiAB3YHvRfRuMpsiPYTnffmxi+6lgWe36J4hAdOeFVIJenEqhhJGY3b/b1oA+683zywns1OMyRhv6b7bjRU465hmegLz4lYPOObvMcy/ZtaHKjVeawod2/rqqPNi/r8P4Jn3T1Wnydd/xQIRJk1D0+0AbSfzCZwlMGU09gD/3SEuNXneBz7VvfZwVnKraahOiFoIjqFOwDZTAU5s5jlX4IwIyeMTyLTN8wVd12TdAfSNEhaHffUx1wTDcoGVwWL2SjK7wQOkyY56R4k0nAyk15LA4Syl3MDu6pY2eL0WBL4mFSabVQvslO9XCvJ2U8pyxDeoQqyTCZ9n/t8VUU4oJ/OUySfI7Cq7j6nWkPnoMm3oKhgGcAVMOk9D9GwdaYM8V5s44RXCAhx62yCcfkw9uzp1cPfrOCJN95DQjqtjVKlgmSKzRf6ZQFK5QYzJ06RvrGB50qDPsw6FZ6lnV0tm/++QYSDXeEQCOCpXqVYv4WmtlQjnb5FwyhRou0noKcHmvso23cjsNG8z7hQJCrTuYft7ro+XMp0fa8ruAwi3D3jcFFDNjS5kvzY7I4tuiQX4hlnfSD6XBpxZHjFzCwSZ+5Dd8dklTOHgrp/jv5vkepZx/0yD2ZmOJpl8o9Foc8MJoF5V3mTgTyOAg2Rb4o05jibDuHca/93qAjhdi4HMXj/qCk9R/dDDQOIDJg36p/DOFpXDUcBI6wJ9hB0qjC8XtMMThQDqzPUwHWHrHgVodiQL7AKeGGwQJN6nIPid4sGHwGNKkUU/kkpjp7JbXHQjkafJWlDmo5S0F5Ih2kf7aB+FHf8Cgi8PV9LesHwAAAAASUVORK5CYII=";
exports.pdf = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAUCAYAAACEYr13AAAAAXNSR0IArs4c6QAAAY5JREFUOBGd000rBVEYwHFcXK6XUhQbK8XC2wpZ6Ja7sbCyUJY2JFsLK3tLxQfwAdhbiLKQImWtK5SrkPe3vP3/415uUWY89TtzZubMc54zc6aw4Dvq6e7iHRmUIZ5lvwRv8P4FrtCPIAppl+DN8eDKzybGpWo0oAMvaC2mMabRhXV04rd45eJ11gnHG8RN0IxJOEMjrCJMmCBWRLMIE1XC2Pg8/NlakUsveMSwnYixz/g2K7hEacSHHW4FaRMcw88TNXymxAR+37AvLn8SKyg3geV7kosknVlMYBALaEIKW/BzG1aQKKYxQf4SfOgBfXCzmGwO25jBDgwnTViB2zW/At+Jn3QM7g2/txM9YwotMEyesHOKVeR2YDv9HhijWEY3BtCLXGzSSXpyBkvObSSvhYkNBqVcgmWu4BZRwiUEW9n1paM8mR37lcAKav+RwBcfVOAyfA81qEPYcOKYf5MPH2IPazDO4UutQgWcxLHB38cxg3mMeOEIZjuA63Kw5T3BP/Ued9lzt72s1HFDH5a0UcFq1eaaAAAAAElFTkSuQmCC';


/***/ }),
/* 21 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
var data_1 = __webpack_require__(2);
;
;
var Gray = /** @class */ (function (_super) {
    __extends(Gray, _super);
    function Gray() {
        var _this = _super.call(this) || this;
        _this.state = {
            height: 'auto',
            showGray: false
        };
        return _this;
    }
    Gray.prototype.setDimension = function () {
        this.setState({
            height: document.documentElement.clientHeight
        });
    };
    Gray.prototype.componentWillMount = function () {
        var _this = this;
        window.addEventListener('resize', function () {
            _this.setDimension();
        });
        this._modal = data_1.modal.subscribe('gray', this.toggle.bind(this));
        this.setDimension();
    };
    Gray.prototype.componentWillUnmount = function () {
        window.removeEventListener('resize', function () { });
        this._modal.remove();
    };
    Gray.prototype.hide = function () {
        this.setState({ showGray: false });
        document.body.style.overflowY = '';
    };
    Gray.prototype.show = function () {
        this.setDimension();
        document.body.style.overflowY = 'hidden';
        this.setState({ showGray: true });
    };
    Gray.prototype.toggle = function (action) {
        if (action == 'open') {
            this.show();
        }
        if (action == 'close') {
            this.hide();
        }
    };
    Gray.prototype.render = function () {
        return (React.createElement("div", { className: (!this.state.showGray) ? "vtk-yp-gray __hidden" : "vtk-yp-gray", style: {
                height: this.state.height
            } }));
    };
    return Gray;
}(React.Component));
exports.default = Gray;


/***/ }),
/* 22 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
__webpack_require__(50);
var React = __webpack_require__(1);
var data_1 = __webpack_require__(2);
;
;
var VtkPopUp = /** @class */ (function (_super) {
    __extends(VtkPopUp, _super);
    function VtkPopUp() {
        var _this = _super.call(this) || this;
        _this.state = {
            top: 0,
            left: 0,
            maxHeight: 500,
            width: 600,
            visible: false
        };
        return _this;
    }
    VtkPopUp.prototype.setDimentions = function () {
        var element = document.getElementById(this.props.name);
        if (element) {
            this.setState({
                screen: {
                    height: document.documentElement.clientHeight,
                    width: document.documentElement.clientWidth
                },
                modal: {
                    height: (element) ? element.clientHeight : 0,
                    width: (element) ? element.clientWidth : 0
                },
                top: (document.documentElement.clientHeight < 200)
                    ? 0
                    : ((window.innerHeight / 2) - (element.offsetHeight / 2)),
                left: (document.documentElement.clientWidth < 600)
                    ? 0
                    : ((window.innerWidth / 2) - (element.offsetWidth / 2)),
                width: (document.documentElement.clientWidth < 600)
                    ? window.innerWidth
                    : 600,
            });
        }
    };
    VtkPopUp.prototype.componentWillMount = function () {
        this.modal = data_1.modal.subscribe(this.props.name, this.openclose.bind(this));
        this.setDimentions();
    };
    VtkPopUp.prototype.componentDidMount = function () {
        var _this = this;
        window.addEventListener('resize', function (e) {
            _this.setDimentions();
        });
        window.addEventListener('load', function (e) {
            _this.setDimentions();
            var element = document.getElementById(_this.props.name);
            if (element) {
                _this.originalHeight = element.offsetHeight;
            }
        });
    };
    VtkPopUp.prototype.componentWillUnmount = function () {
        this.modal.remove();
        window.removeEventListener('resize', function () { });
    };
    VtkPopUp.prototype.hide = function () {
        data_1.modal.publish('gray', 'close');
        this.setState({ 'visible': false });
    };
    VtkPopUp.prototype.open = function () {
        data_1.modal.publish('gray', 'open');
        this.setState({ 'visible': true });
    };
    VtkPopUp.prototype.openclose = function (s) {
        if (s == "close") {
            this.hide();
        }
        if (s == "open") {
            this.setDimentions();
            this.open();
        }
    };
    VtkPopUp.prototype.render = function () {
        var _this = this;
        var _a = this.props, title = _a.title, children = _a.children, name = _a.name;
        var events = {};
        // let visible: string = 'vtk-popup';
        events = {
            onClick: function (e) {
                _this.hide();
            },
            style: {
                "color": "white",
                "position": "absolute",
                "top": "5px",
                "right": '5px'
            }
        };
        var childrenWithProps = React.Children.map(this.props.children, function (child) { return React.cloneElement(child); });
        return (React.createElement("div", { id: name, className: this.state.visible ? 'vtk-popup' : 'vtk-popup ___hide', style: {
                left: this.state.left,
                top: this.state.top,
                width: this.state.width
            } },
            React.createElement("div", { className: "___header" },
                React.createElement("div", null, title),
                " ",
                React.createElement("div", { onClick: function () { return data_1.modal.publish(_this.props.name, 'close'); }, style: { position: 'absolute', top: '5px', right: '10px' } },
                    React.createElement("i", { className: "icon-button-circle-cross" }))),
            React.createElement("div", { className: "___content" },
                React.createElement("div", { className: "row" }, childrenWithProps))));
    };
    return VtkPopUp;
}(React.Component));
exports.default = VtkPopUp;


/***/ }),
/* 23 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var React = __webpack_require__(1);
var ReactDOM = __webpack_require__(14);
var data = __webpack_require__(2);
var vtk_yp_main_1 = __webpack_require__(13);
window['startYPApp'] = function () {
    data.getYearPlan().then(function (response) {
        ReactDOM.render(React.createElement(vtk_yp_main_1.default, { data: response }), document.getElementById("vtk-yp-main"));
    });
};


/***/ }),
/* 24 */
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__(3)(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/***/ }),
/* 25 */
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__(3)(false);
// imports


// module
exports.push([module.i, ".meeting .body ul ._text {\n  width: 100%; }\n\n.meeting .body ul li {\n  width: 100%; }\n\n.truncate {\n  white-space: nowrap;\n  overflow: hidden;\n  line-height: 20px;\n  text-overflow: ellipsis; }\n", ""]);

// exports


/***/ }),
/* 26 */
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__(3)(false);
// imports


// module
exports.push([module.i, ".vtk-popup {\n  position: fixed;\n  max-width: 600px;\n  z-index: 4000;\n  overflow: hidden;\n  -webkit-box-shadow: 0px 0px 20px 0px rgba(0, 0, 0, 0.75);\n  -moz-box-shadow: 0px 0px 20px 0px rgba(0, 0, 0, 0.75);\n  box-shadow: 0px 0px 20px 0px rgba(0, 0, 0, 0.75); }\n  .vtk-popup .___header {\n    padding: 10px 10px;\n    background-color: #18aa51;\n    width: 100%;\n    color: white;\n    text-transform: uppercase; }\n  .vtk-popup .___content {\n    padding: 40px;\n    width: 100%;\n    background-color: white;\n    overflow-x: hidden;\n    overflow-y: auto;\n    font-size: 14px; }\n    .vtk-popup .___content table {\n      font-size: 14px;\n      vertical-align: top; }\n      .vtk-popup .___content table tr {\n        background-color: white !important;\n        font-size: 14px; }\n        .vtk-popup .___content table tr td {\n          font-size: 14px;\n          vertical-align: top; }\n  .vtk-popup.___hide {\n    visibility: hidden; }\n\n.__preview .__header > .vtk-icon {\n  pointer-events: auto !important; }\n", ""]);

// exports


/***/ }),
/* 27 */
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__(3)(false);
// imports


// module
exports.push([module.i, "#vtk-yp-main h2 {\n  margin-bottom: 10px;\n  font-size: 30px; }\n\n#vtk-yp-main ._main_boxes {\n  padding: 0px; }\n  #vtk-yp-main ._main_boxes ._box_wrap {\n    padding-bottom: 20px;\n    text-align: center;\n    cursor: pointer; }\n    #vtk-yp-main ._main_boxes ._box_wrap ._box {\n      background: #FFFFFF;\n      box-shadow: 0 0 3px 2px rgba(0, 0, 0, 0.08);\n      padding: 15px; }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box * {\n        text-align: center; }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box .__img {\n        width: 100%;\n        height: 100px;\n        background-position: center center;\n        background-repeat: no-repeat; }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box.__tracks .__img {\n        background-image: url('data:image/svg+xml;utf8,<svg width=\"57px\" height=\"50px\" viewBox=\"0 0 57 50\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><g id=\"EXPLORE-TAB\" stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\"><g id=\"1-EXPLORE-MAIN\" transform=\"translate(-676.000000, -428.000000)\" fill=\"%23000000\"><path d=\"M701.1202,455.519714 L700.77934,455.519714 C700.58383,455.719525 700.58611,456.11745 700.60834,456.531789 C701.13844,457.377449 702.11485,458.929524 703.32667,458.050468 C703.53928,457.052544 702.95617,456.84594 702.6478,456.364242 C702.06241,456.159336 702.08008,455.353299 701.1202,455.519714 M683.949517,457.207072 C684.288667,457.377449 684.219127,457.953676 684.799957,457.882355 C685.726777,457.567072 686.466067,457.063298 686.499697,455.857072 C685.481677,455.000657 683.942107,455.978204 683.949517,457.207072 M709.277471,455.01311 C708.958841,454.879525 708.657311,454.731223 708.257171,454.676884 C708.230381,454.872167 707.989841,454.859148 707.748161,454.844997 C707.139401,455.40594 706.302641,455.961223 705.708131,456.364242 C705.649991,456.404431 705.585581,456.642732 705.538841,456.702166 C705.493241,456.756506 705.244721,456.645563 705.195701,456.702166 C705.132431,456.782544 705.055481,457.141978 705.028121,457.207072 C704.942051,457.42443 704.810951,457.540468 704.859971,457.882355 C705.402041,459.065373 706.976951,458.345374 707.578871,457.714808 C708.189911,456.916129 709.506611,456.815374 709.445621,455.351601 C709.315661,455.31311 709.251251,455.210091 709.277471,455.01311 M689.560598,451.637262 C688.641757,452.414998 687.244687,452.714431 687.179137,454.338959 C687.840907,455.115563 688.828717,454.330469 689.390738,454.000469 C689.808548,453.34896 690.561518,453.026318 690.580328,451.974054 C690.277658,451.825752 690.147698,451.503111 689.560598,451.637262 M711.149922,452.312545 C711.042762,452.94028 710.452242,453.085186 710.638062,454.000469 C710.828442,454.035563 710.902542,454.187261 710.977782,454.338959 C711.792312,454.68594 712.460922,453.913299 713.017812,453.494997 C713.628282,452.974809 714.315132,452.533865 714.376122,451.469715 C713.360952,450.207451 711.919992,451.771979 711.149922,452.312545 M699.419319,451.299337 C699.382269,450.888394 698.954199,450.538017 698.738169,450.287262 C698.469699,449.975376 698.243409,449.463112 697.718439,449.444432 C697.137039,449.370281 697.207149,449.948772 696.867999,450.118583 C696.970599,451.029904 697.597599,451.421036 697.890009,452.144432 C698.438919,452.668582 698.600229,453.574809 699.58804,453.66311 C699.62566,453.530658 699.73054,453.467261 699.92947,453.494997 C700.53025,452.850847 699.98134,451.748771 699.419319,451.299337 M694.830249,447.248772 C693.770618,447.917263 691.328168,448.715376 692.448218,450.457074 C693.536918,450.523866 694.023128,449.992357 694.490529,449.444432 C695.290809,449.226508 695.623119,448.543866 695.849409,447.755376 C695.574669,447.521036 695.432169,447.157074 694.830249,447.248772 M716.927443,447.080659 C716.234893,447.878206 714.373842,448.339527 715.056133,449.948772 C715.868383,450.369338 716.311843,449.546885 716.927443,449.273489 C717.417643,448.803678 718.164343,448.589715 717.947173,447.417452 C717.590923,447.320093 717.462103,446.999716 716.927443,447.080659 M719.986633,443.197641 C719.037583,442.227453 718.809013,440.542925 717.608023,439.821227 C716.579743,439.813302 716.433253,440.681604 716.246863,441.509151 C716.353453,442.64066 716.972473,443.263868 717.266023,444.211415 C716.317543,444.61783 715.239673,444.898018 713.866542,444.886131 C713.428782,445.464622 712.451802,445.505943 712.507092,446.572924 C712.764162,446.885376 712.882722,447.117452 713.359242,447.248772 C713.809542,447.373301 714.528312,447.15481 715.056133,447.248772 C716.116333,446.950471 717.122953,446.599527 718.288033,446.405376 C719.095153,447.06481 719.434873,448.189527 719.986633,449.105376 C720.619334,449.940847 720.757844,451.265941 722.535674,450.961979 C722.736314,450.652357 722.981414,450.390847 723.216254,450.118583 C722.929544,448.265942 721.777574,447.269716 721.176794,445.729527 C722.111594,445.644622 722.692994,445.208207 723.557684,445.054245 C724.353974,444.835188 725.085284,444.546509 725.086994,443.536132 C724.519274,441.218774 721.614554,443.088962 719.986633,443.197641 M729.336345,447.417452 C728.913975,454.97745 729.391065,463.591976 728.994345,471.387446 C726.729735,470.430842 725.086994,468.856126 723.045824,467.673673 C720.829094,466.723296 719.515243,464.87858 717.095593,464.129146 C715.662613,464.391787 714.978612,465.40273 713.866542,465.984617 C713.034912,466.791221 711.898902,467.294994 711.149922,468.180277 C709.088801,469.45556 707.381651,471.078955 705.538841,472.568766 C704.883341,472.434049 704.783021,471.743484 704.34925,471.387446 C704.03974,470.906314 703.38082,470.771031 703.15852,470.206691 C701.80648,469.633295 700.91728,468.605371 699.419319,468.180277 C698.128269,467.548579 696.904479,466.851221 695.507409,466.323107 C695.116959,465.813674 694.443789,465.579334 694.149668,464.971976 C693.618428,464.656127 693.212588,464.214617 692.448218,464.129146 C691.328168,464.026693 690.646448,464.364051 690.069608,464.803863 C689.777198,465.468957 688.833847,465.4899 688.540867,466.154994 C687.929827,466.501409 687.623167,467.156315 687.009277,467.504994 C686.548147,468.003673 686.223247,468.637069 685.481107,468.856126 C684.636367,469.932163 683.492376,470.707635 682.759926,471.892917 C682.208166,472.302163 681.869586,472.923106 681.398196,473.411596 C681.284766,473.804992 680.836746,473.865558 680.722746,474.256124 C680.523246,474.30537 680.230266,475.213294 680.209746,474.427634 C680.701656,473.763106 680.004546,472.83537 680.041026,472.062729 C679.860906,461.665184 679.546836,451.396696 679.021866,441.341038 C678.984816,440.797642 679.627206,440.929529 679.871736,440.665755 C680.125956,440.468774 680.565996,440.455755 680.722746,440.158585 C681.450636,439.925944 681.840516,439.35651 682.589496,439.145944 C683.815566,438.446888 684.870637,437.584246 686.159407,436.951982 C687.272617,436.143681 688.787677,435.735002 689.899178,434.925568 C690.757028,434.822549 690.955388,434.06236 691.940348,434.082172 C692.496098,433.734625 693.007958,433.340663 693.640088,433.070097 C695.362059,434.340285 697.321149,435.377266 699.079029,436.613492 C699.94543,437.271794 700.99936,437.746133 701.79907,438.470095 C702.80056,438.937642 703.37569,439.829718 704.51797,440.158585 C706.138481,439.965567 707.228891,439.248963 708.257171,438.470095 C709.325921,437.730284 710.478462,437.075379 711.487362,436.277266 C713.748552,434.978775 715.566283,433.235946 717.776743,431.888776 C717.901573,431.730852 718.084543,431.631229 718.288033,431.549154 C719.309473,432.39255 720.187843,433.376323 721.515374,433.913493 C722.656514,434.638587 723.778844,435.380096 725.086994,435.938775 C725.700885,436.790662 726.610605,437.350473 727.466745,437.964624 C728.186655,438.655756 728.935635,439.316888 729.846495,439.821227 C729.685755,442.273868 729.480555,444.814811 729.336345,447.417452 M679.700166,475.269898 C679.612956,475.181596 679.824996,475.053671 679.871736,475.102351 C679.957806,475.187822 679.748616,475.316313 679.700166,475.269898 M732.907396,438.807454 C732.688516,438.008209 732.131626,437.55085 731.547376,437.120096 C730.869646,436.722171 730.418775,436.101794 729.675495,435.768398 C729.322095,435.305379 728.434035,435.033115 728.145615,434.757455 C728.002545,434.619908 728.002545,434.707077 727.805325,434.588209 C727.133295,434.182926 726.534795,433.656512 725.765865,433.406889 C724.697114,432.385757 723.372434,431.621606 722.197664,430.706324 C721.502834,430.383682 720.868424,429.999909 720.328064,429.526135 C719.616133,429.160475 719.418913,428.289343 718.457323,428.173871 C718.445923,428.071419 718.431103,427.975192 718.288033,428.005758 C718.057753,428.057834 717.961423,428.245192 717.608023,428.173871 C716.329513,428.931796 715.425493,430.058777 714.207972,430.874437 C712.721412,431.819153 711.237132,432.76387 709.789901,433.744813 C709.022681,434.167643 708.457811,434.786323 707.578871,435.095379 C707.375381,435.511983 706.994051,435.755379 706.558001,435.938775 C706.194911,436.109152 705.925871,436.810473 705.538841,436.444247 C703.40305,435.527832 701.70046,434.177832 699.75961,433.070097 C698.999229,432.303682 697.711029,432.066512 696.867999,431.38104 C695.648199,431.017078 694.829109,430.256324 693.299228,430.198588 C692.213948,430.132361 691.741988,430.680852 690.749048,430.706324 C686.462647,433.53821 681.253416,435.456511 676.813115,438.135001 C676.582265,438.749152 676.593095,439.603303 676.131395,439.990472 C675.903965,442.638962 676.017395,445.402358 676.131395,448.093301 C676.453445,455.685563 675.562535,464.377636 676.641545,472.230276 C676.739015,473.540653 676.436345,475.248388 676.813115,476.282539 C676.857575,476.630086 677.265695,476.620463 677.491985,476.788577 C677.912645,476.568954 678.315635,477.166124 678.340145,476.788577 C678.728316,477.247067 678.646235,478.173671 679.700166,477.971595 C680.780316,478.141973 681.002046,477.461595 681.571476,477.125369 C682.097016,476.74669 682.549026,476.298388 682.930926,475.777634 C684.014497,475.052539 684.822187,474.054615 685.819117,473.244049 C686.776147,472.393861 687.710377,471.519333 688.711867,470.712163 C689.223158,470.321031 689.766938,469.961031 690.240038,469.531409 C690.930308,469.31688 691.226138,468.707258 691.940348,468.517069 C694.291029,469.67122 696.262089,471.20122 698.569449,472.400653 C699.66271,473.057823 700.76623,473.707634 701.62807,474.595747 C702.29098,474.781973 702.8587,475.061596 703.32667,475.437445 C703.82713,475.394992 703.85107,475.820652 704.34925,475.777634 C704.27059,476.304615 704.711771,476.315369 705.195701,476.282539 C706.500431,475.774237 707.483111,474.950653 708.428741,474.088011 C709.503191,473.355559 710.584482,472.630464 711.487362,471.724238 C712.815462,471.240842 713.422512,470.044805 714.717552,469.531409 C715.098313,469.006126 715.860403,468.865748 716.246863,468.34839 C716.844793,468.042164 717.232963,467.528768 717.947173,467.335749 C720.244274,468.824428 721.839704,471.009899 723.897974,472.737446 C724.486784,473.052162 724.681724,473.755181 725.426145,473.919332 C725.886705,474.418011 726.212175,475.052539 726.956025,475.269898 C727.512345,475.672917 727.527165,476.614803 728.485905,476.620463 C729.126015,476.940841 729.264525,477.761029 730.355505,477.633105 C731.016136,477.315558 731.218486,476.774992 731.375806,475.944615 C731.539966,475.075747 731.250976,474.120841 731.375806,473.244049 C731.649976,472.616314 731.928136,471.991408 732.055816,471.218201 C732.961546,463.732919 732.345376,454.740846 732.735826,446.741603 C733.042486,445.606131 732.897136,444.073868 732.907396,442.690472 C732.912526,441.268585 733.113166,439.857453 732.907396,438.807454\" id=\"Fill-1097\"></path></g></g></svg>'); }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box.__library .__img {\n        background-image: url('data:image/svg+xml;utf8,<svg width=\"50px\" height=\"50px\" viewBox=\"0 0 50 50\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><g id=\"EXPLORE-TAB\" stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\"><g id=\"1-EXPLORE-MAIN\" transform=\"translate(-237.000000, -426.000000)\" fill=\"%23000000\"><path d=\"M261.518182,452.503218 C260.761364,452.650134 260.694545,451.575756 260.967273,451.137262 C261.577727,451.134558 261.746818,452.106636 261.518182,452.503218 Z M262.894545,450.589258 C262.742273,450.281005 262.622273,449.899294 262.483182,449.771306 C262.359091,449.659091 262.042727,449.757336 262.069091,449.498656 C260.865909,449.151646 259.442273,449.449984 258.900455,450.180057 C258.775455,450.421161 258.853182,450.861908 258.625909,451.000261 C258.504545,452.894844 259.28,453.902073 260.415909,454.550575 C261.702273,454.688027 262.48,454.321188 263.034091,453.732624 C263.445909,453.034999 263.284545,451.725376 263.170909,451.273813 C263.058182,450.824503 263.015909,450.83577 262.894545,450.589258 Z M280.663636,450.317058 C279.497727,450.526616 278.605455,451.008373 277.496364,451.273813 C275.936818,451.594233 274.559545,452.09582 273.089091,452.503218 C272.547273,452.921883 271.687273,453.025085 271.022273,453.322071 C270.257273,453.473493 269.687727,453.819151 268.818182,453.867372 C267.588182,454.514973 265.800455,454.608711 264.686818,455.37123 C264.348636,455.855692 264.318636,456.644801 264.135455,457.282487 C264.040455,457.962085 263.655909,458.353711 263.584545,459.057194 C263.239545,459.535346 263.181818,460.297866 262.894545,460.832801 C262.925455,461.273098 262.652273,461.409649 262.757727,461.924755 C262.368636,461.902222 262.872273,462.767042 262.483182,462.74496 C262.519091,465.922126 261.786364,468.338124 261.242727,470.938894 C260.700455,469.974928 260.523182,468.647729 260.14,467.524679 C260.031364,466.1772 259.713636,465.034322 259.451364,463.837364 C258.914091,461.456968 258.062273,459.38798 257.386364,457.145937 C256.994545,456.588919 257.933182,456.37846 257.660909,455.506429 C257.425909,455.238285 257.237727,454.924625 256.833636,454.824578 C256.303182,454.850716 255.819091,454.829535 255.457273,454.687576 C253.323182,453.79797 250.471364,453.622662 248.295,452.775418 C245.748636,452.296815 243.042273,451.975493 240.856818,451.137262 C241.111364,450.934465 241.511364,450.874526 241.959091,450.864161 C242.217273,450.664067 242.871364,450.857852 243.060909,450.589258 C243.614091,450.683897 243.847727,450.459918 244.3,450.45451 C244.546818,450.288215 245.101364,450.428371 245.264091,450.180057 C246.772273,449.900196 248.342273,449.681625 249.81,449.361655 C251.475909,449.238173 252.679091,448.655918 254.354545,448.542351 C255.900455,448.342708 257.515455,448.21472 258.488182,447.449947 C259.211364,445.344906 259.45,442.758557 259.865,440.348418 C260.175,440.291184 259.869545,439.623753 260.14,439.529114 C260.072273,439.09738 260.389091,439.046906 260.277727,438.571909 C260.509091,438.119444 260.528636,437.453816 260.691818,436.933302 C260.926364,435.755723 261.205909,434.620956 261.380909,433.382988 L261.380909,432.972886 C261.541364,432.951254 261.624091,433.003531 261.656364,433.109887 C261.837273,435.479467 262.368636,437.503389 262.483182,439.938315 C262.949091,442.026232 262.909545,444.61258 263.722273,446.357092 C263.763636,446.725283 264.301364,446.601801 264.410455,446.902844 C264.455,447.223264 264.836364,447.208843 264.961364,447.449947 C266.602273,447.417049 267.573182,448.046173 269.093636,448.13315 C270.476818,448.308909 271.806364,448.538295 273.226364,448.678902 C275.665909,449.401313 278.770455,449.463955 281.628636,449.771306 C281.558636,450.20259 280.841364,449.993032 280.663636,450.317058 Z M270.608636,459.330745 C270.447273,459.944547 269.905455,460.181144 270.057727,461.106353 C269.351818,461.04281 269.478182,460.741768 269.368636,460.150049 C269.277727,459.649814 269.440455,458.948584 269.368636,458.511893 C269.414091,458.191472 269.586818,457.99904 269.506818,457.555588 C269.921364,458.100439 270.168182,458.811132 270.608636,459.330745 Z M271.296818,460.55925 C271.713636,460.967099 272.076364,461.426774 272.260909,462.062207 C271.838636,462.026154 271.641818,461.766122 271.296818,461.652105 L271.296818,460.55925 Z M267.991364,460.013498 C267.080455,459.915705 266.685909,459.30641 265.788636,459.194646 C265.895455,458.844932 265.925909,458.420409 266.200455,458.23744 C266.425909,457.54973 266.124091,456.340604 267.028182,456.326183 C266.928182,456.636689 266.905455,457.024258 266.751364,457.282487 C266.917273,458.140548 266.202273,459.164001 267.028182,459.466395 C267.579091,459.375361 267.427727,458.589407 267.441818,457.96524 C267.576364,457.417235 267.609545,456.767381 267.579091,456.053532 C267.938182,455.999904 268.335455,455.984131 268.543636,455.779981 C268.980455,455.939064 268.832273,456.676798 269.232273,456.871934 C268.743182,457.845364 268.27,458.832313 267.991364,460.013498 Z M255.870455,457.827338 C255.640909,457.554236 255.370455,457.323498 255.32,456.871934 L256.008636,456.871934 C255.818182,457.049495 255.871818,457.465907 255.870455,457.827338 Z M254.769091,459.466395 C254.379545,459.488026 254.423636,459.081529 254.217727,458.921094 C254.158636,458.661062 253.921818,458.57814 253.943636,458.23744 C253.686818,457.853927 253.362273,457.539365 253.253182,457.008485 C253.010909,456.885454 252.828182,456.703838 252.701818,456.463635 L252.151818,456.463635 C252.163636,456.26985 252.174545,456.076516 252.013636,456.053532 C252.213182,455.734013 252.833182,456.14006 253.116364,456.189632 C253.554091,456.210363 253.721818,456.499688 254.217727,456.463635 C254.145,457.172526 254.4,457.557391 254.492727,458.102241 C254.637727,458.601575 255.293182,459.081529 254.769091,459.466395 Z M252.427273,460.6958 C252.090909,460.074788 252.05,459.158593 251.600455,458.648443 C251.635455,458.182008 251.949091,457.99273 251.876364,457.419038 C252.290455,457.734952 252.378636,458.377145 252.564545,458.921094 C252.873182,459.298298 253.100455,459.75617 253.253182,460.286599 C252.962273,460.408729 252.845909,460.70256 252.427273,460.6958 Z M250.637727,461.789557 C250.671818,461.369089 250.936364,461.175755 251.05,460.832801 C251.254545,460.948171 251.248182,461.273098 251.325,461.516005 C251.062727,461.575042 250.978636,461.809386 250.637727,461.789557 Z M252.564545,443.215528 C252.185,443.183982 252.250909,442.708534 252.013636,442.533677 C251.925909,442.029386 252.191364,441.172678 251.876364,440.89507 C252.139545,440.468744 252.474545,441.107332 252.840909,441.03072 C252.683636,441.695446 252.495909,442.329527 252.564545,443.215528 Z M254.354545,444.172283 L254.354545,441.850023 C254.477727,441.955928 254.667273,441.997389 254.906364,441.987475 C254.815909,443.262397 254.813182,444.6261 254.630455,445.81089 C254.115455,445.684705 254.451818,444.713528 254.354545,444.172283 Z M256.559091,442.669777 L257.248182,442.669777 C257.249545,443.354332 257.141364,443.929827 256.972273,444.446286 C256.975455,445.084874 257.044091,445.791511 256.421818,445.81089 C256.61,444.90506 256.557727,443.760829 256.559091,442.669777 Z M266.200455,442.806327 C266.335909,442.810834 266.308636,442.973974 266.34,443.079428 C266.588636,444.151553 267.107727,444.957336 267.303182,446.083991 C266.826818,445.91995 266.269545,445.833874 265.65,445.81089 C265.492273,444.829348 265.164091,444.017256 265.099091,442.942427 C265.37,443.195699 265.951364,442.984789 266.200455,442.806327 Z M268.267273,442.123575 C268.426818,443.46835 269.08,444.323706 269.506818,445.400788 C269.197273,445.5112 269.114091,446.548623 268.818182,446.220542 C268.317273,446.261552 268.635,445.491371 268.404091,445.264237 C267.934545,444.502168 267.811364,443.395342 267.303182,442.669777 C267.475909,442.340794 267.867727,442.227678 268.267273,442.123575 Z M270.195909,441.03072 C270.635455,441.550783 270.607727,442.534578 271.022273,443.079428 C271.125,443.500797 270.643636,443.341714 270.746364,443.762632 C270.384091,443.721622 270.637727,444.291709 270.195909,444.172283 C269.820909,443.313773 269.420909,442.481851 269.093636,441.576471 C269.538636,441.471918 269.908182,441.290752 270.195909,441.03072 Z M271.848182,440.348418 C271.722273,440.998272 272.372273,441.368716 271.986818,441.850023 C271.667727,441.484085 271.444545,441.022608 271.436364,440.348418 L271.848182,440.348418 Z M250.498182,440.89507 C250.216818,440.627377 250.088182,440.209163 250.085,439.665665 C250.359091,439.667468 250.26,440.038362 250.637727,439.938315 C250.592273,440.259637 250.418636,440.451168 250.498182,440.89507 Z M248.436034,438.862253 C248.355345,438.781753 248.545974,438.668253 248.587831,438.710253 C248.667008,438.789253 248.476379,438.903753 248.436034,438.862253 Z M273.101559,439.665665 C273.373382,439.775665 273.122236,439.940665 273.101559,440.120165 C272.829233,440.008665 273.078361,439.844665 273.101559,439.665665 Z M284.520455,448.2688 C283.544091,447.96235 282.414545,447.808674 281.352273,447.586498 C280.557727,447.647337 279.942273,447.529264 279.287273,447.449947 L277.357727,447.449947 C276.295909,447.27509 275.154091,447.177747 274.051818,447.040295 C273.576364,446.874903 273.018182,446.790629 272.399545,446.766744 C272.074545,446.452182 271.038636,446.841103 270.885,446.357092 C271.592273,445.373748 272.343636,444.43547 272.95,443.352079 C273.383636,443.564341 273.72,443.439507 274.051818,443.215528 C274.095,442.7198 274.030909,442.327724 273.914545,441.987475 C274.457273,441.342578 274.829545,440.526429 275.154091,439.665665 C275.578636,439.02933 276.291364,438.047338 275.704545,437.207755 C275.339545,436.944569 274.425455,437.049122 274.191364,437.343404 C273.786818,437.626871 273.122727,437.650756 272.812727,438.025706 C271.657273,438.246531 270.877273,438.839151 269.920455,439.255563 C269.416364,439.439433 269.068636,439.776978 268.543636,439.938315 C268.251364,440.331743 267.522727,440.292536 267.303182,440.75852 C266.795,440.936982 266.384545,441.210984 266.063636,441.576471 C265.783182,441.617481 265.793636,441.946915 265.512273,441.987475 C265.469545,442.145206 265.086818,442.630119 264.961364,442.259224 C264.430909,436.730955 263.29,431.807925 262.62,426.41891 C262.347273,426.068746 261.310909,425.752381 260.967273,426.281458 C260.955,426.291373 260.834091,426.269741 260.829545,426.281458 C260.491364,427.3653 260.303182,428.622196 260.002273,429.833125 C259.820455,430.380679 259.812727,431.100386 259.590455,431.607831 C259.485455,432.277514 259.477273,433.04364 259.177273,433.52044 C259.043182,434.846287 258.649545,435.911201 258.488182,437.207755 C258.27,437.718806 258.183636,438.363703 258.074091,438.982462 C257.777273,439.461966 257.968182,440.42503 257.523636,440.75852 C255.662273,440.464238 254.220909,439.753093 252.701818,439.119012 C252.029545,438.693587 251.191818,438.431753 250.498182,438.025706 C249.597273,437.828767 249.209091,437.119425 248.02,437.207755 C247.805,437.550258 247.46,437.664726 247.743182,438.162708 C247.988636,438.229856 247.684091,438.309173 247.606364,438.437161 C247.362727,438.470059 247.292727,438.671054 247.330909,438.982462 C247.655,439.708928 248.266364,440.149676 248.570909,440.89507 C248.989545,441.571063 249.529545,442.129433 249.947273,442.806327 C250.528182,443.279072 250.793182,444.061871 251.325,444.581484 C251.760455,444.696403 251.717273,445.285418 252.151818,445.400788 C252.124091,445.973579 252.828182,445.823508 252.701818,446.494544 C250.335909,446.969091 247.823636,447.302581 245.677727,447.995699 C243.381818,448.540999 240.994091,448.996168 238.927727,449.771306 C238.22,449.979963 237.060455,449.73976 237,450.589258 C237.140909,451.042173 237.380909,451.395942 237.964091,451.410363 C238.170455,451.798383 238.534545,452.02777 238.789545,452.366217 C240.546818,453.21932 242.597273,453.782197 244.850455,454.141825 C247.085909,454.564996 249.025,455.284703 251.186364,455.779981 C250.021364,457.355495 249.627727,459.696232 248.708182,461.516005 C248.364545,462.540359 247.796364,463.342537 247.055455,463.973915 C246.921364,464.524173 246.452273,464.742744 246.091818,465.065868 C245.857273,465.559343 245.660455,466.268234 246.091818,466.704475 C247.141818,467.118633 247.783182,466.167286 248.434091,465.749522 C249.08,465.207827 249.687273,464.625572 250.360455,464.110916 C252.436818,462.845908 254.450909,461.518709 256.697273,460.424051 C257.469545,463.026173 258.161818,465.707611 258.625909,468.618436 C258.884545,470.04433 259.486364,471.132227 259.728182,472.578401 C259.902273,473.223749 259.802727,474.143099 260.14,474.626209 C260.113636,475.518069 260.425909,476.073284 261.380909,475.992165 C262.035455,475.867782 262.220455,475.276063 262.207273,474.488757 C262.539545,473.999789 262.502273,473.144883 262.757727,472.578401 C263.012273,471.189912 263.309091,469.847841 263.584545,468.480984 C263.750455,467.873041 263.831818,467.17767 263.996818,466.568826 C263.966818,466.12943 264.240909,465.991528 264.135455,465.475069 C264.432273,465.362404 264.083182,464.605292 264.410455,464.519666 C264.349545,464.051428 264.463182,463.753992 264.548182,463.428163 C264.601364,463.07169 264.619091,462.679163 264.824545,462.471408 C264.859091,461.688608 265.034545,461.041007 265.375455,460.55925 C266.582727,461.228933 268.015909,461.675088 268.956364,462.60886 C270.203182,463.283501 271.458636,463.95048 272.812727,464.519666 C273.576364,465.174027 273.851818,466.313751 275.43,466.158723 C275.616364,465.844161 275.983636,465.706709 275.842727,465.065868 C275.968636,464.396185 275.266364,464.546255 275.292727,463.973915 C275.372727,463.531365 275.198636,463.338481 275.154091,463.018511 C274.260909,461.855353 273.594091,460.468667 272.675455,459.330745 C271.917727,457.987773 270.949545,456.85526 270.334545,455.37123 C273.093636,454.420785 276.159091,453.771381 279.148636,453.04897 C279.733182,452.715931 280.488182,452.556397 281.215,452.366217 C282.471364,451.743853 284.092273,451.485173 285.348636,450.864161 C286.251818,450.803773 286.975909,450.567175 287,449.634756 C286.796818,448.561279 285.762273,448.314316 284.520455,448.2688 Z\" id=\"Fill-1093\"></path></g></g></svg>'); }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box h3 {\n        font-size: 18px; }\n      #vtk-yp-main ._main_boxes ._box_wrap ._box .btn {\n        padding: 12px 5px;\n        margin: 0 !important; }\n  #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__library {\n    padding: 10px;\n    width: 340px;\n    margin: 0 auto;\n    position: relative; }\n    #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__library .__img {\n      height: 68px; }\n  #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__tracks {\n    padding: 44px 10px 10px 10px;\n    box-shadow: 0 0 3px 2px white; }\n    #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__tracks .__img {\n      height: 68px;\n      background-image: url('data:image/svg+xml;utf8,<svg width=\"80px\" height=\"70px\" viewBox=\"0 0 80 70\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><g id=\"EXPLORE-TAB\" stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\"><g id=\"4.-PREVIEW-PRESELECTED\" transform=\"translate(-752.000000, -430.000000)\"><g id=\"Group-6\" transform=\"translate(752.000000, 430.000000)\"><polygon id=\"Path-2\" fill=\"%23D6E59A\" points=\"4 17.3591502 4 70 22.900501 53.6530859 39.4957704 63.7523195 57.8746715 53.6530859 77 63.7523195 77 17.3591502 57.8746715 4 40.5 13.4809735 22.900501 4\"></polygon><path d=\"M35.2564208,38.5275994 L34.7780207,38.5275994 C34.5036207,38.8073351 34.5068207,39.3644293 34.5380207,39.9445047 C35.2820208,41.1284289 36.6524211,43.3013341 38.3532214,42.0706551 C38.6516215,40.6735611 37.8332213,40.3843159 37.4004212,39.7099387 C36.5788211,39.4230708 36.6036211,38.2946183 35.2564208,38.5275994 M11.1572162,40.8899007 C11.6332162,41.1284289 11.5356162,41.9351457 12.3508164,41.8352967 C13.6516166,41.3939006 14.6892168,40.6886177 14.7364168,38.9999011 C13.3076166,37.8009203 11.1468162,39.169486 11.1572162,40.8899007 M46.705223,37.8183542 C46.2580229,37.6313354 45.8348228,37.4237128 45.2732227,37.3476374 C45.2356227,37.6210335 44.8980227,37.6028071 44.5588226,37.5829958 C43.7044224,38.3683164 42.5300222,39.1457124 41.695622,39.7099387 C41.614022,39.7662028 41.523622,40.0998254 41.458022,40.1830329 C41.394022,40.2591084 41.0452219,40.1037877 40.9764219,40.1830329 C40.8876219,40.2955612 40.7796219,40.7987686 40.7412219,40.8899007 C40.6204218,41.1942025 40.4364218,41.3566553 40.5052218,41.8352967 C41.266022,43.4915227 43.4764224,42.4835229 44.3212225,41.6007307 C45.1788227,40.48258 47.0268231,40.3415235 46.9412231,38.2922409 C46.758823,38.2383541 46.668423,38.0941278 46.705223,37.8183542 M19.0324177,33.0921667 C17.7428174,34.1809966 15.782017,34.6002041 15.690017,36.8745432 C16.6188172,37.9617882 18.0052175,36.8626564 18.7940176,36.4006565 C19.3804177,35.4885435 20.4372179,35.0368455 20.463618,33.563676 C20.0388179,33.3560534 19.8564178,32.9043554 19.0324177,33.0921667 M49.3332235,34.0375627 C49.1828235,34.9163927 48.3540233,35.1192606 48.6148234,36.4006565 C48.8820234,36.4497885 48.9860234,36.6621658 49.0916235,36.8745432 C50.2348237,37.3603166 51.1732239,36.2786188 51.954824,35.6929963 C52.8116242,34.9647323 53.7756244,34.3474117 53.8612244,32.8576007 C52.4364241,31.0904313 50.4140237,33.2807704 49.3332235,34.0375627 M32.8692203,32.6190725 C32.8172203,32.0437519 32.2164202,31.5532237 31.9132202,31.2021671 C31.5364201,30.7655257 31.21882,30.0483561 30.4820199,30.0222052 C29.6660197,29.9183939 29.7644197,30.7282805 29.2884197,30.9660163 C29.4324197,32.241865 30.3124198,32.7894498 30.7228199,33.8022043 C31.4932201,34.5360154 31.7196201,35.8047321 33.1060204,35.9283547 C33.1588204,35.7429208 33.3060204,35.6541661 33.5852205,35.6929963 C34.4284206,34.7911852 33.6580205,33.2482799 32.8692203,32.6190725 M26.4284191,26.9482814 C24.9412188,27.8841679 21.5132182,29.0015262 23.0852185,31.4399029 C24.6132188,31.5334124 25.2956189,30.7892993 25.951619,30.0222052 C27.0748192,29.7171109 27.5412193,28.761413 27.8588194,27.6575265 C27.4732193,27.3294511 27.2732193,26.819904 26.4284191,26.9482814 M57.4420251,26.7129229 C56.4700249,27.8294887 53.8580244,28.4753376 54.8156246,30.7282805 C55.9556248,31.3170728 56.5780249,30.1656391 57.4420251,29.7828845 C58.1300252,29.1251488 59.1780254,28.8256017 58.8732254,27.1844323 C58.3732253,27.0481304 58.1924252,26.5996022 57.4420251,26.7129229 M61.7356259,21.2766978 C60.4036256,19.918434 60.0828256,17.5600949 58.3972253,16.5497178 C56.954025,16.5386235 56.7484249,17.7542458 56.4868249,18.9128116 C56.6364249,20.4969244 57.5052251,21.3694148 57.9172252,22.6959805 C56.5860249,23.2649615 55.0732246,23.6572256 53.1460242,23.6405841 C52.5316241,24.4504707 51.1604239,24.5083197 51.2380239,26.0020929 C51.598824,26.4395268 51.765224,26.7644324 52.4340241,26.9482814 C53.0660242,27.122621 54.0748244,26.8167342 54.8156246,26.9482814 C56.3036249,26.5306588 57.7164251,26.0393382 59.3516254,25.7675269 C60.4844257,26.6907343 60.9612258,28.2653377 61.7356259,29.547526 C62.6236261,30.7171861 62.8180261,32.5723178 65.3132266,32.1467707 C65.5948266,31.7132991 65.9388267,31.347186 66.2684268,30.9660163 C65.8660267,28.3723188 64.2492264,26.9776021 63.4060262,24.8213385 C64.7180265,24.7024706 65.5340266,24.0914896 66.7476269,23.8759425 C67.8652271,23.5692633 68.8916273,23.1651125 68.8940273,21.7505845 C68.0972271,18.5062834 64.0204263,21.1245469 61.7356259,21.2766978 M74.8580284,27.1844323 C74.2652283,37.7684297 74.9348284,49.8287665 74.3780283,60.7424242 C71.1996277,59.4031793 68.8940273,57.198576 66.0292267,55.5431425 C62.9180261,54.2126145 61.0740258,51.6300113 57.6780251,50.580804 C55.6668247,50.948502 54.7068246,52.3638225 53.1460242,53.1784638 C51.978824,54.3077088 50.3844237,55.0129916 49.3332235,56.2523876 C46.440423,58.0377834 44.0444225,60.3105375 41.458022,62.3962729 C40.5380218,62.2076692 40.3972218,61.2408769 39.7884217,60.7424242 C39.3540216,60.0688395 38.4292214,59.8794433 38.1172214,59.089368 C36.219621,58.2866135 34.9716207,56.8475195 32.8692203,56.2523876 C31.05722,55.3680104 29.3396197,54.3917088 27.3788193,53.6523505 C26.8308192,52.9391431 25.886019,52.6110677 25.4732189,51.760766 C24.7276188,51.3185774 24.1580187,50.7004644 23.0852185,50.580804 C21.5132182,50.4373701 20.556418,50.9096719 19.7468178,51.5254076 C19.3364177,52.4565394 18.0124175,52.4858602 17.6012174,53.416992 C16.7436172,53.901973 16.3132172,54.8188407 15.451617,55.3069916 C14.8044169,56.0051423 14.3484168,56.8918969 13.3068166,57.198576 C12.1212163,58.7050285 10.515616,59.7906886 9.48761584,61.4500844 C8.71321569,62.0230277 8.2380156,62.8923482 7.57641547,63.5762349 C7.41721544,64.1269895 6.78841532,64.2117819 6.62841529,64.7585742 C6.34841523,64.8275176 5.93721515,66.0986116 5.90841515,64.9986874 C6.59881528,64.068348 5.62041509,62.7695181 5.6716151,61.6878202 C5.41881505,47.1312577 4.97801497,32.7553743 4.24121483,18.6774532 C4.18921482,17.9166986 5.09081499,18.1013401 5.43401506,17.7320572 C5.79081512,17.4562836 6.40841524,17.4380572 6.62841529,17.0220196 C7.65001548,16.6963216 8.19721559,15.8991142 9.24841579,15.6043218 C10.9692161,14.6256428 12.4500164,13.417945 14.2588168,12.5327754 C15.8212171,11.401153 17.9476175,10.8290022 19.5076178,9.69579494 C20.711618,9.55156855 20.9900181,8.48730466 22.3724183,8.5150405 C23.1524185,8.02847458 23.8708186,7.47692754 24.7580188,7.09813518 C27.1748192,8.87639891 29.9244198,10.3281721 32.3916203,12.0588887 C33.6076205,12.9805111 35.0868208,13.6445864 36.209221,14.6581334 C37.6148213,15.3126993 38.4220214,16.5616046 40.0252217,17.0220196 C42.2996222,16.7517932 43.8300225,15.7485482 45.2732227,14.6581334 C46.773223,13.6223978 48.3908233,12.7055301 49.8068236,11.5881718 C52.9804242,9.77028548 55.5316247,7.3303238 58.6340253,5.44428652 C58.8092253,5.22319223 59.0660254,5.08372057 59.3516254,4.96881493 C60.7852257,6.14956937 62.018026,7.52685206 63.8812263,8.27888961 C65.4828266,9.29402145 67.0580269,10.3321344 68.8940273,11.1142852 C69.7556275,12.3069264 71.0324277,13.090662 72.2340279,13.9504732 C73.2444281,14.9180578 74.2956283,15.8436425 75.5740286,16.5497178 C75.3484285,19.9834151 75.0604285,23.540735 74.8580284,27.1844323 M5.19321501,66.1778569 C5.07081499,66.0542343 5.36841504,65.87514 5.43401506,65.9432909 C5.55481508,66.0629513 5.26121502,66.242838 5.19321501,66.1778569 M79.8700294,15.1304351 C79.5628293,14.011492 78.7812292,13.3711903 77.961229,12.7681338 C77.0100288,12.2110396 76.3772287,11.3425115 75.3340285,10.8757569 C74.8380284,10.2275307 73.5916282,9.84636094 73.1868281,9.4604365 C72.9860281,9.26787051 72.9860281,9.38990822 72.709228,9.22349316 C71.7660278,8.65609707 70.9260277,7.91911612 69.8468275,7.5696445 C68.3468272,6.14005994 66.4876268,5.07024887 64.8388265,3.78885295 C63.8636263,3.33715495 62.9732261,2.79987206 62.214826,2.1365892 C61.2156258,1.62466479 60.9388258,0.405080177 59.5892255,0.243419838 C59.5732255,0.0999859102 59.5524255,-0.0347310387 59.3516254,0.00806140391 C59.0284254,0.0809670468 58.8932254,0.343268871 58.3972253,0.243419838 C56.6028249,1.30451392 55.3340247,2.88228713 53.6252243,4.02421139 C51.5388239,5.34681484 49.4556235,6.6694183 47.4244231,8.04273873 C46.3476229,8.63470085 45.5548228,9.50085159 44.3212225,9.93353073 C44.0356225,10.5167759 43.5004224,10.8575305 42.8884223,11.1142852 C42.3788222,11.3528134 42.0012221,12.3346622 41.458022,11.8219454 C38.4604214,10.5389645 36.070821,8.648965 33.3468204,7.09813518 C32.2796202,6.0251543 30.4716199,5.69311665 29.2884197,4.7334565 C27.5764193,4.22390945 26.4268191,3.1588531 24.2796187,3.07802293 C22.7564184,2.98530597 22.0940183,3.75319258 20.700418,3.78885295 C14.6844168,7.75349351 7.37321543,10.4391155 1.14121423,14.1890014 C0.817214165,15.0488125 0.832414168,16.2446236 0.184414043,16.7866612 C-0.134786018,20.4945471 0.0244140126,24.3633009 0.184414043,28.1306207 C0.636414131,38.759788 -0.61398611,50.9286907 0.900414181,61.9223862 C1.03721421,63.7569141 0.612414126,66.1477437 1.14121423,67.5955547 C1.20361424,68.0821206 1.77641435,68.0686489 2.09401441,68.3040073 C2.68441453,67.9965357 3.25001463,68.8325732 3.28441464,68.3040073 C3.82921475,68.945894 3.71401472,70.2431389 5.19321501,69.9602333 C6.7092153,70.1987616 7.02041536,69.2462335 7.81961551,68.7755167 C8.55721566,68.2453658 9.19161578,67.6177433 9.72761588,66.8886869 C11.2484162,65.8735551 12.3820164,64.4764611 13.7812167,63.3416689 C15.1244169,62.151405 16.4356172,60.9270657 17.8412174,59.7970282 C18.5588176,59.2494435 19.3220177,58.7454436 19.9860179,58.143972 C20.954818,57.8436325 21.3700181,56.990161 22.3724183,56.7238969 C25.671619,58.3397078 28.4380195,60.4817073 31.6764201,62.1609145 C33.2108204,63.080952 34.7596207,63.9906876 35.9692209,65.2340458 C36.8996211,65.4947627 37.6964213,65.8862343 38.3532214,66.4124229 C39.0556215,66.3529889 39.0892215,66.9489133 39.7884217,66.8886869 C39.6780217,67.6264603 40.2972218,67.6415169 40.9764219,67.5955547 C42.8076223,66.8839322 44.1868225,65.7309136 45.5140228,64.5232158 C47.0220231,63.4977821 48.5396234,62.4826502 49.8068236,61.2139336 C51.670824,60.537179 52.5228241,58.8627266 54.3404245,58.143972 C54.8748246,57.408576 55.9444248,57.2120477 56.4868249,56.487746 C57.3260251,56.0590291 57.8708252,55.3402746 58.8732254,55.0700482 C62.097226,57.1541987 64.3364264,60.2138583 67.225227,62.6324238 C68.0516271,63.0730274 68.3252272,64.0572536 69.3700274,64.2870649 C70.0164275,64.9852157 70.4732276,65.8735551 71.5172278,66.1778569 C72.2980279,66.7420832 72.3188279,68.0607244 73.6644282,68.0686489 C74.5628284,68.5171771 74.7572284,69.665441 76.2884287,69.4863467 C77.2156289,69.0417807 77.4996289,68.2849885 77.720429,67.1224604 C77.950829,65.9060456 77.545229,64.569178 77.720429,63.3416689 C78.1052291,62.4628389 78.4956291,61.5879712 78.6748292,60.5054809 C79.9460294,50.0260872 79.0812292,37.4371845 79.6292294,26.2382438 C80.0596294,24.6485838 79.8556294,22.5034145 79.8700294,20.5666603 C79.8772294,18.5760192 80.1588295,16.6004348 79.8700294,15.1304351\" id=\"Fill-1097\" fill=\"%23000000\"></path></g></g></g></svg>') !important;\n      margin-bottom: 30px; }\n    #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__tracks p {\n      margin: 0px; }\n    #vtk-yp-main ._main_boxes.__OPEN ._box_wrap ._box.__tracks .btn {\n      display: none; }\n\n#vtk-yp-main ._back_box_tracks {\n  background-color: #e9e9e9;\n  display: inline-block;\n  width: 100%; }\n  #vtk-yp-main ._back_box_tracks .row {\n    background-color: transparent; }\n  #vtk-yp-main ._back_box_tracks.__OPEN {\n    padding-bottom: 30px; }\n  #vtk-yp-main ._back_box_tracks.__CLOSE {\n    overflow: hidden;\n    height: 0; }\n  #vtk-yp-main ._back_box_tracks ._triangle {\n    width: 0;\n    height: 0;\n    border-left: 40px solid transparent;\n    border-right: 40px solid transparent;\n    display: block;\n    border-top: 40px solid white;\n    position: absolute; }\n\n@media screen and (max-width: 650px) {\n  #vtk-yp-main .__padding {\n    padding: 20px 0; } }\n\n@media screen and (min-width: 650px) {\n  #vtk-yp-main .__padding {\n    padding: 0; } }\n\n#vtk-yp-main .__categories {\n  margin-bottom: 40px; }\n\n#vtk-yp-main .row:nth-child(n+2) {\n  padding-bottom: 0px !important; }\n\n#vtk-yp-main .row:nth-child(1) {\n  padding-bottom: 0px !important; }\n\n#vtk-yp-main .__header {\n  padding: 5px 5px 5px 10px;\n  margin-bottom: 10px; }\n\n#vtk-yp-main .__list__tracks_main {\n  width: 100%;\n  height: 500px; }\n\n#vtk-yp-main .__year-plan-track-row .__year-plan-track,\n#vtk-yp-main .__year-plan-track-row .__meetings {\n  padding: 5px 0; }\n  #vtk-yp-main .__year-plan-track-row .__year-plan-track .table,\n  #vtk-yp-main .__year-plan-track-row .__meetings .table {\n    width: 100%;\n    display: table;\n    font-size: 14px; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .cell,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .cell {\n      display: table-cell;\n      vertical-align: middle; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .cell.c18,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .cell.c18 {\n      width: 60%; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .cell.c3,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .cell.c3 {\n      width: 28%;\n      text-align: right; }\n      #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .cell.c3 .button,\n      #vtk-yp-main .__year-plan-track-row .__meetings .table .cell.c3 .button {\n        width: 100%;\n        margin: 0; }\n      #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .cell.c3 .inactive,\n      #vtk-yp-main .__year-plan-track-row .__meetings .table .cell.c3 .inactive {\n        background-color: #969696 !important;\n        pointer-events: none; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .click-preview,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .click-preview {\n      color: #00A850;\n      font-weight: 600;\n      cursor: pointer;\n      padding-right: 20px; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .click-preview.__close:before,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .click-preview.__close:before {\n      position: relative;\n      content: \"\";\n      width: 0;\n      height: 0;\n      display: inline-block;\n      border-style: solid;\n      border-color: transparent transparent transparent #00A850;\n      top: 2px;\n      right: 0px;\n      border-width: 7px; }\n    #vtk-yp-main .__year-plan-track-row .__year-plan-track .table .click-preview.__open:before,\n    #vtk-yp-main .__year-plan-track-row .__meetings .table .click-preview.__open:before {\n      position: relative;\n      content: \"\";\n      width: 0;\n      height: 0;\n      display: inline-block;\n      border-style: solid;\n      border-color: #00A850 transparent transparent transparent;\n      top: 6px;\n      right: 0px;\n      border-width: 7px; }\n\n#vtk-yp-main ._year-plan-box {\n  background: #FFFFFF;\n  box-shadow: 0 0 3px 2px rgba(0, 0, 0, 0.08);\n  font-size: 14px;\n  color: #000000;\n  text-align: center;\n  line-height: 19px;\n  padding: 15px 10px 10px;\n  height: 250px;\n  width: 250px;\n  margin: 0px 12px 12px 0;\n  float: left; }\n  #vtk-yp-main ._year-plan-box .__top {\n    font-size: 14px;\n    line-height: 12px;\n    height: 48px;\n    text-align: center;\n    overflow: hidden; }\n  #vtk-yp-main ._year-plan-box img {\n    width: 220px;\n    height: 105px;\n    margin: 0 auto 30px; }\n  #vtk-yp-main ._year-plan-box.__selected {\n    box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.3); }\n\n#vtk-yp-main .__meetings {\n  padding-top: 0px !important;\n  padding-bottom: 20px;\n  background-color: transparent; }\n  #vtk-yp-main .__meetings .list-meetings {\n    background-color: white;\n    padding: 5px; }\n\n#vtk-yp-main .meeting {\n  background-color: #f6f6f6;\n  margin-bottom: 10px;\n  position: relative; }\n  #vtk-yp-main .meeting:last-child {\n    margin-bottom: 0px; }\n  #vtk-yp-main .meeting .square {\n    width: 83px;\n    height: 83px;\n    background-color: #00ae57;\n    display: inline-block;\n    float: left; }\n    #vtk-yp-main .meeting .square p {\n      margin: 0;\n      padding: 0;\n      text-align: center;\n      text-transform: uppercase;\n      color: white;\n      font-weight: 100; }\n    #vtk-yp-main .meeting .square .postion {\n      font-size: 30px; }\n  #vtk-yp-main .meeting .arrowGreen {\n    display: inline-block;\n    width: 0;\n    height: 0;\n    border-top: 16px solid transparent;\n    border-bottom: 16px solid transparent;\n    border-left: 14px solid #00ae57;\n    float: left;\n    margin-top: 27px; }\n  #vtk-yp-main .meeting .square-small {\n    width: 100%;\n    height: 83px;\n    background-color: #00ae57; }\n    #vtk-yp-main .meeting .square-small p {\n      margin: 0;\n      padding: 0;\n      text-align: center;\n      text-transform: uppercase;\n      color: white;\n      font-weight: 100; }\n    #vtk-yp-main .meeting .square-small .postion {\n      font-size: 30px; }\n  #vtk-yp-main .meeting .arrowGreen-small {\n    border-right: 16px solid transparent;\n    border-top: 14px solid #00ae57;\n    border-left: 16px solid transparent;\n    width: 0;\n    height: 0;\n    margin-left: -16px;\n    margin-top: -1px;\n    position: absolute;\n    left: 50%; }\n  #vtk-yp-main .meeting .body {\n    display: inline-block;\n    min-height: 80px; }\n    @media screen and (max-width: 650px) {\n      #vtk-yp-main .meeting .body {\n        padding: 12px 0 0 0;\n        width: 100%; } }\n    @media screen and (min-width: 650px) {\n      #vtk-yp-main .meeting .body {\n        padding: 12px 0 0 0;\n        width: 80%; } }\n    @media screen and (min-width: 940px) {\n      #vtk-yp-main .meeting .body {\n        padding: 12px 0 0 20px;\n        width: 87%; } }\n    #vtk-yp-main .meeting .body ul {\n      list-style: none;\n      margin: 0;\n      padding: 0;\n      display: table;\n      width: 100%;\n      height: 80px; }\n      #vtk-yp-main .meeting .body ul li {\n        display: table-cell;\n        vertical-align: middle;\n        height: 80px; }\n\n#vtk-yp-main .big-arrow-white {\n  height: 50px;\n  float: right;\n  width: 100%;\n  clear: both; }\n  #vtk-yp-main .big-arrow-white:after {\n    content: '';\n    width: 0;\n    height: 0;\n    border-left: 40px solid transparent;\n    border-right: 40px solid transparent;\n    border-top: 20px solid white;\n    float: right;\n    margin-right: 307px; }\n\n#vtk-yp-main .big-arrow-white-small {\n  height: 50px;\n  float: right;\n  width: 100%;\n  clear: both; }\n  #vtk-yp-main .big-arrow-white-small:after {\n    content: '';\n    width: 0;\n    height: 0;\n    border-left: 40px solid transparent;\n    border-right: 40px solid transparent;\n    border-top: 20px solid white;\n    float: right;\n    margin-right: 248px; }\n\n#vtk-yp-main .vtk-yp-link {\n  cursor: pointer;\n  font-size: 14px;\n  color: #00ae58;\n  text-align: right; }\n\n#vtk-yp-main .vtk-yp-link:hover {\n  color: green !important; }\n\n#vtk-yp-main .vtk-yp-gray {\n  width: 100%;\n  position: fixed;\n  top: 0;\n  left: 0;\n  z-index: 100;\n  background-color: black;\n  opacity: 0.8; }\n  #vtk-yp-main .vtk-yp-gray.__hidden {\n    visibility: hidden; }\n\n#vtk-yp-main .last-part .button {\n  margin: 0; }\n\n#vtk-yp-main .last-part .inactive {\n  background-color: #969696 !important;\n  pointer-events: none; }\n\n#vtk-yp-main .last-part .table {\n  width: 100%;\n  display: table;\n  font-size: 14px; }\n  #vtk-yp-main .last-part .table .cell {\n    display: table-cell;\n    vertical-align: middle; }\n  #vtk-yp-main .last-part .table .cell.c19 {\n    width: 72%; }\n  #vtk-yp-main .last-part .table .cell.c3 {\n    width: 28%;\n    text-align: right; }\n    #vtk-yp-main .last-part .table .cell.c3 .button {\n      width: 100%;\n      margin: 0; }\n    #vtk-yp-main .last-part .table .cell.c3 .inactive {\n      background-color: #969696 !important;\n      pointer-events: none; }\n  #vtk-yp-main .last-part .table .click-preview {\n    color: #00A850;\n    font-weight: 600;\n    cursor: pointer;\n    padding-right: 20px; }\n  #vtk-yp-main .last-part .table .click-preview.__close:before {\n    position: relative;\n    content: \"\";\n    width: 0;\n    height: 0;\n    display: inline-block;\n    border-style: solid;\n    border-color: transparent transparent transparent #00A850;\n    top: 2px;\n    right: 0px;\n    border-width: 7px; }\n  #vtk-yp-main .last-part .table .click-preview.__open:before {\n    position: relative;\n    content: \"\";\n    width: 0;\n    height: 0;\n    display: inline-block;\n    border-style: solid;\n    border-color: #00A850 transparent transparent transparent;\n    top: 6px;\n    right: 0px;\n    border-width: 7px; }\n\n#vtk-yp-main img._requirement_modal {\n  background-color: white;\n  -webkit-box-shadow: 6px 6px 30px -6px rgba(0, 0, 0, 0.75);\n  -moz-box-shadow: 6px 6px 30px -6px rgba(0, 0, 0, 0.75);\n  box-shadow: 6px 6px 30px -6px rgba(0, 0, 0, 0.75);\n  cursor: pointer;\n  padding: 2px;\n  border-radius: 8px; }\n\n#vtk-yp-main ._intro {\n  text-align: center;\n  padding: 15px;\n  box-shadow: 0 0 20px 3px rgba(0, 0, 0, 0.08);\n  margin-bottom: 10px; }\n\n#vtk-yp-main .btn.button {\n  padding: 11px 20px;\n  font-size: 14px;\n  margin: 0 10px 10px 0 !important;\n  min-width: 136px !important; }\n  #vtk-yp-main .btn.button.btn-line {\n    border: 1px solid #18aa51;\n    background-color: white !important;\n    color: #18aa51; }\n  #vtk-yp-main .btn.button.btn-default {\n    border: 1px solid #18aa51; }\n  #vtk-yp-main .btn.button.selected {\n    background-color: orange !important;\n    border: none; }\n    #vtk-yp-main .btn.button.selected.inactive {\n      pointer-events: none; }\n", ""]);

// exports


/***/ }),
/* 28 */
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__(3)(false);
// imports


// module
exports.push([module.i, "", ""]);

// exports


/***/ }),
/* 29 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(30);

/***/ }),
/* 30 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);
var bind = __webpack_require__(11);
var Axios = __webpack_require__(32);
var defaults = __webpack_require__(5);

/**
 * Create an instance of Axios
 *
 * @param {Object} defaultConfig The default config for the instance
 * @return {Axios} A new instance of Axios
 */
function createInstance(defaultConfig) {
  var context = new Axios(defaultConfig);
  var instance = bind(Axios.prototype.request, context);

  // Copy axios.prototype to instance
  utils.extend(instance, Axios.prototype, context);

  // Copy context to instance
  utils.extend(instance, context);

  return instance;
}

// Create the default instance to be exported
var axios = createInstance(defaults);

// Expose Axios class to allow class inheritance
axios.Axios = Axios;

// Factory for creating new instances
axios.create = function create(instanceConfig) {
  return createInstance(utils.merge(defaults, instanceConfig));
};

// Expose Cancel & CancelToken
axios.Cancel = __webpack_require__(8);
axios.CancelToken = __webpack_require__(31);
axios.isCancel = __webpack_require__(9);

// Expose all/spread
axios.all = function all(promises) {
  return Promise.all(promises);
};
axios.spread = __webpack_require__(46);

module.exports = axios;

// Allow use of default import syntax in TypeScript
module.exports.default = axios;


/***/ }),
/* 31 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var Cancel = __webpack_require__(8);

/**
 * A `CancelToken` is an object that can be used to request cancellation of an operation.
 *
 * @class
 * @param {Function} executor The executor function.
 */
function CancelToken(executor) {
  if (typeof executor !== 'function') {
    throw new TypeError('executor must be a function.');
  }

  var resolvePromise;
  this.promise = new Promise(function promiseExecutor(resolve) {
    resolvePromise = resolve;
  });

  var token = this;
  executor(function cancel(message) {
    if (token.reason) {
      // Cancellation has already been requested
      return;
    }

    token.reason = new Cancel(message);
    resolvePromise(token.reason);
  });
}

/**
 * Throws a `Cancel` if cancellation has been requested.
 */
CancelToken.prototype.throwIfRequested = function throwIfRequested() {
  if (this.reason) {
    throw this.reason;
  }
};

/**
 * Returns an object that contains a new `CancelToken` and a function that, when called,
 * cancels the `CancelToken`.
 */
CancelToken.source = function source() {
  var cancel;
  var token = new CancelToken(function executor(c) {
    cancel = c;
  });
  return {
    token: token,
    cancel: cancel
  };
};

module.exports = CancelToken;


/***/ }),
/* 32 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var defaults = __webpack_require__(5);
var utils = __webpack_require__(0);
var InterceptorManager = __webpack_require__(33);
var dispatchRequest = __webpack_require__(34);
var isAbsoluteURL = __webpack_require__(42);
var combineURLs = __webpack_require__(40);

/**
 * Create a new instance of Axios
 *
 * @param {Object} instanceConfig The default config for the instance
 */
function Axios(instanceConfig) {
  this.defaults = instanceConfig;
  this.interceptors = {
    request: new InterceptorManager(),
    response: new InterceptorManager()
  };
}

/**
 * Dispatch a request
 *
 * @param {Object} config The config specific for this request (merged with this.defaults)
 */
Axios.prototype.request = function request(config) {
  /*eslint no-param-reassign:0*/
  // Allow for axios('example/url'[, config]) a la fetch API
  if (typeof config === 'string') {
    config = utils.merge({
      url: arguments[0]
    }, arguments[1]);
  }

  config = utils.merge(defaults, this.defaults, { method: 'get' }, config);

  // Support baseURL config
  if (config.baseURL && !isAbsoluteURL(config.url)) {
    config.url = combineURLs(config.baseURL, config.url);
  }

  // Hook up interceptors middleware
  var chain = [dispatchRequest, undefined];
  var promise = Promise.resolve(config);

  this.interceptors.request.forEach(function unshiftRequestInterceptors(interceptor) {
    chain.unshift(interceptor.fulfilled, interceptor.rejected);
  });

  this.interceptors.response.forEach(function pushResponseInterceptors(interceptor) {
    chain.push(interceptor.fulfilled, interceptor.rejected);
  });

  while (chain.length) {
    promise = promise.then(chain.shift(), chain.shift());
  }

  return promise;
};

// Provide aliases for supported request methods
utils.forEach(['delete', 'get', 'head'], function forEachMethodNoData(method) {
  /*eslint func-names:0*/
  Axios.prototype[method] = function(url, config) {
    return this.request(utils.merge(config || {}, {
      method: method,
      url: url
    }));
  };
});

utils.forEach(['post', 'put', 'patch'], function forEachMethodWithData(method) {
  /*eslint func-names:0*/
  Axios.prototype[method] = function(url, data, config) {
    return this.request(utils.merge(config || {}, {
      method: method,
      url: url,
      data: data
    }));
  };
});

module.exports = Axios;


/***/ }),
/* 33 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

function InterceptorManager() {
  this.handlers = [];
}

/**
 * Add a new interceptor to the stack
 *
 * @param {Function} fulfilled The function to handle `then` for a `Promise`
 * @param {Function} rejected The function to handle `reject` for a `Promise`
 *
 * @return {Number} An ID used to remove interceptor later
 */
InterceptorManager.prototype.use = function use(fulfilled, rejected) {
  this.handlers.push({
    fulfilled: fulfilled,
    rejected: rejected
  });
  return this.handlers.length - 1;
};

/**
 * Remove an interceptor from the stack
 *
 * @param {Number} id The ID that was returned by `use`
 */
InterceptorManager.prototype.eject = function eject(id) {
  if (this.handlers[id]) {
    this.handlers[id] = null;
  }
};

/**
 * Iterate over all the registered interceptors
 *
 * This method is particularly useful for skipping over any
 * interceptors that may have become `null` calling `eject`.
 *
 * @param {Function} fn The function to call for each interceptor
 */
InterceptorManager.prototype.forEach = function forEach(fn) {
  utils.forEach(this.handlers, function forEachHandler(h) {
    if (h !== null) {
      fn(h);
    }
  });
};

module.exports = InterceptorManager;


/***/ }),
/* 34 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);
var transformData = __webpack_require__(37);
var isCancel = __webpack_require__(9);
var defaults = __webpack_require__(5);

/**
 * Throws a `Cancel` if cancellation has been requested.
 */
function throwIfCancellationRequested(config) {
  if (config.cancelToken) {
    config.cancelToken.throwIfRequested();
  }
}

/**
 * Dispatch a request to the server using the configured adapter.
 *
 * @param {object} config The config that is to be used for the request
 * @returns {Promise} The Promise to be fulfilled
 */
module.exports = function dispatchRequest(config) {
  throwIfCancellationRequested(config);

  // Ensure headers exist
  config.headers = config.headers || {};

  // Transform request data
  config.data = transformData(
    config.data,
    config.headers,
    config.transformRequest
  );

  // Flatten headers
  config.headers = utils.merge(
    config.headers.common || {},
    config.headers[config.method] || {},
    config.headers || {}
  );

  utils.forEach(
    ['delete', 'get', 'head', 'post', 'put', 'patch', 'common'],
    function cleanHeaderConfig(method) {
      delete config.headers[method];
    }
  );

  var adapter = config.adapter || defaults.adapter;

  return adapter(config).then(function onAdapterResolution(response) {
    throwIfCancellationRequested(config);

    // Transform response data
    response.data = transformData(
      response.data,
      response.headers,
      config.transformResponse
    );

    return response;
  }, function onAdapterRejection(reason) {
    if (!isCancel(reason)) {
      throwIfCancellationRequested(config);

      // Transform response data
      if (reason && reason.response) {
        reason.response.data = transformData(
          reason.response.data,
          reason.response.headers,
          config.transformResponse
        );
      }
    }

    return Promise.reject(reason);
  });
};


/***/ }),
/* 35 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * Update an Error with the specified config, error code, and response.
 *
 * @param {Error} error The error to update.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 @ @param {Object} [response] The response.
 * @returns {Error} The error.
 */
module.exports = function enhanceError(error, config, code, response) {
  error.config = config;
  if (code) {
    error.code = code;
  }
  error.response = response;
  return error;
};


/***/ }),
/* 36 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var createError = __webpack_require__(10);

/**
 * Resolve or reject a Promise based on response status.
 *
 * @param {Function} resolve A function that resolves the promise.
 * @param {Function} reject A function that rejects the promise.
 * @param {object} response The response.
 */
module.exports = function settle(resolve, reject, response) {
  var validateStatus = response.config.validateStatus;
  // Note: status is not exposed by XDomainRequest
  if (!response.status || !validateStatus || validateStatus(response.status)) {
    resolve(response);
  } else {
    reject(createError(
      'Request failed with status code ' + response.status,
      response.config,
      null,
      response
    ));
  }
};


/***/ }),
/* 37 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

/**
 * Transform the data for a request or a response
 *
 * @param {Object|String} data The data to be transformed
 * @param {Array} headers The headers for the request or response
 * @param {Array|Function} fns A single function or Array of functions
 * @returns {*} The resulting transformed data
 */
module.exports = function transformData(data, headers, fns) {
  /*eslint no-param-reassign:0*/
  utils.forEach(fns, function transform(fn) {
    data = fn(data, headers);
  });

  return data;
};


/***/ }),
/* 38 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


// btoa polyfill for IE<10 courtesy https://github.com/davidchambers/Base64.js

var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

function E() {
  this.message = 'String contains an invalid character';
}
E.prototype = new Error;
E.prototype.code = 5;
E.prototype.name = 'InvalidCharacterError';

function btoa(input) {
  var str = String(input);
  var output = '';
  for (
    // initialize result and counter
    var block, charCode, idx = 0, map = chars;
    // if the next str index does not exist:
    //   change the mapping table to "="
    //   check if d has no fractional digits
    str.charAt(idx | 0) || (map = '=', idx % 1);
    // "8 - idx % 1 * 8" generates the sequence 2, 4, 6, 8
    output += map.charAt(63 & block >> 8 - idx % 1 * 8)
  ) {
    charCode = str.charCodeAt(idx += 3 / 4);
    if (charCode > 0xFF) {
      throw new E();
    }
    block = block << 8 | charCode;
  }
  return output;
}

module.exports = btoa;


/***/ }),
/* 39 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

function encode(val) {
  return encodeURIComponent(val).
    replace(/%40/gi, '@').
    replace(/%3A/gi, ':').
    replace(/%24/g, '$').
    replace(/%2C/gi, ',').
    replace(/%20/g, '+').
    replace(/%5B/gi, '[').
    replace(/%5D/gi, ']');
}

/**
 * Build a URL by appending params to the end
 *
 * @param {string} url The base of the url (e.g., http://www.google.com)
 * @param {object} [params] The params to be appended
 * @returns {string} The formatted url
 */
module.exports = function buildURL(url, params, paramsSerializer) {
  /*eslint no-param-reassign:0*/
  if (!params) {
    return url;
  }

  var serializedParams;
  if (paramsSerializer) {
    serializedParams = paramsSerializer(params);
  } else if (utils.isURLSearchParams(params)) {
    serializedParams = params.toString();
  } else {
    var parts = [];

    utils.forEach(params, function serialize(val, key) {
      if (val === null || typeof val === 'undefined') {
        return;
      }

      if (utils.isArray(val)) {
        key = key + '[]';
      }

      if (!utils.isArray(val)) {
        val = [val];
      }

      utils.forEach(val, function parseValue(v) {
        if (utils.isDate(v)) {
          v = v.toISOString();
        } else if (utils.isObject(v)) {
          v = JSON.stringify(v);
        }
        parts.push(encode(key) + '=' + encode(v));
      });
    });

    serializedParams = parts.join('&');
  }

  if (serializedParams) {
    url += (url.indexOf('?') === -1 ? '?' : '&') + serializedParams;
  }

  return url;
};


/***/ }),
/* 40 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * Creates a new URL by combining the specified URLs
 *
 * @param {string} baseURL The base URL
 * @param {string} relativeURL The relative URL
 * @returns {string} The combined URL
 */
module.exports = function combineURLs(baseURL, relativeURL) {
  return baseURL.replace(/\/+$/, '') + '/' + relativeURL.replace(/^\/+/, '');
};


/***/ }),
/* 41 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

module.exports = (
  utils.isStandardBrowserEnv() ?

  // Standard browser envs support document.cookie
  (function standardBrowserEnv() {
    return {
      write: function write(name, value, expires, path, domain, secure) {
        var cookie = [];
        cookie.push(name + '=' + encodeURIComponent(value));

        if (utils.isNumber(expires)) {
          cookie.push('expires=' + new Date(expires).toGMTString());
        }

        if (utils.isString(path)) {
          cookie.push('path=' + path);
        }

        if (utils.isString(domain)) {
          cookie.push('domain=' + domain);
        }

        if (secure === true) {
          cookie.push('secure');
        }

        document.cookie = cookie.join('; ');
      },

      read: function read(name) {
        var match = document.cookie.match(new RegExp('(^|;\\s*)(' + name + ')=([^;]*)'));
        return (match ? decodeURIComponent(match[3]) : null);
      },

      remove: function remove(name) {
        this.write(name, '', Date.now() - 86400000);
      }
    };
  })() :

  // Non standard browser env (web workers, react-native) lack needed support.
  (function nonStandardBrowserEnv() {
    return {
      write: function write() {},
      read: function read() { return null; },
      remove: function remove() {}
    };
  })()
);


/***/ }),
/* 42 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * Determines whether the specified URL is absolute
 *
 * @param {string} url The URL to test
 * @returns {boolean} True if the specified URL is absolute, otherwise false
 */
module.exports = function isAbsoluteURL(url) {
  // A URL is considered absolute if it begins with "<scheme>://" or "//" (protocol-relative URL).
  // RFC 3986 defines scheme name as a sequence of characters beginning with a letter and followed
  // by any combination of letters, digits, plus, period, or hyphen.
  return /^([a-z][a-z\d\+\-\.]*:)?\/\//i.test(url);
};


/***/ }),
/* 43 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

module.exports = (
  utils.isStandardBrowserEnv() ?

  // Standard browser envs have full support of the APIs needed to test
  // whether the request URL is of the same origin as current location.
  (function standardBrowserEnv() {
    var msie = /(msie|trident)/i.test(navigator.userAgent);
    var urlParsingNode = document.createElement('a');
    var originURL;

    /**
    * Parse a URL to discover it's components
    *
    * @param {String} url The URL to be parsed
    * @returns {Object}
    */
    function resolveURL(url) {
      var href = url;

      if (msie) {
        // IE needs attribute set twice to normalize properties
        urlParsingNode.setAttribute('href', href);
        href = urlParsingNode.href;
      }

      urlParsingNode.setAttribute('href', href);

      // urlParsingNode provides the UrlUtils interface - http://url.spec.whatwg.org/#urlutils
      return {
        href: urlParsingNode.href,
        protocol: urlParsingNode.protocol ? urlParsingNode.protocol.replace(/:$/, '') : '',
        host: urlParsingNode.host,
        search: urlParsingNode.search ? urlParsingNode.search.replace(/^\?/, '') : '',
        hash: urlParsingNode.hash ? urlParsingNode.hash.replace(/^#/, '') : '',
        hostname: urlParsingNode.hostname,
        port: urlParsingNode.port,
        pathname: (urlParsingNode.pathname.charAt(0) === '/') ?
                  urlParsingNode.pathname :
                  '/' + urlParsingNode.pathname
      };
    }

    originURL = resolveURL(window.location.href);

    /**
    * Determine if a URL shares the same origin as the current location
    *
    * @param {String} requestURL The URL to test
    * @returns {boolean} True if URL shares the same origin, otherwise false
    */
    return function isURLSameOrigin(requestURL) {
      var parsed = (utils.isString(requestURL)) ? resolveURL(requestURL) : requestURL;
      return (parsed.protocol === originURL.protocol &&
            parsed.host === originURL.host);
    };
  })() :

  // Non standard browser envs (web workers, react-native) lack needed support.
  (function nonStandardBrowserEnv() {
    return function isURLSameOrigin() {
      return true;
    };
  })()
);


/***/ }),
/* 44 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

module.exports = function normalizeHeaderName(headers, normalizedName) {
  utils.forEach(headers, function processHeader(value, name) {
    if (name !== normalizedName && name.toUpperCase() === normalizedName.toUpperCase()) {
      headers[normalizedName] = value;
      delete headers[name];
    }
  });
};


/***/ }),
/* 45 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var utils = __webpack_require__(0);

/**
 * Parse headers into an object
 *
 * ```
 * Date: Wed, 27 Aug 2014 08:58:49 GMT
 * Content-Type: application/json
 * Connection: keep-alive
 * Transfer-Encoding: chunked
 * ```
 *
 * @param {String} headers Headers needing to be parsed
 * @returns {Object} Headers parsed into an object
 */
module.exports = function parseHeaders(headers) {
  var parsed = {};
  var key;
  var val;
  var i;

  if (!headers) { return parsed; }

  utils.forEach(headers.split('\n'), function parser(line) {
    i = line.indexOf(':');
    key = utils.trim(line.substr(0, i)).toLowerCase();
    val = utils.trim(line.substr(i + 1));

    if (key) {
      parsed[key] = parsed[key] ? parsed[key] + ', ' + val : val;
    }
  });

  return parsed;
};


/***/ }),
/* 46 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * Syntactic sugar for invoking a function and expanding an array for arguments.
 *
 * Common use case would be to use `Function.prototype.apply`.
 *
 *  ```js
 *  function f(x, y, z) {}
 *  var args = [1, 2, 3];
 *  f.apply(null, args);
 *  ```
 *
 * With `spread` this example can be re-written.
 *
 *  ```js
 *  spread(function(x, y, z) {})([1, 2, 3]);
 *  ```
 *
 * @param {Function} callback
 * @returns {Function}
 */
module.exports = function spread(callback) {
  return function wrap(arr) {
    return callback.apply(null, arr);
  };
};


/***/ }),
/* 47 */
/***/ (function(module, exports) {


/**
 * When source maps are enabled, `style-loader` uses a link element with a data-uri to
 * embed the css on the page. This breaks all relative urls because now they are relative to a
 * bundle instead of the current page.
 *
 * One solution is to only use full urls, but that may be impossible.
 *
 * Instead, this function "fixes" the relative urls to be absolute according to the current page location.
 *
 * A rudimentary test suite is located at `test/fixUrls.js` and can be run via the `npm test` command.
 *
 */

module.exports = function (css) {
  // get current location
  var location = typeof window !== "undefined" && window.location;

  if (!location) {
    throw new Error("fixUrls requires window.location");
  }

	// blank or null?
	if (!css || typeof css !== "string") {
	  return css;
  }

  var baseUrl = location.protocol + "//" + location.host;
  var currentDir = baseUrl + location.pathname.replace(/\/[^\/]*$/, "/");

	// convert each url(...)
	/*
	This regular expression is just a way to recursively match brackets within
	a string.

	 /url\s*\(  = Match on the word "url" with any whitespace after it and then a parens
	   (  = Start a capturing group
	     (?:  = Start a non-capturing group
	         [^)(]  = Match anything that isn't a parentheses
	         |  = OR
	         \(  = Match a start parentheses
	             (?:  = Start another non-capturing groups
	                 [^)(]+  = Match anything that isn't a parentheses
	                 |  = OR
	                 \(  = Match a start parentheses
	                     [^)(]*  = Match anything that isn't a parentheses
	                 \)  = Match a end parentheses
	             )  = End Group
              *\) = Match anything and then a close parens
          )  = Close non-capturing group
          *  = Match anything
       )  = Close capturing group
	 \)  = Match a close parens

	 /gi  = Get all matches, not the first.  Be case insensitive.
	 */
	var fixedCss = css.replace(/url\s*\(((?:[^)(]|\((?:[^)(]+|\([^)(]*\))*\))*)\)/gi, function(fullMatch, origUrl) {
		// strip quotes (if they exist)
		var unquotedOrigUrl = origUrl
			.trim()
			.replace(/^"(.*)"$/, function(o, $1){ return $1; })
			.replace(/^'(.*)'$/, function(o, $1){ return $1; });

		// already a full url? no change
		if (/^(#|data:|http:\/\/|https:\/\/|file:\/\/\/)/i.test(unquotedOrigUrl)) {
		  return fullMatch;
		}

		// convert the url to a full url
		var newUrl;

		if (unquotedOrigUrl.indexOf("//") === 0) {
		  	//TODO: should we add protocol?
			newUrl = unquotedOrigUrl;
		} else if (unquotedOrigUrl.indexOf("/") === 0) {
			// path should be relative to the base url
			newUrl = baseUrl + unquotedOrigUrl; // already starts with '/'
		} else {
			// path should be relative to current directory
			newUrl = currentDir + unquotedOrigUrl.replace(/^\.\//, ""); // Strip leading './'
		}

		// send back the fixed url(...)
		return "url(" + JSON.stringify(newUrl) + ")";
	});

	// send back the fixed css
	return fixedCss;
};


/***/ }),
/* 48 */
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__(24);
if(typeof content === 'string') content = [[module.i, content, '']];
// Prepare cssTransformation
var transform;

var options = {}
options.transform = transform
// add the styles to the DOM
var update = __webpack_require__(4)(content, options);
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./head.scss", function() {
			var newContent = require("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./head.scss");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ }),
/* 49 */
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__(25);
if(typeof content === 'string') content = [[module.i, content, '']];
// Prepare cssTransformation
var transform;

var options = {}
options.transform = transform
// add the styles to the DOM
var update = __webpack_require__(4)(content, options);
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./meeting.scss", function() {
			var newContent = require("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./meeting.scss");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ }),
/* 50 */
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__(26);
if(typeof content === 'string') content = [[module.i, content, '']];
// Prepare cssTransformation
var transform;

var options = {}
options.transform = transform
// add the styles to the DOM
var update = __webpack_require__(4)(content, options);
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./vtk-popup.scss", function() {
			var newContent = require("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./vtk-popup.scss");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ }),
/* 51 */
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__(27);
if(typeof content === 'string') content = [[module.i, content, '']];
// Prepare cssTransformation
var transform;

var options = {}
options.transform = transform
// add the styles to the DOM
var update = __webpack_require__(4)(content, options);
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./vtk-yp-main.scss", function() {
			var newContent = require("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./vtk-yp-main.scss");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ }),
/* 52 */
/***/ (function(module, exports, __webpack_require__) {

// style-loader: Adds some css to the DOM by adding a <style> tag

// load the styles
var content = __webpack_require__(28);
if(typeof content === 'string') content = [[module.i, content, '']];
// Prepare cssTransformation
var transform;

var options = {}
options.transform = transform
// add the styles to the DOM
var update = __webpack_require__(4)(content, options);
if(content.locals) module.exports = content.locals;
// Hot Module Replacement
if(false) {
	// When the styles change, update the <style> tags
	if(!content.locals) {
		module.hot.accept("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./year-plan-track.scss", function() {
			var newContent = require("!!./node_modules/css-loader/index.js!./node_modules/sass-loader/lib/loader.js!./year-plan-track.scss");
			if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
			update(newContent);
		});
	}
	// When the module is disposed, remove the <style> tags
	module.hot.dispose(function() { update(); });
}

/***/ })
/******/ ]);
//# sourceMappingURL=vtk-yp-app.js.map
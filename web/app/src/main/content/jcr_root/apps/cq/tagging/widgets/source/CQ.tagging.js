/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

// create tagging package
CQ.Ext.ns("CQ.tagging");

// calls the TagJsonServlet on the server (meta data for one tag)
CQ.tagging.TAG_JSON_SUFFIX = ".tag.json";

// calls the TagTreeServlet on the server (full tree of namespace)
// @deprecated
CQ.tagging.TAG_TREE_JSON_SUFFIX = ".tagtree.json";

// calls the TagListServlet on the server (list of tags below certain tag with all details)
CQ.tagging.TAG_LIST_JSON_SUFFIX = ".tags.json";

// name of the default namespace
CQ.tagging.DEFAULT_NAMESPACE = "default";

// where the tag languages are defined ("languages" multi-value property)
CQ.tagging.LANGUAGES_URL = "/etc/tags.json";

// private - splits tagID into namespace and local (also works for title paths)
CQ.tagging.parseTag = function(tag, isPath) {
    var tagInfo = {
        namespace: null,
        local: tag,
        getTagID: function() {
            return this.namespace + ":" + this.local;
        }
    };

    // parse tag pattern: namespace:local
    var colonPos = tag.indexOf(isPath ? '/' : ':');
    if (colonPos > 0) {
        // the first colon ":" delimits a namespace
        // don't forget to trim the strings (in case of title paths)
        tagInfo.namespace = tag.substring(0, colonPos).trim();
        tagInfo.local = tag.substring(colonPos + 1).trim();
    }
    
    return tagInfo;
};

// private - same as parseTag(), but only suited for tagIDs and returns namespace = "default" if no namespace is given
CQ.tagging.parseTagID = function(tagID) {
    var tagInfo = CQ.tagging.parseTag(tagID);
    if (tagInfo.namespace === null) {
        tagInfo.namespace = CQ.tagging.DEFAULT_NAMESPACE;
    }
    return tagInfo;
};

// makes sure the locale code is in the form "de_de", not "de-DE"
CQ.tagging.getTagLocaleCode = function(code) {
    if (code == null) {
        return null;
    }
    return code.replace("-", "_").toLowerCase();
};

// private
CQ.tagging.getLocalizedTitle = function(tag, locale, attr, name) {
	if (locale && locale.code) {
        return tag[attr + "." + locale.code.toLowerCase()] ||
               tag[attr + "." + locale.language] ||
               tag[attr] ||
               name;
    } else {
        return tag[attr] || name;
    }
};

// private
CQ.tagging.getLocaleSelectCombo = function(selectFn, value) {
    selectFn = selectFn || CQ.Ext.emptyFn;
    value = value ? CQ.tagging.getTagLocaleCode(value) : null;
    return {
        ref: "../localeSelect",
        xtype: "combo",
        width: 150,
        // avoids closing of the parent popupMenu when clicking in the combo list
        listClass: "x-menu",
        forceSelection: true,
        selectOnFocus: true,
        triggerAction: "all",
        mode: "local", // we load the store manually
        valueField: "locale",
        displayField: "title",
        store: new CQ.Ext.data.Store({
            proxy: new CQ.Ext.data.HttpProxy({
                url: CQ.tagging.LANGUAGES_URL,
                method: "GET"
            }),
            reader: new CQ.Ext.data.ArrayReader({
                root: "languages",
                fields: [{
                        name: "locale",
                        mapping: 0,
                        // languages is an array of strings, hence "value" points to the full string
                        convert: function(v, value) {
                            // make sure "de_de" style is enforced (and not "de-DE")
                            return CQ.tagging.getTagLocaleCode(value);
                        }
                    },{
                        name: "title",
                        mapping: 0,
                        convert: function(v, value) {
                            // make sure "de_de" style is enforced (and not "de-DE")
                            value = CQ.tagging.getTagLocaleCode(value);
                            var l = CQ.I18n.getLanguages()[value];
                            return (l && l.title) ? l.title : value;
                        }
                    }
                ]
            })
        }),
        listeners: {
            select: function(cb) {
                selectFn( CQ.tagging.getTagLocaleCode(cb.getValue()) );
            },
            render: function(cb) {
                if (value) {
                    cb.loadAndSetValue(value);
                }
            }
        },
        loadAndSetValue: function(value) {
            value = CQ.tagging.getTagLocaleCode(value);
            this.getStore().on("load", function() {
                this.setLocale(value);
            }, this);
            this.getStore().on("exception", function() {
                this.setLocale(value);
            }, this);
            this.getStore().load();
        },
        // like setValue, but adds the value + title to the store if it does not exist there yet
        setLocale: function(locale) {
            var store = this.getStore();
            var pos = store.findExact("locale", locale);
            if (pos >= 0) {
                this.setValue(locale);
            } else {
                var langs = CQ.I18n.getLanguages();
                if (langs[locale]) {
                    var data = {};
                    data.locale = locale;
                    data.title = langs[locale].title;
                    store.add([ new store.recordType(data) ]);
                    this.setValue(locale);
                }
            }
        }
    };    
};
/*
 * Copyright 1997-2009 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

/**
 * @class CQ.User
 * A helper class providing information about a CQ user as well as
 * methods to manipulate it. Use the static method <code>CQ.User.getCurrentUser()</code>
 * to retrieve an instance of this class for the current user.
 * @singleton
 */
CQ.User = function(infoData) {
    this.data = CQ.shared.User.init(infoData);
    if (this.data) {
        if (this.data.preferences) {
            this.preferencesProvider = new CQ.state.PreferencesProvider({
                data:this.data.preferences,
                id:CQ.User.PREFERENCES_PROVIDER_ID
            });
        }
        var data = new Object();
        var fields = new Array();
        if (this.data.permissions) {
            //build a store:
            //need a name value
            var per = this.data.permissions;
            var i = 0;
            for (var n in per) {
                data[i++] = n;
                fields.push(n);
            }
        }
        this.permissionStore = new CQ.Ext.data.SimpleStore({
            "id":0,
            "data":[data],
            "fields":fields,
            "autoLoad": false
        });
    } else {
        this.data = {};
    }
};

CQ.User.prototype = function() {

    return {

        /**
         * @property {Object} data
         * The user data.
         * @private
         */
        data: null,

        /**
         * @property {String} language
         * Resolved language read from preferences
         * @private
         */
        language: null,

        /**
         * @property {CQ.state.PreferencesProvider} preferencesProvider
         * The StateProvider used to access the user preferences.
         * @private
         */
        preferencesProvider:null,

        /**
         * @property {String} userPropsPath
         * The path where user properties may be requested from.
         * @private
         */
        userPropsPath: null,

        /**
         * @property {CQ.Ext.data.Store} permissionStore
         * The store containing permissions per path.
         * @private
         */
        permissionStore: null,

        /**
         * Returns true if the user is the result of impersonation.
         * @return {Boolean} True if user is impersonated
         */
        isImpersonated: function() {
            return this.data.impersonated;
        },

        /**
         * @deprecated Use {@link #isImpersonated} instead
         * @private
         */
        isImperonated: function() {
            return this.isImpersonated();
        },

        /**
         * Returns the ID of the user.
         * @return {String} The user ID
         */
        getUserID: function() {
            return this.data.userID;
        },

        /**
         * @deprecated Will be removed with no replacement. Returns <code>null</code> always.
         */
        getAuthType: function() {
            return null;
        },

        /**
         * Returns the name of the user.
         * @return {String} The user name
         */
        getUserName: function(protectXss) {
            return protectXss && this.data[CQ.shared.XSS.getXSSPropertyName("userName")] ? this.data[CQ.shared.XSS.getXSSPropertyName("userName")] : this.data.userName;
        },

        /**
         * Returns the home of the user.
         * @return {String} The user home
         */
        getHome: function() {
            return CQ.HTTP.encodePath(this.data.home);
        },

        /**
         * Returns the language selected by the user.
         * @return {String} The language
         */
        getLanguage: function() {
            if (!this.language && this.preferencesProvider) {
                var lang  = this.preferencesProvider.get(CQ.User.LANGUAGE);
                if (!lang) {
                    lang = this.preferencesProvider.get(CQ.User.PLATFORM_LANGUAGE);
                }
                this.language = lang || "en";
            }
            return this.language;
        },

        /**
         * Returns the language selected by the user as a locale object.
         * @return {Object} an object with "code" ("de_CH"), "language" ("de") and "country" ("CH")
         *                  (or null if the user has no language)
         * @since 5.4
         */
        getLocale: function() {
            return CQ.WCM.parseLocale(this.getLanguage());
        },

        /**
         * Returns the applications the user has permission to use.
         * @return {String[]} The allowed applications
         */
        getAllowedApps: function() {
            return this.data.allowedApps;
        },

        /**
         * Returns the specified user preference.
         * @param {String} name The preference
         * @return {String} The preference
         */
        getPreference: function(name) {
            return this.preferencesProvider? this.preferencesProvider.get(name) : null;
        },

        /**
         * Returns true if the user has the specified permission.
         * @param {String} permission The permission
         * @return {Boolean} True if user has permission
         */
        hasPermission: function(permission) {
            return this.hasPermissionOn(permission);
        },

        /**
         * Returns true if the user has the specified permission on a
         * certain path.
         * @param {String} permission The permission
         * @param {String} path (optional) The path to check (defaults to "/")
         * @return {Boolean} True if user has permission
         */
        hasPermissionOn: function(permission, path) {
            /**
             * check if there is a store registered for permission or fall back to current
             */
            var store = CQ.Ext.StoreMgr.lookup(CQ.User.PRIVILEGES_STORE_ID);
            if (!store) {
                store = this.permissionStore;
            }
            if (!path) {
                path = "/";
            }
            var rec = store.getById(path);
            return rec!=undefined && rec && rec.get(permission);
        },

        /**
         * Starts impersonating the user with the specified ID.
         * @param {String} asUser The user ID
         */
        sudoAs: function(asUser) {
            this.postImpersonation(asUser);
        },

        /**
         * Stops impersonating.
         */
        revertSelf:function() {
            this.postImpersonation("-");
        },

        /**
         * Logs the user in with the specified password.
         * @param {String} pwd The password
         */
        login:function(pwd) {
            CQ.User.login(null, pwd);
        },

        /**
         * Assembles the url to request the user properties from.
         * Apply default if no path has been set.
         * @private
         */
        getUserPropsUrl: function() {
            if(!this.userPropsPath) {
                this.userPropsPath = CQ.shared.User.PROXY_URI;
            }
            return this.userPropsPath;
        },

        /**
         * Posts impersonation of the specified user to the server.
         * @param {String} user The user ID
         * @private
         */
        postImpersonation: function(user) {
            var path = this.getHome()+".impersonate" + CQ.HTTP.EXTENSION_JSON;
            path = CQ.HTTP.addParameter(path, "impersonate", user);
            var href = CQ.Ext.getDoc().dom.URL;
            path = CQ.HTTP.addParameter(path, "path", CQ.HTTP.getPath(href));
            path = CQ.HTTP.noCaching(CQ.HTTP.externalize(path)+CQ.HTTP.EXTENSION_HTML);
            CQ.shared.Util.reload(document, path);
        },

        /**
         * Sets the store to load permissions from.
         * @param {CQ.Ext.data.Store} store The store
         * @private
         */
         setPermissionStore:function(store) {
             this.permissionStore = store;
         },

        /**
         * @private
         * @param {String} href
         * @deprecated Use {@link CQ.shared.HTTP#getPath} instead
         */
        getPath: function(href) {
            var path = href.replace(/.*\:[\/][\/]/, ""); // remove protocol
            path = path.substring(path.indexOf("/")); // remove host[:port]
            if (path.indexOf("?") >= 0) {
                path = path.substring(0, path.indexOf("?")); // remove query
            }
            if (path.indexOf("#") >= 0) {
                path = path.substring(0, path.indexOf("#")); // remove hash
            }
            if (path.indexOf(".")>-1) {
                path = path.substring(0, path.indexOf('.'));
            }
            return path;
        }
    };
}();

/**
 * Returns an instance of the current user.
 * @static
 * @return {CQ.User} The current user
 */
CQ.User.getCurrentUser = function() {
    //todo: check if user reloads for login/logout/sudo
    var top = CQ.WCM.getTopWindow();
    if (!top.CQ_User) {
        top.CQ_User = new CQ.User();
    }
    return top.CQ_User;
};
/**
 * Returns the ID of the current user.
 * @static
 * @return {String} The user ID
 * @deprecated Use {@link CQ.User#getUserID CQ.User.getCurrentUser().getUserID()} instead.
 */
CQ.User.getUserID = function() {
    return CQ.User.getCurrentUser().getUserID();
};

/**
 * Returns the name of the current user.
 * @static
 * @param {Boolean} Protect from XSS (optional, defaults to false)
 * @return {String} The user name
 * @deprecated Use {@link CQ.User#getUserName CQ.User.getCurrentUser().getUserName()} instead.
 */
CQ.User.getUserName = function(protextXSS) {
    return CQ.User.getCurrentUser().getUserName(protextXSS);
};

/**
 * The default ID of the preferences provider.
 * @static
 * @final
 * @type String
 */
CQ.User.PREFERENCES_PROVIDER_ID = "cq-security-preferences-provider";

/**
 * The default ID of the privileges store.
 * @static
 * @final
 * @type String
 */
CQ.User.PRIVILEGES_STORE_ID = "cq-security-privileges-store";

/**
 * The update permission (defaults to "update").
 * @static
 * @final
 * @type String
 */
CQ.User.PERMISSION_UPDATE = "update";

/**
 * The ID of the admin user (defaults to "admin").
 * @static
 * @final
 * @type String
 */
CQ.User.ADMIN = "admin";

/**
 * The ID of the anonymous user (defaults to "anonymous").
 * @static
 * @final
 * @type String
 */
CQ.User.ANONYMOUS = "anonymous";

/**
 * The path to the language property in the user info (defaults to "language").
 * @static
 * @final
 * @type String
 */
CQ.User.LANGUAGE = "language";

/**
 * The path to the platform language property in the user info (defaults to
 * "platform/language").
 * @static
 * @final
 * @type String
 */
CQ.User.PLATFORM_LANGUAGE = "platform/language";

/**
 * Login servlet URL
 * @static
 * @final
 * @type String
 * @deprecated since 5.5, use {@link CQ.Sling#LOGIN_URL} instead
 */
CQ.User.LOGIN_SERVLET_URL = CQ.Sling.LOGIN_URL;


/**
 * Login servlet URL
 * @static
 * @final
 * @type String
 * @deprecated since 5.5, use {@link CQ.Sling#LOGIN_URL} instead
 */
CQ.User.TOKEN_LOGIN_SERVLET_URL = CQ.Sling.LOGIN_URL;

/**
 * Logout URL
 * @static
 * @final
 * @type String
 * @deprecated since 5.5, use {@link CQ.Sling#LOGOUT_URL} instead
 */
CQ.User.LOGOUT_URL = CQ.Sling.LOGOUT_URL;

/**
 * Logs in with the specified user ID and password.
 * @static
 * @param {String} user The user ID
 * @param {String} pass The password
 */
CQ.User.login = function(user, pass) {

    var loginServlet = CQ.HTTP.externalize(CQ.Sling.LOGIN_URL);
    var url = CQ.HTTP.addParameter(loginServlet, "resource", CQ.utils.WCM.getPagePath() || "/");

    if (navigator.userAgent.indexOf("MSIE") > 0 || navigator.userAgent.indexOf("Firefox") > 0) {
        var xmlhttp;
        if (window.XMLHttpRequest) {
            xmlhttp = new XMLHttpRequest();
        } else {
            if (window.ActiveXObject) {
                try {
                    xmlhttp = new ActiveXObject('Msxml2.XMLHTTP');
                } catch (ex) {
                    xmlhttp = new ActiveXObject('Microsoft.XMLHTTP');
                }
            }
        }

        if (xmlhttp.readyState < 4) {
            xmlhttp.abort();
        }

        if (!user) {
            user = CQ.User.getCurrentUser().getUserID();
        }


        // send the authentication request
        xmlhttp.open('POST', url, false, user, pass);
        xmlhttp.send('');
        return xmlhttp.status == 200;

    } else {
        // send the authentication request
        var response = CQ.HTTP.post(url, callback, {
            "_charset_": "utf-8",
            "resource": CQ.utils.WCM.getPagePath(),
            "j_username": user.getUserID(),
            "j_password": pass
        });

        return CQ.HTTP.isOk(response);
    }
};

/**
 * Logs the current user out.
 * @static
 * @since 5.3
 */
CQ.User.logout = function() {
    if (CQ.shared.ClientSidePersistence) {
        CQ.shared.ClientSidePersistence.clearAllMaps();
    }

    CQ.Util.reload(null, CQ.HTTP.externalize(CQ.Sling.LOGOUT_URL));
};

// backward compatibility
CQ.utils.User = CQ.User;
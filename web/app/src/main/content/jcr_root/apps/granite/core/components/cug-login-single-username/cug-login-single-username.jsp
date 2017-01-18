<%--

ADOBE CONFIDENTIAL
__________________

Copyright 2012 Adobe Systems Incorporated
All Rights Reserved.

NOTICE:  All information contained herein is, and remains
the property of Adobe Systems Incorporated and its suppliers,
if any.  The intellectual and technical concepts contained
herein are proprietary to Adobe Systems Incorporated and its
suppliers and are protected by trade secret or copyright law.
Dissemination of this information or reproduction of this material
is strictly forbidden unless prior written permission is obtained
from Adobe Systems Incorporated.
--%>
<%@page session="false"
        contentType="text/html"
        pageEncoding="utf-8"
        import="java.util.HashMap,
                  java.util.Map,
                  java.util.Iterator,
                  org.apache.commons.io.IOUtils,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.xss.XSSAPI,
                  com.day.cq.i18n.I18n,
                  com.day.cq.widget.HtmlLibrary,
                  com.day.cq.widget.HtmlLibraryManager,
                  com.day.cq.widget.LibraryType,
                  org.apache.sling.auth.core.AuthUtil"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><%@ taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %><%--
login
=====

    The component to render the login screen.

    It has the following content structure:

   /**
    * The HTML title.
    * Defaults to "Adobe Marketing Cloud".
    */
    - title (String)


   /**
    * The favicon.
    * Defaults to "login/adobe-logo.png".
    */
    - favicon (String)


   /**
    * The title in the box.
    * Defaults to "Welcome to Adobe Marketing Cloud".
    */
    - box/title (String)


   /**
    * The text in the box.
    * Defaults to "All the tools you need to solve these complex digital business challenges.".
    */
    - box/text (String)


   /**
    * The text of the learn more link. The link is following the text.
    * Defaults to "Learn More".
    */
    - /box/learnMore/text (String)


   /**
    * The href of the learn more link.
    * Defaults to "#".
    */
    - /box/learnMore/link (String)


   /**
    * Enables autocomplete for fields username and password.
    * Defaults to "false".
    */
    - box/autocomplete (Boolean)


   /**
    * The title of the login form. Note that this title is not shown in browsers that display field labels instead of
    * placeholders (IE8 and older).
    * Defaults to "Sign In".
    */
    - box/formTitle (String)


   /**
    * The title of the change password form. Note that this title is not shown in browsers that display field labels instead of
    * placeholders (IE8 and older).
    * Defaults to "Change Password".
    */
    - box/changePasswordTitle (String)


   /**
    * The placeholder of the user field.
    * Defaults to "User name".
    */
    - box/userPlaceholder (String)


   /**
    * The placeholder of the password field in the login form.
    * Defaults to "Password".
    */
    - box/passwordPlaceholder (String)


   /**
    * The placeholder of the password field in the change password form.
    * Defaults to "Old password".
    */
    - box/oldPasswordPlaceholder (String)


   /**
    * The placeholder of the new password field.
    * Defaults to "New password".
    */
    - box/newPasswordPlaceholder (String)


   /**
    * The placeholder of the confirm password field.
    * Defaults to "Confirm new password".
    */
    - box/confirmPasswordPlaceholder (String)


   /**
    * The text of the submit button in the login form.
    * Defaults to "Sign In".
    */
    - box/submitText (String)


   /**
    * The text of the submit button in the change password form.
    * Defaults to "Submit".
    */
    - box/changePasswordSubmitText (String)


   /**
    * The text of the back button.
    * Defaults to "Back".
    */
    - box/backText (String)


   /**
    * The error message displayed when login fails.
    * Defaults to "User name and password do not match".
    */
    - box/invalidLoginText (String)


   /**
    * The error message displayed when the session timed out.
    * Defaults to "Session timed out, please login again".
    */
    - box/sessionTimedOutText (String)


   /**
    * The error message displayed when the password is expired.
    * Defaults to "Your password has expired".
    */
    - box/loginExpiredText (String)


   /**
    * The error message displayed when the new and confirm passwords do not match.
    * Defaults to "New passwords do not match".
    */
    - box/passwordsDoNotMatchText (String)


   /**
    * The error message displayed when the new password is blank.
    * Defaults to "New password must not be blank".
    */
    - box/passwordEmptyText (String)


    /**
     * The title of the success modal.
     * Defaults to "Password Changed"
     */
     - changePasswordSuccessTitle


    /**
     * The text of the success modal.
     * Defaults to "Your password has been changed successfully."
     */
     - changePasswordSuccessText


   /**
    * The items on the left side of the footer.
    * Default items are "Help", "Term of Use" and "Privacy Policy and Cookies".
    */
    - footer/items (String)


   /**
    * The copyright on the right side of the footer.
    * Defaults to "© 2014 Adobe Systems Incorporated. All Rights Reserved.".
    */
    - footer/copy/text (String)


--%><%!

    static final String PARAM_NAME_REASON = "j_reason";

    static final String REASON_KEY_INVALID_LOGIN = "invalid_login";
    static final String REASON_KEY_SESSION_TIMED_OUT = "session_timed_out";

    String printProperty(ValueMap cfg, I18n i18n, XSSAPI xssAPI, String name, String defaultText) {
        String text = cfg.get(name, String.class);
        return xssAPI.encodeForHTML( text != null ? i18n.getVar(text) : defaultText );
    }

    /**
     * Select the configuration root resource among those stored under <code>configs</code> node.
     * The configuration with the highest order property is selected.
     * @param current the
     * @return the selected configuration root resource or <code>null</code> if no configuration root could be found.
     */
    Resource getConfigRoot(Resource current) {
        Resource configs = current.getChild("configs");
        Resource configRoot = null;
        if (configs != null) {
            long maxOrder = Long.MIN_VALUE;
            for (Iterator<Resource> cfgs = configs.listChildren() ; cfgs.hasNext() ; ) {
                Resource cfg = cfgs.next();
                ValueMap props = ResourceUtil.getValueMap(cfg);
                Long order = props.get("order", Long.class);
                if (order != null) {
                    if (order > maxOrder) {
                        configRoot = cfg;
                        maxOrder = order;
                    }
                }
            }
        }
        return configRoot;
    }

%><sling:defineObjects /><%

    final Resource configs = getConfigRoot(resource);

    final I18n i18n = new I18n(slingRequest);
    final XSSAPI xssAPI = sling.getService(XSSAPI.class).getRequestSpecificAPI(slingRequest);
    final ValueMap cfg = ResourceUtil.getValueMap(configs);

    final String authType = request.getAuthType();
    final String user = request.getRemoteUser();
    final String contextPath = slingRequest.getContextPath();

    // used to map readable reason codes to valid reason messages to avoid phishing attacks through j_reason param
    Map<String,String> validReasons = new HashMap<String, String>();
    validReasons.put(REASON_KEY_INVALID_LOGIN, printProperty(cfg, i18n, xssAPI, "box/invalidLoginText", i18n.get("User name and password do not match")));
    validReasons.put(REASON_KEY_SESSION_TIMED_OUT, printProperty(cfg, i18n, xssAPI, "box/sessionTimedOutText", i18n.get("Session timed out, please login again")));

    String reason = request.getParameter(PARAM_NAME_REASON) != null
            ? request.getParameter(PARAM_NAME_REASON)
            : "";

    if (!StringUtils.isEmpty(reason)) {
        if (validReasons.containsKey(reason)) {
            reason = validReasons.get(reason);
        } else {
            // a reason param value not matching a key in the validReasons map is considered bogus
            log.warn("{} param value '{}' cannot be mapped to a valid reason message: ignoring", PARAM_NAME_REASON, reason);
            reason = "";
        }
    }

    String selectorString = slingRequest.getRequestPathInfo().getSelectorString();
    Boolean isLogin = selectorString == null || selectorString.indexOf("changepassword") == -1;

String cug = cfg.get("box/cug","anonymous");

%><!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="ie6 oldie coral-App"> <![endif]-->
<!--[if IE 7 ]> <html class="ie7 oldie coral-App"> <![endif]-->
<!--[if IE 8 ]> <html class="ie8 oldie coral-App"> <![endif]-->
<!--[if IE 9 ]> <html class="ie9 coral-App"> <![endif]-->
<!--[if !(lt IE 10)|!(IE)]><!--> <html class="coral-App"> <!--<![endif]-->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <%-- optimized for mobile, zoom/scaling disabled --%>
    <meta name="viewport" content="width = device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="chrome=1" />
    <title><%= printProperty(cfg, i18n, xssAPI, "title", i18n.get("Adobe Marketing Cloud")) %></title>
    <style type="text/css">
        <%
            HtmlLibraryManager htmlMgr = sling.getService(HtmlLibraryManager.class);
HtmlLibrary lib = htmlMgr.getLibrary(LibraryType.CSS, "/etc/clientlibs/granite/cug-login");
            IOUtils.copy(lib.getInputStream(true), out, "utf-8");
        %>
    </style>

    <ui:includeClientLib css="coralui2" />
    <%
        String favicon = xssAPI.getValidHref(cfg.get("favicon", "login/adobe-logo.png"));
        favicon = xssAPI.getValidHref(favicon);
    %>
    <link rel="shortcut icon" href="<%= favicon %>" type="image/png">
    <link rel="icon" href="<%= favicon %>" type="image/png">
    <%-- Load the clientlib(s). Extension libraries should use the  'granite.core.login.extension' category. --%>
    <ui:includeClientLib js="jquery,typekit,granite.core.login,granite.core.login.extension"/>
</head>
<body class="coral--light coral-App">
<div id="wrap">
    <div id="backgrounds">
        <%-- this holds all the background divs that are dynamically loaded --%>
        <div id="bg_default" class="background"></div>
    </div>
    <div id="tag"></div>

    <%
        // make sure the redirect path is valid and prefixed with the context path
        String redirect = request.getParameter("resource");
        if (redirect == null || !AuthUtil.isRedirectValid(request, redirect)) {
            redirect = "/";
        }
        if (!redirect.startsWith(contextPath)) {
            redirect = contextPath + redirect;
        }
        String urlLogin = request.getContextPath() + resource.getPath() + ".html/j_security_check";

        if (authType == null || user == null || user.equals("anonymous")) {

    %>
    <div id="login-box">
        <div class="header">
            <h1 class="coral-Heading coral-Heading--1"><%= printProperty(cfg, i18n, xssAPI, "box/title", i18n.get("Welcome to Adobe Marketing Cloud")) %></h1>
        </div>
        <div id="leftbox" class="box">
            <p>
                <%= printProperty(cfg, i18n, xssAPI, "box/text", i18n.get("All the tools you need to solve these complex digital business challenges.")) %>
                <a class="coral-Link" id="learnmore" href="<%= xssAPI.getValidHref(i18n.getVar(cfg.get("box/learnMore/href", "#"))) %>" x-cq-linkchecker="skip"><%= printProperty(cfg, i18n, xssAPI, "box/learnMore/text", i18n.get("Learn More")) %></a>
            </p>
        </div>
        <div id="rightbox" class="box">
            <% String autocomplete = cfg.get("box/autocomplete", false) ? "on" : "off" ; %>
            <form class="coral-Form coral-Form--vertical" name="login" method="POST" id="login" action="<%= xssAPI.getValidHref(urlLogin) %>" novalidate="novalidate">
                <input type="hidden" name="_charset_" value="UTF-8"/>
                <input type="hidden" name="errorMessage" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>"/>
                <input type="hidden" name="resource" id="resource" value="<%= xssAPI.encodeForHTMLAttr(redirect) %>"/>
                <%
                    String loginTitle = printProperty(cfg, i18n, xssAPI, "box/formTitle", i18n.get("Sign In"));
                    String changeTitle = printProperty(cfg, i18n, xssAPI, "box/changePasswordTitle", i18n.get("Change Password"));
                    String userPlaceholder = printProperty(cfg, i18n, xssAPI, "box/userPlaceholder", i18n.get("User name"));
                    String loginPasswordPlaceholder = printProperty(cfg, i18n, xssAPI, "box/passwordPlaceholder", i18n.get("Password"));
                    String changePasswordPlaceholder = printProperty(cfg, i18n, xssAPI, "box/oldPasswordPlaceholder", i18n.get("Old password"));
                    String newPasswordPlaceholder = printProperty(cfg, i18n, xssAPI, "box/newPasswordPlaceholder", i18n.get("New password"));
                    String confirmPasswordPlaceholder = printProperty(cfg, i18n, xssAPI, "box/confirmPasswordPlaceholder", i18n.get("Confirm new password"));
                    String loginSubmitText = printProperty(cfg, i18n, xssAPI, "box/submitText", i18n.get("Sign In"));
                    String changeSubmitText = printProperty(cfg, i18n, xssAPI, "box/changePasswordSubmitText", i18n.get("Submit"));
                %>
                <p class="sign-in-title"><%= isLogin ? loginTitle : changeTitle %></p>
                <input class="coral-Form-field coral-Textfield" id="username" name="j_username" type="hidden" autofocus="autofocus" pattern=".*" placeholder="<%= userPlaceholder %>" spellcheck="false" autocomplete="<%= autocomplete %>" value="<%= cug %>" disabled/>
                <label id="password-label" for="password"><span><%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %></span></label>
                <input class="coral-Form-field coral-Textfield" id="password" name="j_password" type="password"  placeholder="<%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %>" spellcheck="false" autocomplete="<%= autocomplete %>"/>
                <div id="change_fields" class="<%= isLogin ? "hidden" : "" %>">
                    <label for="new_password"><span><%= newPasswordPlaceholder %></span></label>
                    <input class="coral-Form-field coral-Textfield" id="new_password" name="<%= isLogin ? "" : "j_newpassword" %>" type="password"  placeholder="<%= newPasswordPlaceholder %>" spellcheck="false" autocomplete="false"/>
                    <label for="confirm_password"><span><%= confirmPasswordPlaceholder %></span></label>
                    <input class="coral-Form-field coral-Textfield" id="confirm_password" name="" type="password"  placeholder="<%= confirmPasswordPlaceholder %>" spellcheck="false" autocomplete="false"/>
                </div>
                <div id="error" class="coral-Form-field coral-Alert coral-Alert--error <%= reason.length() > 0 ? "" : "hidden" %>">
                    <i class="coral-Alert-typeIcon coral-Icon coral-Icon--sizeS coral-Icon--alert"></i>
                    <div class='coral-Alert-message' aria-live="assertive"><%= reason %></div>
                </div>
                <button id="submit-button" type="submit" class="coral-Button coral-Button--primary"><%= isLogin ? loginSubmitText : changeSubmitText %></button>
                <button id="back-button" class="coral-Button hidden"><%= printProperty(cfg, i18n, xssAPI, "box/backText", i18n.get("Back")) %></button>
            </form>
            <input id="login_title" type="hidden" value="<%= loginTitle %>">
            <input id="change_title" type="hidden" value="<%= changeTitle %>">
            <input id="login_password_placeholder" type="hidden" value="<%= loginPasswordPlaceholder %>">
            <input id="change_password_placeholder" type="hidden" value="<%= changePasswordPlaceholder %>">
            <input id="login_submit_text" type="hidden" value="<%= loginSubmitText %>">
            <input id="change_submit_text" type="hidden" value="<%= changeSubmitText %>">
            <input id="invalid_message" type="hidden" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>"/>
            <input id="expired_message" type="hidden" value="<%= printProperty(cfg, i18n, xssAPI, "box/loginExpiredText", i18n.get("Your password has expired")) %>"/>
            <input id="not_match_message" type="hidden" value="<%= printProperty(cfg, i18n, xssAPI, "box/passwordsDoNotMatchText", i18n.get("New passwords do not match")) %>"/>
            <input id="empty_message" type="hidden" value="<%= printProperty(cfg, i18n, xssAPI, "box/passwordEmptyText", i18n.get("New password must not be blank")) %>"/>
        </div>
    </div>
    <div id="push"></div>
</div>
<div id="footer">
    <div class="legal-footer"><%
        // Footer: default copyright (removable)
        if (cfg.containsKey("footer/copy/text")) {
            %><span><%= printProperty(cfg, i18n, xssAPI, "footer/copy/text", "") %></span><%
        }
        %><ul id="usage-box"><%

            // Footer: dynamic items (config/footer/items)
            if (configs.getChild("footer/items") != null) {
                Iterator<Resource> footerItems = configs.getChild("footer/items").listChildren();
                while (footerItems.hasNext()) {
                    %>
                    <li><%
                    String itemName = footerItems.next().getName();
                    String href = i18n.getVar(cfg.get("footer/items/" + itemName + "/href", String.class));
                    if (href != null) {
                        %><a href="<%= xssAPI.getValidHref(href) %>"><%
                    }
                    %><%= printProperty(cfg, i18n, xssAPI, "footer/items/" + itemName + "/text", "") %><%
                    if (href != null) {
                        %></a><%
                    }
                    %></li><%
                }
            }
        %>
        </ul>
    </div>
</div>


<%
String modalTitle = printProperty(cfg, i18n, xssAPI, "changePasswordSuccessTitle", i18n.get("Password Changed"));
%>
<div id="success_backdrop" class="coral-Modal-backdrop hidden"></div>
<div id="success_modal" class="coral-Modal coral-Modal--success">
    <div class="coral-Modal-header">
        <i class="coral-Modal-typeIcon coral-Icon coral-Icon--sizeS coral-Icon--checkCircle"></i>
        <h2 class="coral-Modal-title coral-Heading coral-Heading--2"><%= modalTitle %></h2>
        <button type="button" class="coral-MinimalButton coral-Modal-closeButton" title="<%= i18n.get("Close") %>">
            <i class="coral-Icon coral-Icon--sizeXS coral-Icon--close coral-MinimalButton-icon "></i>
        </button>
    </div>
    <div class="coral-Modal-body">
        <%
            // This text is read in the alert fallback for IE 8. In case of DOM changes please adjust in login.js
            // the selector in showSuccessModal().
        %>
        <p><%= printProperty(cfg, i18n, xssAPI, "changePasswordSuccessText", i18n.get("Your password has been changed successfully.")) %></p>
    </div>
    <div class="coral-Modal-footer">
        <button type="button" class="coral-Button coral-Button--primary" aria-label="<%= modalTitle %> - <%= i18n.get("Ok") %>"><%= i18n.get("Ok") %></button>
    </div>
</div>


<script type="text/javascript">
    // try to append the current hash/fragment to the redirect resource
    if (window.location.hash) {
        var resource = document.getElementById("resource");
        if (resource) {
            resource.value += window.location.hash;
        }
    }
</script>
<% } else { %>
<script type="text/javascript">
    var redirect = '<%= xssAPI.encodeForJSString(xssAPI.getValidHref(redirect)) %>';
    if (window.location.hash) {
        redirect += window.location.hash;
    }
    document.location = redirect;
</script>
<% } %>
<!-- QUICKSTART_HOMEPAGE - (string used for readyness detection, do not remove) -->
</body>
</html>
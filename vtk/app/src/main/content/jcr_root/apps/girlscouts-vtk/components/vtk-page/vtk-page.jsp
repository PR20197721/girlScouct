<%@page session="false" contentType="text/html; charset=utf-8" import="com.day.cq.commons.Doctype,
                                                                       com.day.cq.wcm.api.WCMMode,
                                                                       com.day.cq.wcm.foundation.ELEvaluator,
                                                                       org.girlscouts.vtk.auth.models.ApiConfig,
                                                                       org.girlscouts.vtk.osgi.component.ConfigManager" %>
<%@ page import="org.girlscouts.vtk.osgi.component.CouncilMapper, org.girlscouts.vtk.osgi.component.util.UserUtil" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects/>
<%
    boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
    boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;
%>
<html <%= wcmModeIsPreview ? "class=\"preview\"" : ""%>>
<%
    CouncilMapper mapper = sling.getService(CouncilMapper.class);
    try {
        HttpSession session = request.getSession();
        final ConfigManager configManager = sling.getService(ConfigManager.class);
        boolean isDemoSite = false;
        String _demoSite = configManager.getConfig("isDemoSite");
        if (_demoSite != null && _demoSite.equals("true")) {
            isDemoSite = true;
        }
        if (request.getParameter("useAsDemo") != null && !request.getParameter("useAsDemo").trim().equals("")) {
            session.setAttribute("useAsDemo", request.getParameter("useAsDemo"));
        } else {
            session.removeAttribute("useAsDemo");
        }
        String myUrl = request.getRequestURL().toString();
        if (myUrl.trim().contains("vtk.demo.index.html")) {
            org.girlscouts.vtk.auth.models.ApiConfig apiConfig = new org.girlscouts.vtk.auth.models.ApiConfig();
            session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), apiConfig);
        }

        // read the redirect target from the 'page properties' and perform the
        // redirect if WCM is disabled.
        String location = properties.get("cq:redirectTarget", "");
        // resolve variables in path
        location = ELEvaluator.evaluate(location, slingRequest, pageContext);
        if ((location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview))) {
            // check for recursion
            if (currentPage != null && !location.equals(currentPage.getPath()) && location.length() > 0) {
                // check for absolute path
                final int protocolIndex = location.indexOf(":/");
                final int queryIndex = location.indexOf('?');
                final String redirectPath;
                if (protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex)) {
                    redirectPath = location;
                } else {
                    redirectPath = slingRequest.getResourceResolver().map(request, location) + ".html";
                }
                response.sendRedirect(redirectPath);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }
        // set doctype
        if (currentDesign != null) {
            currentDesign.getDoctype(currentStyle).toRequest(request);
        }
        request.setAttribute("PAGE_CATEGORY", "VTK");
        final UserUtil userUtilHead = sling.getService(UserUtil.class);
        ApiConfig apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class.getName());
        if(apiConfig != null){
            String councilId = null;
            String referer = "";
            String branch = "";
            Page newCurrentPage = null;
            Design newCurrentDesign = null;
            try {
                if (!apiConfig.getUser().isAdmin()) {
                    councilId = apiConfig.getUser().getTroops().get(0).getCouncilCode();
                } else {
                    councilId = apiConfig.getUser().getAdminCouncilId();
                }
                branch = mapper.getCouncilBranch(councilId);
                referer = branch;
            } catch (Exception e) {
                Cookie[] cookies = request.getCookies();
                String refererCouncil = null;
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("vtk_referer_council")) {
                            refererCouncil = cookie.getValue();
                        }
                    }
                }
                if (refererCouncil != null && !refererCouncil.isEmpty() && refererCouncil.length() > 3) {
                    referer = "/content/" + refererCouncil;
                } else {
                    referer = mapper.getCouncilBranch();
                }
            }
            referer = referer + "/en/site-search.html";
            request.setAttribute("altSearchPath", referer);
            request.setAttribute("mapper", mapper);
            apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class.getName());
            request.setAttribute("apiconfig", apiConfig);
            // TODO: language
            branch += "/en";
            newCurrentPage = (Page) resourceResolver.resolve(branch).adaptTo(Page.class);
            if (newCurrentPage == null) {
                out.println("Missing council design on branch: " + branch);
                return;
            }
            // Get design
            String designPath = newCurrentPage.getProperties().get("cq:designPath", "");
            if (!designPath.isEmpty()) {
                newCurrentDesign = (Design) resourceResolver.resolve(designPath).adaptTo(Design.class);
            }
            // Override currentPage and currentDesign according to councilId
            if (newCurrentPage != null) {
                request.setAttribute("newCurrentPage", newCurrentPage);
            }
            if (newCurrentDesign != null) {
                request.setAttribute("newCurrentDesign", newCurrentDesign);
            }
        }
        %>
        <cq:include script="head.jsp"/>
        <cq:include script="body.jsp"/>
        <%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</html>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%

    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String eyebrowNavPath = headerPath + "/eyebrow-nav";
    String headerSearchPath = headerPath + "/search";
    String cookieHeaderPath = headerPath + "/cookie-header";

    %>
<!-- content -->
<div id="main" class="three-cols">
    <cq:include path="content/top/par" resourceType="girlscouts-common/components/styled-parsys" />

    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="right-wrapper">
        <% if (isCookiePage(currentPage)) { %>
                <cq:include path="<%= cookieHeaderPath %>" resourceType="gsusa/components/cookie-header" />
        <% } %>
        <div class="hero-section">
            <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
            <cq:include path="content/hero/par" resourceType="girlscouts-common/components/styled-parsys" />
        </div>
        <div class="middle-col">
            <cq:include script="main.jsp"/>
        </div>
        <div class="right-col">
            <cq:include script="right.jsp"/>
        </div>
        
<!-- brightedge three-col lem code begin -->         
<div class="be-ix-link-block" style="display: block;"></div>


<style type="text/css">

    .be-ix-link-block .be-related-link-container {
        margin: 0 auto;
        width: 100%;
        padding: 1rem 0;
        text-align: left;
        display: inline-flex;
        margin-left: -5px;
        margin-right: -5px;
    }

    .be-ix-link-block .be-related-link-container .be-related-link {
        display: inline-block;
        border: 1px #00ae58 solid !important;
        /*margin-left: 10px;*/
        padding: 1em;
        margin: 5px;
    }

    .be-ix-link-block .be-related-link-container .be-related-link:first-child {
      /*margin-left:0;*/
    }

    .be-ix-link-block .be-related-link-container .be-related-link .headline {
        font-weight: 600;
        line-height:  1.5rem;
        margin: 0;
        font-size: 20px;
        color:  #00ae58;
        margin-bottom: 5px;
    }

    .be-ix-link-block .be-related-link-container .be-related-link .desc {
        color:  #333;
        font-family: inherit;
        font-size: 1.0625rem;
        font-weight: normal;
        line-height: 1.5625rem;
    }

    a.be-related-link:hover {
        background: rgba(0, 174, 88, 0.1);
    }

    @media (max-width:768px) {

        .be-ix-link-block .be-related-link-container {
            display: inherit;
        }
        .be-ix-link-block .be-related-link-container .be-related-link .headline {
            font-size: 1.25rem;
        }

        .be-ix-link-block .be-related-link-container .be-related-link {
            margin: 10px !important;
            border: 1px #00ae58 solid !important;
            width: 95%;
        }
        .be-ix-link-block .be-related-link-container .be-related-link:first-child {
            margin: 10px !important;
        }
    }

</style>
<!-- brightedge lem code end -->        
        
    </div>
    <div class="wrapper clearfix"></div>
    <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
</div>
<!-- END of content -->

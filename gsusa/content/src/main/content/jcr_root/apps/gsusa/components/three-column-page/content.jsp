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
    <cq:include path="content/top/par" resourceType="girlscouts/components/styled-parsys" />

    <div class="cookie-landing-hero hide-for-small">
      <div class="welcome-video-slider">
      <div><img src="/etc/designs/gsusa/images/cookie-carousel-hp.png" alt="" /></div>
      <div><img src="/etc/designs/gsusa/images/cookie-carousel-hp2.png" alt="" /></div>
      <div><img src="/etc/designs/gsusa/images/cookie-carousel-h3.png" alt="" /></div>
      <div><img src="/etc/designs/gsusa/images/cookie-carousel-h4.png" alt="" /></div>
      </div>
      <div class="cookie-header">
        <div class="wrapper clearfix">
          <div class="wrapper-inner clearfix">
            <form class="find-cookies" name="find-cookies">
              <label for="zip-code">Nemo enim ipsam volu quia voluptas sit.</label>
              <div class="form-wrapper clearfix">
                <input type="text" placeholder="ZIP Code" class="zip-code" name="zip-code">
                <input type="submit" class="link-arrow" value="Go >"/>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="facebook-image">
        <a href="" title="follow on facebook"><img src="/etc/designs/gsusa/images/facebook-image.png" alt="facebook" /></a>
      </div>
    </div>


    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="right-wrapper">
        <% if (isCookiePage(currentPage)) { %>
                <cq:include path="<%= cookieHeaderPath %>" resourceType="gsusa/components/cookie-header" />
        <% } %>
        <div class="hero-section">
            <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
            <cq:include path="content/hero/par" resourceType="girlscouts/components/styled-parsys" />
        </div>
        <div class="middle-col">
            <cq:include script="main.jsp"/>
        </div>
        <div class="right-col">
            <cq:include script="right.jsp"/>
        </div>
    <cq:include script="cookie-footer.jsp"/>
    </div>
    <div class="wrapper clearfix"></div>
    <cq:include path="content/bottom/par" resourceType="girlscouts/components/styled-parsys" />
</div>
<!-- END of content -->

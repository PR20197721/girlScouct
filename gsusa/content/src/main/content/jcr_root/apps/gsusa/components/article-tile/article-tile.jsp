<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
%>
<ul class="toberemoved large-block-grid-3 clearfix">
<li>
    <section>
        <a href="http://nataliemac.com">
            <img src="/content/gsusa/en/about-girl-scouts/who-we-are/_jcr_content/content/hero/par/image_e03.img.jpg" alt="Article title" />
            <div class="text-content" style="background: rgba(36, 184, 238, 0.8)">
                <h3>Article Title</h3>
                <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
            </div>
        </a>
    </section>
</li>
<li>

    <section>
        <a href="http://nataliemac.com" data-reveal-id="videoModal">
            <img src="/content/gsusa/en/about-girl-scouts/who-we-are/_jcr_content/content/hero/par/image_e03.img.jpg" alt="Article title" />
            <div class="text-content" style="background: rgba(36, 184, 238, 0.8)">
                <h3>Article Title</h3>
                <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
            </div>
        </a>
    </section>
</li>
</ul>

<div id="videoModal" class="reveal-modal" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
  <img src="/content/gsusa/en/about-girl-scouts/who-we-are/_jcr_content/content/hero/par/image_e03.img.jpg" alt="Article title" />
  <a class="close-reveal-modal" aria-label="Close">&#215;</a>
</div>
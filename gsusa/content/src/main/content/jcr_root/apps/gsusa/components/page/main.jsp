<%@include file="/libs/foundation/global.jsp" %>
<!-- right -->
    <div id="mainContent">
        <cq:include path="content/middle/par" resourceType="girlscouts/components/styled-parsys" />
          <a href="#" data-reveal-id="shareModal" class="button">Share with your friends</a>
          <!-- Reveal Modals begin -->
          <div id="shareModal" class="reveal-modal share-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
            <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
            <div class="float-left">
              <img src="/etc/designs/gsusa/images/temp_popup-image.png" alt="" />
            </div>
            <div class="float-right">
                <h3>Only (n) days untill cookie season.</h3>
                <p>Share it with your friends.</p>
                <a href="#" class="button">Share on facebook <i class="icon-social-facebook"></i></a>
                <a href="#" class="button">Share on twitter <i class="icon-social-twitter-tweet-bird"></i></a>
            </div>
          </div>
    </div>
<!-- END of right -->

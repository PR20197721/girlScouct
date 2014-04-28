<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!--PAGE STRUCTURE: MAIN-->
                <div id="main" class="row">
                    <div class="large-24 medium-24 small-24 columns">
                        <div class="row">
                            <div id="heroBanner" class="large-24 medium-24 small-24 columns">
                              <cq:include path="par/hero-slideshow" resourceType="girlscouts/components/hero-slideshow"/> 
                            </div>
                        </div>
                        <cq:include path="content/styled-subpar" resourceType="girlscouts/components/styled-subparsys"/>
                    </div>
                </div>

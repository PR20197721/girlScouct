<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/include-options.jsp"%>
<!--PAGE STRUCTURE: MAIN-->
                <div id="main" class="row">
                    <div class="large-24 medium-24 small-24 columns">
                        <div class="row">
                            <div class="large-24 medium-24 small-24 columns">
                               <cq:include path="par" resourceType="foundation/components/parsys"/>
                            </div>
                        </div>
                        <div class="row">
                            <div id="heroBanner" class="large-24 medium-24 small-24 columns">
                                <img src="/content/dam/girlscouts-shared/en/banners/welcome.png" width="1998" height="874" class="hide-for-small"/>
                                <img src="/content/dam/girlscouts-shared/en/banners/welcome-small.png" width="1998" height="874" class="show-for-small"/>
                            </div>
                        </div>
                        
                        <div class="row">
                          
                          <cq:include path="feature-storystories" resourceType="girlscouts/components/parsys-feature-shortstories"/>
                         
                      
                           
                        </div>
                        <div>
                            <cq:include path="content/par" resourceType="foundation/components/parsys"/>
                        </div>    
                    </div>
                </div>

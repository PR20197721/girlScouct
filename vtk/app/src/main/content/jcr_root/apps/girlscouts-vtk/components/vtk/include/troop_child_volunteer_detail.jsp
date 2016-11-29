<dd class="accordion-navigation clearfix">
  <div id="panel<%=i+1%>b" class="content" style="padding:20px">
    <div class="screenboard-white row">

      <div class="row">
        <div class="column small-24 medium-8">
          Membership: *?Adult 2016<br>
          Role: *Friends and Family Volunteer
        </div>
        <div class="column  small-24 medium-8">
          Membership Years: Girl:*3 Adult:*12<br>
          Gender:*Female
        </div>
        <div class="column small-24 medium-8">
          <input type="checkbox"> *Email Opt In<br>
          <input type="checkbox"> *Text Opt In
        </div>
      </div>


      <div style="margin:15px 0"></div>

      <div class="row">
        <div class="column small-24"> Phone : *888-888-8888</div>
      </div>

      <div class="row">
        <div class="column small-24"> Email:mail *</div>
      </div>

      <div style="margin:15px 0"></div>
      <div class="row">
        <div class="column small-24"> Home Address: *</div>
      </div>


       <div class="row">
              <div style="float:right">
              
              <% 
               
               if(  apiConfig!=null && !apiConfig.isDemoUser() ){
                if( troop.getTroop().getRole().equals("PA")  ){
                 %>
                  <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Renewal" class="button">RENEW NOW</a>
                  <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Renewal" class="button">UPDATE CONTACT INFO</a>
                  <%         
                }else{
                  %>
                  <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Troop_Renewal" class="button">RENEW NOW</a>
                  <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Troop_Renewal" class="button">UPDATE CONTACT INFO</a>
                  <% 
                }
               }else{
                   %>
                   <a href="javascript:void(0)" class="button" disabled=true>*RENEW NOW</a>
                   <a href="javascript:void(0)" class="button" disabled=true>UPDATE CONTACT INFO*</a>
                  <% 
               }
              %>
              </div> 
        </div>                    

    </div>
</dd>



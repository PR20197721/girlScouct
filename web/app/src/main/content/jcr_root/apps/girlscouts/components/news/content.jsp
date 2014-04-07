<%@include file="/libs/foundation/global.jsp"%>

<div class="large-20 medium-19 small-24 columns mainRight">
    <div class="row">
       <div class="large-24 medium-24 hide-for-small columns rightBodyTop">
         <nav class="breadcrumbs">
             <a href="#"> This is #### Place Holder #####</a>
         </nav>
       </div>
     </div>  
       <div>
        <div class="large-19 medium-19 small-24 columns rightBodyLeft"/>
          <div id="mainContent">
            <h1><cq:include path="title" resourceType="foundation/components/title"/></h1>
            <%=properties.get("date","") %><br/>
            <cq:include path="image" resourceType="foundation/components/image" />
            <p><cq:include path="text" resourceType="foundation/components/text"/></p>
          </div>
       </div> 
    </div>
</div>



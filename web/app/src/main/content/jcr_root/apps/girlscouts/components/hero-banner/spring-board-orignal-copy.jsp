<%@include file="/libs/foundation/global.jsp"%>
<%
  // Get Spring Broad - One
     String firstsbtitle = properties.get("firstsbtitle", "");
     String firstsbdesc = properties.get("firstsbdesc", "");
     String firstsburl = properties.get("firstsburl","");
     String firstsbbutton = properties.get("firstsbbutton", "");
     
   // Get Spring Broad - Second
     String secondsbtitle = properties.get("secondsbtitle", "");
     String secondsbdesc = properties.get("secondsbdesc", "");
     String secondsburl = properties.get("secondsburl","");
     String secondsbbutton = properties.get("secondsbbutton", "");  
     
%>
<div class="hide-for-small-6 hide-for-medium-6 large-6 columns">
<div class="row content">
   <div class="spring-board-right">
    <div class="spring-broad" id="first">
      <div class="title" id="title1"> 
        <h2><span><a href="#"><%=firstsbtitle %></a></span></h2>
      </div>  
      <div class="slideup slidehidden">
        <h2><span><a href="#"><%=firstsbtitle%></a></span></h2>
         <div> 
           <p><%=firstsbdesc%>
           </p> 
           <input type="submit" value="<%=firstsbbutton%>" id="sub" onClick="parent.location='<%=firstsburl%>'" class="form-btn pull-center"/>
        </div> 
     </div>
    </div>
    <hr style="border-top: 1px dashed black; margin:0px">
    <div class="spring-broad" id="second">
      <div class="title" id="title2"> 
        <h2><span><a href="#"><%=secondsbtitle%></a></span></h2>
      </div>  
      <div class="slideup slidehidden">
        <h2><span><a href="#"><%=secondsbtitle%></a></span></h2>
         <div> 
           <p><%=secondsbdesc%></p>
          <input type="submit" value="<%=firstsbbutton %>" id="sub" onClick="parent.location='<%=firstsburl%>'" class="form-btn pull-center"/>
        </div> 
     </div>
    </div>
    
   
   </div> 
   
  </div> 
</div>


<style>
.pull-center{
  float:center;
}

.title{
margin: 0 auto;
padding: 60px 10px 10px 10px;
/*background-color: red;*/

}

span {
  display: inline-block;
  vertical-align: middle;
  line-height: normal;
  padding-bottom:12px;      
}

.spring-board-right {
    overflow:hidden;
    height:380px;
    text-align:center;
    border-color:grey;
    border-left:solid;
    border-right:solid;
    border-bottom:solid;
    border-width: 1px; /* this allows you to adjust the thickness */
    border-style: solid;
    margin:0 10px 10px 10px;
    
    /*background-color: yellow;*/
}

.spring-broad{
   height:188px;
   /*background-color: yellow;*/
  
}
.slideup{
  height:320px;
  padding:10px;
}
.slidehidden {
    position: middle;
    /*height:200px;*/
    /*background-color: yellow;*/
    z-index: -1;
    display:none;
}

</style>
<script>
$('.title').on('click',function(){
    $(this).next('.slideup').slideToggle();
    $(this).hide();
    parentId = $(this).parent().attr('id');
    titleId = $(this).parent().attr('id');
    if(parentId=='second'){
    	$('#first').height('60px');
        $('#first').find('.slideup').hide();
        $('#title1').css('padding','0px');
        $('#title1').show();
     }else{
    	 $('#first').height('320px');
    	 $('#second').find('.slideup').hide();
         $('#title2').css('padding','0px');
         $('#title2').show();
      }
    
   
});


$('.slideup').on('click',function(){
	$('#first').removeAttr('style');
	$('#title1').css('padding','60px 10px 10px 10px');
	$('#title2').css('padding','60px 10px 10px 10px');
	$(this).parent().find('.title').slideToggle();
    $(this).hide();
  
});


</script>


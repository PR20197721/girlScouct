<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%!
        String activeTab = "resource";
        boolean showVtkNav = true;

%>
<%@include file="include/vtk-nav.jsp"%>


<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
  <link rel="stylesheet" href="/resources/demos/style.css">
  <style>
  .ui-autocomplete-loading {
    background: white url('images/ui-anim_basic_16x16.gif') right center no-repeat;
  }
  </style>
  <script>
  $(function() {
    var cache = {};
    $( "#birds" ).autocomplete({
      minLength: 2,
      
      source: function( request, response ) {
        var term = request.term;
        console.log(term)
        if ( term in cache ) {
          var x = response( cache[ term ] );
          console.log("x: "+x);
          return;
        }
 
        $.getJSON( "/content/girlscouts-vtk/controllers/vtk.getdata.html?q="+term, request, 
        		function( data, status, xhr ) {
         		 	cache[ term ] = data;
         		 	var y =response( data );
          			console.log("y: "+data.label);
       			 })
       			 .done(function( json ) {
       				$("#caca").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
    				console.log( "JSON Data: " + json );
    				//$( "div.caca" ).html("searching...");
    				$(jQuery.parseJSON(JSON.stringify(json))).each(function() {  
    				    var ID = this.label;
    				   // var TITLE = this.Title;
    				    console.log( "--- "+ID );
    				    //$( "#caca" ).append("<li>"+ID+"</li>");
    				});
    				
    				
    				
  }				)			;
     	 }
    	});
  });
  
  
  
  </script>











<div class="ui-widget">
  <label for="birds">Birds: </label>
  <input id="birds">
</div>

<div id="caca">
Hellow
</div>


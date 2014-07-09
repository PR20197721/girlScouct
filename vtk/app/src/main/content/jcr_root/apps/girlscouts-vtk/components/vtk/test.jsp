
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>






<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask_custom_extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/alex.js"></script>

<script>
jQuery(function($){
	$("#date").inputmask("mm/dd/yyyy", {});
	$("#time").inputmask("h:s t", {});
	$("#money").maskMoney();
	});
	
	
	
	
	
	function testdate(){
		/*
		var dt= $.trim( document.getElementById('date') );
		var now = new Date();
		if ( document.getElementById('date') < now) {
		  alert("PAST DATE");
		}
		alert("GOOD DATE")
		*/
	}
	
	function testhr(){
		
	}
	
	
	function testmoney(){
		
		
	}
	
</script>

Date: <input type="text" id="date" value=""/> <a href="javascript:void(0)" onclick="testdate()">test date</a>
<br/>Time <input type="text" id="time" />     <a href="javascript:void(0)" onclick="testhr()">test date</a>
<br/>Curr <input type="text" id="money" />    <a href="javascript:void(0)" onclick="testmoney()">test date</a>







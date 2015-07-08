<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

	<script>
		var completeAndRedirectInvest = function(data){
			var toPost = $('.invest-zip').serialize();
			$.ajax({
				method: "POST",
				url: '/invest/ajax_CouncilFinder.asp',
				data: toPost,
				async: false,
				success: function(resp){
					if(resp == null || resp == ""){
						alert("The council you have searched for does not exist");
					}
					else{
						//console.log(resp);
						var url = resp.split(',',3);
						//console.log(url[2]);
						window.open(url[2],'_blank');
					}
				}
			});
		}
	</script>

	<h3>Support Your Local Girl Scout Council</h3>
	<ul class="block-grid">
		<li>
			<form class="invest-zip" onsubmit="completeAndRedirectInvest(); return false;" method="POST">
				<h6>Enter Zip Code: </h6>
				<section><input type="text" name="zipcode" required/></section>
				<input type="submit" value="Go" class="button tiny"/>
			</form>
		</li>	
	</ul>
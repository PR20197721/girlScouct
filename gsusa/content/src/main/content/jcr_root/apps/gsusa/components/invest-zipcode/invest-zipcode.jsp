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

	<div class="invest-button">
		<p>Support Your Local Girl Scout Council</p>
		<form class="invest-zip" onsubmit="completeAndRedirectInvest(); return false;" method="POST">
			<p>Enter Zip Code: </p>
			<section class="clearfix">
				<input type="text" name="zipcode" required />
				<button type="submit" class="fa fa-play-circle"></button>
			</section>
		</form>
	</div>
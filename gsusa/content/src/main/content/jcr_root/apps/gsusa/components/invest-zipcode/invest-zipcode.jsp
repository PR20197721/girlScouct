<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<style>
.invest-button{
	color: #ffffff;
	background-color: #fbb41f;
	border-radius:3px;
	padding-left:25px;
	padding-right:25px;
	padding-top:6px;
	padding-bottom:6px;
	margin:0px;
	width:247px;
}
</style>

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
			<section>
			<input type="image" width="31" height="31" align="right" src="/content/dam/girlscouts-gsusa/images/misc/button_orange.png" style="margin-top:5px;">
			<input type="text" name="zipcode" style="max-width:166px" required/>
			</section>
		</form>
	</div>
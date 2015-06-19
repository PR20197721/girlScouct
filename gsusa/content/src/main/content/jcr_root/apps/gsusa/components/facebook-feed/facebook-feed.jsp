<%@include file="/libs/foundation/global.jsp"%>

<script>
FB.api(
	"/GirlScoutsUSA?fields=posts.limit(1){id}&access_token=CAACEdEose0cBAPQaH5VEB7OCfCKUdMUnW69hpZByF99nRaQaptfx8G9DkG1HBrnOZAUEOZAscVso3T1z3LQcv3fcUfmc5lsdk7I3VkzYx7oA5IuJSZCOYZC35JhUlCbIlZBdxrFxiSzhdRLBCeo2HtcJlBKI8Sf6oCdrLYyttoeqFErn2rDpLNqNtL4PG4SBKOERDLqbBEiSDuJNGEwfoVY8EtTVHhA1wZD",
	function(response) {
		if(response && !response.error){
			console.log("ID: " + response);
			FB.api(
				"/" + response.id + "/attachments?fields=subattachments{url}&access_token=CAACEdEose0cBAPQaH5VEB7OCfCKUdMUnW69hpZByF99nRaQaptfx8G9DkG1HBrnOZAUEOZAscVso3T1z3LQcv3fcUfmc5lsdk7I3VkzYx7oA5IuJSZCOYZC35JhUlCbIlZBdxrFxiSzhdRLBCeo2HtcJlBKI8Sf6oCdrLYyttoeqFErn2rDpLNqNtL4PG4SBKOERDLqbBEiSDuJNGEwfoVY8EtTVHhA1wZD",
				function(response) {
					if(response && !response.error){
						console.log("PICTURES: " + response);
					}
				}
			);
		}
	}
);
</script>
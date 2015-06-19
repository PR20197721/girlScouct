<%@include file="/libs/foundation/global.jsp"%>

<script>
FB.api(
	"/GirlScoutsUSA?fields=posts.limit(1){id,object_id}&access_token=CAACEdEose0cBAPQaH5VEB7OCfCKUdMUnW69hpZByF99nRaQaptfx8G9DkG1HBrnOZAUEOZAscVso3T1z3LQcv3fcUfmc5lsdk7I3VkzYx7oA5IuJSZCOYZC35JhUlCbIlZBdxrFxiSzhdRLBCeo2HtcJlBKI8Sf6oCdrLYyttoeqFErn2rDpLNqNtL4PG4SBKOERDLqbBEiSDuJNGEwfoVY8EtTVHhA1wZD",
	function(response) {
		if(response && !response.error){
			console.log("ID: " + response.posts.data[0].object_id);
			FB.api(
				"/" + response.posts.data[0].object_id + "?fields=images&access_token=CAACEdEose0cBAPQaH5VEB7OCfCKUdMUnW69hpZByF99nRaQaptfx8G9DkG1HBrnOZAUEOZAscVso3T1z3LQcv3fcUfmc5lsdk7I3VkzYx7oA5IuJSZCOYZC35JhUlCbIlZBdxrFxiSzhdRLBCeo2HtcJlBKI8Sf6oCdrLYyttoeqFErn2rDpLNqNtL4PG4SBKOERDLqbBEiSDuJNGEwfoVY8EtTVHhA1wZD",
				function(response) {
					if(response && !response.error){
						console.log(response.images[0].source);
					}
				}
			);
		}
	}
);
</script>
<%@include file="/libs/foundation/global.jsp" %>

<%
	String source7 = (String) request.getAttribute("source7");
%>
	<div class="join">
		<div class="wrapper">
			<!-- <a id="tag_explore-join-now" href="#" title="join" tabindex="55">Join now</a> -->
			<section>
				<form class="bottom-overlay-join" id="findCouncilByZip">
					<span>FIND YOUR LOCAL COUNCIL</span>
					<input type="text" maxlength="5" pattern="[0-9]*" title="5 numbers zip code" name="ZipJoin" placeholder="Enter ZIP Code" />
					<input type="hidden" name="source" value="<%= source7 %>">
					<button id="tag_explore-join-go" class="button btn" type="submit" form="findCouncilByZip">GO</button>
				</form>
			</section>
		</div>
	</div>
	
	<script>
		$('#findCouncilByZip').on('focus', 'input', function() {
			
			var slick = $('.main-slider');
			if (slick != undefined && slick.slick != undefined) {
				slick.slick('slickPause');
				slick.slick('slickSetOption', 'autoplay', false, false);
				slick.slick('autoPlay',$.noop);
			}
		});
		
		$('#findCouncilByZip').on('focusout', 'input', function() {
			var slick = $('.main-slider');
			if (slick != undefined && slick.slick != undefined) {
				slick.slick('slickSetOption', 'autoplay', true, true);
				slick.slick('slickPlay');
				slick.slick('autoPlay',$.noop);
				
			}
		});
	</script>

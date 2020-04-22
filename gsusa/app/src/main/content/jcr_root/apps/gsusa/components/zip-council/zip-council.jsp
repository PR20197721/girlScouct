<%@include file="/libs/foundation/global.jsp" %>
<cq:includeClientLib categories="apps.gsusa.zip.council"/>
<%
	String source7 = (String) request.getAttribute("source7");
%>
	<div class="join">
		<div class="wrapper">
			<!-- <a id="tag_explore-join-now" href="#" title="join" tabindex="55">Join now</a> -->
			<section>
				<form class="bottom-overlay-join" id="findCouncilByZip">
					<div class="caption">
                        <span>FIND YOUR LOCAL COUNCIL</span>
                    </div>
                    <div class="fill"></div>
                    <div class="text">
                        <input type="tel" required pattern="[0-9]{5}" maxlength="5" title="5 Number Zip Code" name="ZipJoin" placeholder="Enter ZIP Code" />
                    </div>
				    <input type="hidden" name="source" value="<%= source7 %>">
                    <div class="submit">
                        <button id="tag_explore-join-go" class="button btn" type="submit" form="findCouncilByZip">GO</button>
                    </div>
                    <div class="fill"></div>
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

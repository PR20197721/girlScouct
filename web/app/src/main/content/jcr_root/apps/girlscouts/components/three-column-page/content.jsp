<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/include-options.jsp"%>

<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
	<!--PAGE STRUCTURE: LEFT CONTENT-->
	<div class="large-4 medium-5 hide-for-small columns mainLeft">
		<!--PAGE STRUCTURE: LEFT CONTENT-->
		<div id="leftContent">

			<!--COMPONENTS: SIDE NAV-->
			<ul class="side-nav" style="padding: 0px;">
				<li><a href="#">Our Mission</a></li>
				<li class="divider"></li>
				<li class="active"><a href="#">Our Programs</a>
					<ul>
						<li><a href="#">Grade Levels</a></li>
						<li class="active"><a href="#">Ways to Participate</a>
							<ul>
								<li><a href="#">Troops</a></li>
								<li><a href="#">Camp</a></li>
								<li><a href="#">Travel</a></li>
								<li><a href="#">Series and Events</a></li>
							</ul></li>
						<li><a href="#">Journeys</a></li>
						<li><a href="#">Badges</a></li>
						<li><a href="#">Highest Awards</a></li>
						<li><a href="#">Cookies</a></li>
					</ul></li>
				<li class="divider"></li>
				<li><a href="#">Our History</a></li>
				<li class="divider"></li>
				<li><a href="#">How to Join</a></li>
			</ul>
		</div>
	</div>
	<div class="large-20 medium-19 small-24 columns mainRight">
		<div class="row">
			<div class="large-24 medium-24 hide-for-small columns rightBodyTop">
				<cq:include path="content/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
		</div>
		<div>
			<div class="large-19 medium-19 small-24 columns rightBodyLeft">
				<!--PAGE STRUCTURE: MAIN CONTENT-->
				<div id="mainContent">
					<h1>Highest Awards</h1>
					<img src="/content/dam/girlscouts-shared/en/articles/guitar.png" width="1000" height="566" />
					<p>We know you want to do good things for the world. Help the
						people who need it most. Protect animals that can't speak for
						themselves. Treat the environment with the respect it deserves. We
						know you have great ideas, ones that make a lasting difference.
						And that you're more than ready to work hard to put those ideas
						into motion. Girl Scounting's highest awards&mdash;the Bronze,
						Silver, and Gold Awrads&mdash;are your chance to make a lasting
						difference in your community... and in the larger world. Click
						below. And start changing th world today!</p>
				</div>
			</div>
			<!--PAGE STRUCTURE: RIGHT CONTENT-->
			<div id="rightContent" class="large-5 medium-5 small-24 columns">
				<br />
				<br />
				<br />
				<br /> <img src="/content/dam/girlscouts-shared/en/ads/i-cant-wait.png" width="250" height="442" />
				<br />
				<br />
				<br />
				<br />
			</div>
		</div>
	</div>
</div>

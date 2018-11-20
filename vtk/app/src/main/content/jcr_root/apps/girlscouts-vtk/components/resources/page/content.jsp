
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>


<div id="modal_popup" class="reveal-modal" data-reveal=""></div>
<div id="myModal0" class="reveal-modal" data-reveal=""></div>
<div id="myModal1" class="reveal-modal" data-reveal=""></div>

<div class="vtk-resource">
	<div class="__first row">
		<div class="column small-22 medium-20 small-centered medium-centered">
			<h3>Resources</h3>
			<cq:include path="from-your-council" resourceType="girlscouts-vtk/components/resources/from-your-council" />
		</div>
	</div>
	<div class="__second">
		<% WCMMode prevMode = WCMMode.DISABLED.toRequest(request); %>
		<% request.setAttribute("VTK_RESOURCES_PREV_MODE", prevMode); %>
		<cq:include path="tab" resourceType="girlscouts-vtk/components/resources/tab" />
		<% request.removeAttribute("VTK_RESOURCES_PREV_MODE"); %>
		<% prevMode.toRequest(request); %>
	</div>
	<div class="__third column small-22 medium-20 small-centered medium-centered">
		<cq:include path="category-list" resourceType="girlscouts-vtk/components/resources/category-list" />
	</div>

	<div class="row" style="margin:40px 5px 0;">
		<div  class="column small-22 medium-20 small-centered medium-centered">
			<cq:include path="let-us-know" resourceType="girlscouts-vtk/components/resources/let-us-know" />
		</div>
	</div>
</div>

<script>
	$(function(){

		if(window.__currentLevel__ === undefined){
			window.__currentLevel__ = "daisy";
		}


		var classSelectedState = __currentLevel__;

		function f(selected){
			if(selected == 'multi'){
				selected+='-level';
			}

			$('.vtk-resource .__second .__menu .__complement li').removeClass('selected');
			$('.vtk-resource .__second .__menu .__complement li._'+selected).addClass('selected');
			$('.vtk-resource  .__second .__content .__block').hide();
			$('.vtk-resource  .__second .__content .__block.'+selected).show();
			classSelectedState = selected;
		}

		$('.vtk-resource .__first .__message_from .__title').on('click', function(event){
			$(this).find('.arrow').stop().toggleClass('arrow-close arrow-open');
			$('.vtk-resource .__first .__body').stop().slideToggle();

		});

		$('.vtk-resource .__second .__menu li').on('click', function(event){
			var selected = $(this).attr('class');
			if(classSelectedState !== selected){
				f(selected);
			}
		});

		$('.vtk-resource .__third .__box .__more').on('click', function(event){
			$(this).find('.arrow-small').stop().toggleClass('arrow-close arrow-open');

			if($(this).siblings('.__more_content:visible').length){
				$(this).find('.__text').text('more');
			}else{
				$(this).find('.__text').text('less');
			}

			$(this).siblings('ul.__more_content').stop().slideToggle();
		});

		$('.vtk-resource .__second .__content .__block.level .__more-level').click(function(){

			if($(this).siblings('.__more_detail_level:visible').length){
				$(this).find('.__text').html('more');

			}else{
				$(this).find('.__text').html('less');
			}
			$(this).find('.arrow-small').stop().toggleClass('arrow-close arrow-open');
			$(this).siblings('.__more_detail_level').stop().slideToggle();

		})

		$('.vtk-resource .__second .__content .__block.level').eq(0).show();

		f(classSelectedState);

		$(document).foundation();
	})
</script>
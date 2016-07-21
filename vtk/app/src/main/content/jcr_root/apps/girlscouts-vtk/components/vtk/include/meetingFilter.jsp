<div id="vtk-meeting-filter" class="content">

	<div class="sectionHeader" style="">
	    <div class="column small-22 small-centered" style="display:table; padding-left:0;">
	    <span class="vtk-green-box" style="">
	    	<span class="icon-search-magnifying-glass" style=""></span>
	    </span> 
	      <p id="showHideReveal" onclick="" class="hide-for-print close">FILTER MEETINGS BY TOPIC</p>
	    </div>
  	</div>

	<div class="main-filter column small-22 small-centered" style="display:none; padding-left:0;">
		 <div class="row">
		 	<div class="column small-24 medium-12">
		 		<div class="vtk-meeting-filter_title"><span>1.</span> Select your Girl Scout Level(s)</div>
		 		<div id="vtk-meeting-group-age" class="row">

					<div class="small-24 medium-12 large-8 column">
						<input id="Daisy" type="checkbox" name="age" value="Daisy"> <label for="Daisy"><span></span><p> Daisy</p></label>
					</div>
			
					<div class="small-24 medium-12 large-8 column">
						<input id="Brownie" type="checkbox" name="age" value="Brownie"> <label for="Brownie"><span></span><p>Brownie</p></label>
					</div>

					<div class="small-24 medium-12 large-8 column">
						<input id="junior" type="checkbox" name="age" value="junior"> <label for="junior"><span></span><p>Junior</p></label>
					</div>
	 			
		 		</div>
		 	</div>
		 	<div class="column small-24 medium-12">
		 		<div class="vtk-meeting-filter_title"><span>2.</span> Select the type of meeting plan you want</div>
		 		<div id="vtk-meeting-group-type" class="row">


		 			<div class="small-24 medium-12 large-8 column">
						<input id="one" type="radio" name="type" value="Daisy"> <label for="one"><span></span><p> Badget + Petals</p></label>
					</div>
			
					<div class="small-24 medium-12 large-8 column">
						<input id="two" type="radio" name="type" value="Brownie"> <label for="two"><span></span><p>Journey</p></label>
					</div>

					<div class="small-24 medium-12 large-8 column">
						<input id="three" type="radio" name="type" value="junior"> <label for="three"><span></span><p>Award Earning</p></label>
					</div>
		 			<!--  -->

					<div class="small-24 medium-12 large-8 column">
						<input id="four" type="radio" name="type" value="Daisy"> <label for="four"><span></span><p> Intro</p></label>
					</div>
			
					<div class="small-24 medium-12 large-8 column">
						<input id="five" type="radio" name="type" value="Brownie"> <label for="five"><span></span><p>Closing</p></label>
					</div>

					<div class="small-24 medium-12 large-8 column">
						<input id="six" type="radio" name="type" value="junior"> <label for="six"><span></span><p>Bridging</p></label>
					</div>	


		 		</div>
		 	</div>
		 </div>
	</div>

	<div class="list-of-categories column small-22 small-centered" style="display:none; padding-left:0;">
		 <div class="row">
		 	<div class="column small-24">
		 		<div class="vtk-meeting-filter_title"><span>3.</span> Select your badge categories</div>
		 		<div id="vtk-meeting-group-categories">

					<div class="small-24 medium-12 large-4 column">
						<input id="uno" type="checkbox" name="categorie" value="uno"> <label for="uno"><span></span><p>categorie one</p></label>
					</div>
			
					<div class="small-24 medium-12 large-4 column">
						<input id="dos" type="checkbox" name="categorie" value="dos"> <label for="dos"><span></span><p>categorie two</p></label>
					</div>

					<div class="small-24 medium-12 large-4 column">
						<input id="tres" type="checkbox" name="categorie" value="tres"> <label for="tres"><span></span><p>categorie three</p></label>
					</div>

					<div class="small-24 medium-12 large-4 column">
						<input id="cuatro" type="checkbox" name="categorie" value="cuatro"> <label for="cuatro"><span></span><p>categorie four</p></label>
					</div>
			
					<div class="small-24 medium-12 large-4 column">
						<input id="cinco" type="checkbox" name="categorie" value="cinco"> <label for="cinco"><span></span><p>categorie five</p></label>
					</div>

					<div class="small-24 medium-12 large-4 column">
						<input id="seis" type="checkbox" name="categorie" value="seis"> <label for="seis"><span></span><p>categorie six</p></label>
					</div>


		 			
		 		</div>
		 	</div>
		 </div>
	</div>


	<div class="list-of-buttons column small-22 small-centered" style="display:none; padding-left:0;">
		<div class="row">
			<div id="vtk-meeting-group-button" class="column small-24" style="padding:40px 0 30px 0;">
				<div id="vtk-meeting-group-button_cancel" class="button tiny ">CANCEL</div>
				<div id="vtk-meeting-group-button_ok" class="button tiny inactive-button">VIEW MEETING PLANS</div>
			</div>
		</div>
	</div>

</div>

<div id="vtk-meeting-report" class="content">
	<div class="main-report column small-22 small-centered" style="padding-left:0;">
		<div class="row">
			<div class="column small-24">
				<h6>Badge Meetings</h6>
			</div>
		</div>
	</div>
	
	<div class="main-report-search column small-22 small-centered" style="padding-left:0;">
		<div class="row">
			<div class="column small-24">
				<div class="column small-24 medium-8">
					{{#plans}} plans
				</div>
				<div class="column small-24 medium-8">

					<div id="vtk-dropdown-filter-1" class="vtk-dropdown-check-box" data-input-name="value1">
	                    <div class="vtk-dropdown_main">
		                    <div class="selected-option">View GS Level</div>
		                    <span class="icon-arrow-css" style="">
		                    	
		                    </span>
	                   </div>
	                    <ul class="vtk-dropdown_options">
	                      
	                      <li> <input type="checkbox" id="Daisy-"> <label for="Daisy-"><span></span> <p>DAISY</p></label></li>
	                      <li> <input type="checkbox" id="Brownie-"> <label for="Brownie-"><span></span> <p>BROWNIE</p></label></li>
	                      <li> <input type="checkbox" id="Junior"> <label for="Junior"><span></span> <p>JUNIOR</p></label></li>
	                    </ul>
	              </div>
					
				</div>
				<div class="column small-24 medium-8">

					<div id="vtk-dropdown-filter-2" class="vtk-dropdown-check-box" data-input-name="value1">
	                    <div class="vtk-dropdown_main">
		                    <div class="selected-option">View category</div>
		                    <span class="icon-arrow-css" style="">
		                    	
		                    </span>
	                   </div>
	                    <ul class="vtk-dropdown_options">
	                      
	                      <li> <input type="checkbox" id="cat-1"> <label for="cat-1"><span></span> <p>categorie 1</p></label></li>
	                      <li> <input type="checkbox" id="cat-2"> <label for="cat-2"><span></span> <p>categorie 2</p></label></li>
	                      <li> <input type="checkbox" id="cat-3"> <label for="cat-3"><span></span> <p>categorie 3</p></label></li>
	                    </ul>
	              	</div>
								
					
				</div>
			</div>
		</div>
	</div>

</div>






<script type="text/javascript">

// top-level namespace being assigned an object literal
    var gsusa = gsusa || {};

    // a convenience function for parsing string namespaces and
    // automatically generating nested namespaces
    function extendNS( ns, ns_string ) {
        var parts = ns_string.split('.'),
            parent = ns,
            pl, i;

        if (parts[0] == "gsusa") {
            parts = parts.slice(1);
        }

        pl = parts.length;
        for (i = 0; i < pl; i++) {
            //create a property if it doesnt exist
            if (typeof parent[parts[i]] == 'undefined') {
                parent[parts[i]] = {};
            }

            parent = parent[parts[i]];
        }

        return parent;
    }
    // Add the name space;
    extendNS(gsusa,'gsusa.component');

    gsusa.component = (function(){
        // (Css Selector)
        function dropDownCheckBox(selector,callbackObject){

            //get main element
            var $element = $(selector);

            //get target input form
            var inputName = $element.data('input-name');
            
            //get Default text
            var default_text = $element.children('.vtk-dropdown_main').children('.selected-option').html();

            //Toggle the Options box
            function toggle(){
                $element.children('.vtk-dropdown_options').toggle();
            }

            //add event listinert to the Icon
            $element.children('.vtk-dropdown_main').children('.icon-arrow-css').click(toggle);

            $element.children('.vtk-dropdown_options').find('input[type="checkbox"]').on('change', function(e){
            	setTimeout(toggle,300);
          		console.log(e);
          	})


           }

          
        return {
            'dropDownCheckBox': dropDownCheckBox
        };
    })();

    //sample
    gsusa.component.dropDownCheckBox('#vtk-dropdown-filter-1');
     gsusa.component.dropDownCheckBox('#vtk-dropdown-filter-2');
  

$(function(){


	var age = $('#vtk-meeting-group-age');
	var type = $('#vtk-meeting-group-type');
	var categories = $('#vtk-meeting-group-categories');

	var button ={
		ok: $('#vtk-meeting-group-button_ok'),
		cancel: $('#vtk-meeting-group-button_cancel')
	}

	function doThis(e){
		console.log('-->',e);
	}

	function onChangeDo(e){
		var ageLength = age.find('input[type="checkbox"]:checked').length;
		var typeLength = type.find('input[type="radio"]:checked').length;
		var categoriesLength = categories.find('input[type="checkbox"]:checked').length;

		if( ageLength > 0 &&  typeLength > 0){
			$('.list-of-categories').slideDown();
			$('.list-of-buttons').slideDown();

			if(categoriesLength > 0){
				button.ok.removeClass('inactive-button');
			}else{
				button.ok.addClass('inactive-button');
			}
		}
	}




		$('#vtk-meeting-filter').find('#showHideReveal').stop().click(function(e){


			$(this).toggleClass('open')
			console.log(e)
			$('.main-filter').slideToggle();





		})
		age.find('input[type="checkbox"]').on('change', onChangeDo);
		type.find('input[type="radio"]').on('change', onChangeDo);
		categories.find('input[type="checkbox"]').on('change', onChangeDo);
		button.ok.on('click',function(e){
			console.log('-', e, $(this));
			if(!$(this).hasClass('inactive-button')){
				doThis(e);
			}
		});

		button.cancel.on('click',function(e){

			console.log('clean',e)
			$('#vtk-meeting-filter').find('input')
			 .not(':button, :submit, :reset, :hidden')
			 .val('')
			 .removeAttr('checked')
			 .removeAttr('selected');
		});
});
	                                                    
</script>
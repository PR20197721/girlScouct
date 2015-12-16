$(document).ready(function(){
	// Setup "contact local council" form
	$('.booth-finder form#contactlocalcouncil').submit(function(){
		$.post($(this).attr('action'), $(this).serialize(), function(response) {
			// Remove blank lines
			response = response.replace(/^\s*\n/gm, '').trim();
			if (response.toUpperCase() == 'OK') {
				$('#contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
			} else {
				$('#contactlocalcouncil div.error').html(response);
			}
		});

		// Prevent default
		return false;
	});
});
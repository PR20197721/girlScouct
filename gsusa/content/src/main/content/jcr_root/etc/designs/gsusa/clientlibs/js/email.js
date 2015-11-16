$('.email-signin').submit(function (event) {
  var me = event.target;
	var emailAddress = $(me).find("[name='email']").val();
	var corphomeValue = $(me).find("[name='CORPHOME']").val();
	$.post('/includes/ajax_sendinfo_home.asp',{
		email: emailAddress,
		CORPHOME: corphomeValue
	}, function(txt){
		//submission page contains either welcome or thank you
		if(txt.search(/OK/i) == -1) {
			alert('Sorry, we are having a difficulty processing your request.\nPlease try again later.');
		} else {
			// show thank you
			$(me).find("[name='email']").val('');
			$('.email-signin').hide();
			$('.email-output-message').css('display','block').html("<p>Thank you! We look forward to staying connected</p>");
		}
	});
	return false;
});

$('#emailSignupNav').submit(function (event){
	var me = event.target;
	var emailAddress = $(me).find("[name='email']").val();
	var zipcode = $(me).find("[name='zipcode']").val();
	var alumna = ($(me).find("[name='alumna']").is(':checked') ? "Yes" : "No");
	var error = $('.error.hide');
	$.get('/program/gs_cookies/newsletteradd.asp?email=' + emailAddress + '&alumna=' + alumna + '&zipcode=' + zipcode, function(result){
		if(result.search(/OK/i) == -1) {
			error.show();
		} else {
			//Display thank you notice
			$('.get-updates section').hide();
			$('.success').removeClass('hide');
		}
	}).fail(function() { error.show() });
	return false;
});
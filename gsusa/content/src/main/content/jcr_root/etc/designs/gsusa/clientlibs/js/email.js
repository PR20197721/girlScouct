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
			alert('works! Let's show thank you message now!');
		}
	});

	return false;

});
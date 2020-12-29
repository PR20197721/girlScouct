var horizontalBorder = $("#content-divider-config").data("enableHorizontalBorder");
$(document).ready(function() {
	if(!horizontalBorder){
		$("#content-divider-config").hide();
	}
});


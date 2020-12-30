var horizontalBorder = $(".content-divider-config").data("enableHorizontalBorder");
var borderLineStyle = $(".content-divider-config").data("borderLineStyle");
$(document).ready(function() {
	if(!horizontalBorder){
		$("#content-divider-config").hide();
	}
});


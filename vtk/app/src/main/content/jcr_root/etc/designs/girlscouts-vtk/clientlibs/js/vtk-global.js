function showError(msg, msgId) {
	var targetNode = "#error_msg";
	if (msgId) {
		targetNode = msgId;
	}
	if (msg) {
		$(targetNode).html(msg);
		$(targetNode).show();
	} else {
		// clear
		$(targetNode).html("");
		$(targetNode).hide();
	}
}


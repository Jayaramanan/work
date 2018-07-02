
document.addEventListener('DOMContentLoaded', function () {
	if (Notification.permission !== "granted")
		Notification.requestPermission();
});

var buildIcon = function(type) {
	return window.location.origin + '/VAADIN/themes/srtheme/layouts/images/24x24/pharmacy.png'
}

function notify(msg,title,url,timeout,practiceId) {
	if (!Notification) {
		// Silently fail
		// alert('Desktop notifications not available in your browser.');
		return;
	}

	if (Notification.permission !== "granted")
		Notification.requestPermission(function(status) {
			if (Notification.permission !== status) {
				Notification.permission = status;
			}
			if (status === "granted") {
				notify(msg,title,url,timeout);
			}
		});
	else {

		if (timeout==null) { timeout = 5000; }

		var notification = new Notification(title, { icon: buildIcon(), body: msg });
		notification.onclick = function () {
			if (url!=null) {
				window.focus();
				window.open(url,"_self");
				com.vetsource.scriptright.analytics.notificationHandler(practiceId)
			}
		};
		setTimeout(function() { notification.close() }, timeout);
	}

}

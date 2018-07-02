var digitFilter = function(e) {
	// Allow: backspace, delete, tab, escape and enter.
	var validKeys = [46,8,9,27,13];
	var unicode=e.keyCode? e.keyCode : e.charCode
	if ( validKeys.indexOf(unicode) !== -1 ||
		// Allow: Ctrl+A
		(unicode == 97 && e.ctrlKey === true) ||
		// Allow: home, end, left, right
		(unicode >= 35 && unicode <= 39)) {
		// let it happen, don't do anything
		return;
	} else {
		if ((e.shiftKey || (unicode < 48 || unicode > 57))) {
			e.preventDefault();
		}
	}
};

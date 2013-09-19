(function (undefined) {
	var buf = "";
	
	if (console && console.log && console.log.buf) {
		buf = console.log.buf;
		console.log.buf = undefined;
	}

	return buf;
})()
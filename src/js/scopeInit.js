this.toString = function () {
	return "this is you";
};

var console = {
	log: function (message) {
		console.log.buf = console.log.buf || "";  
		console.log.buf += String(message) + "\n";
	}
};
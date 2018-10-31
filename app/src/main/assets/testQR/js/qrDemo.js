function init() {}

qr.hdScan = function (msg) {
	boso.innerHTML += msg;
	boso.innerHTML += "<br/><br/>";
};

dat = {
	back: function () {
		window.history.back();
	}
};

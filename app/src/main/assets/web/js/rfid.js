rfid = {
	// 读
	tid: 0,
	scan: function () {
		if (!this.tid) {
			this.tid = setInterval(rfid.doScan, 100);
		}
	},
	stop: function () {
		if (this.tid) {
			clearInterval(this.tid);
			this.tid = 0;
			rfid.doScan();
		}
	},
	doScan: function () {
		var s = rfdo.rfidCatchScanning();
		var o = JSON.parse(s);
		rfid.hdScan(o);
	},
	hdScan: function (obj) {
		// console.log(obj);
	},
	scanStart: function () {
		rfdo.rfidScan();
	},
	scanStop: function () {
		rfdo.rfidStop();
	},
	setBank: function (b) {
		rfdo.setBank(b);
	},

	// 写
	wrt: function (bankNam, dat, tid) {
		rfdo.rfidWrt(bankNam, dat, tid);
	},
	hdWrt: function (ok) {
		// console.log(ok);
	}
};

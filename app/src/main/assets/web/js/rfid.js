rfid = {
	// 读
	tid: 0,
	timf: 100,	// 刷新间隔
	scan: function () {
		if (!this.tid) {
			this.tid = setInterval(rfid.doScan, rfid.timf);
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
	},

	// 其它
	log: function (msg) {
		rfdo.log(msg);
	},

	/**************  业务相关  *************/

	// 获取配置信息
	getConfig: function () {
		return rfdo.getConfig();
	},

	// 保存配置信息
	savConfig: function (tL, tH, to, tb) {
		return rfdo.savConfig(tL, tH, to, tb);
	},

	// 是否可启动
	isRunAble: function () {
		return rfdo.isRunAble();
	},

	// 警告指示灯
	alarmLed: function (b) {
		rfdo.alarmLed(b);
	},

	// 电源指示灯
	powerLed: function (d) {
		rfdo.powerLed(d);
	},

	// 切换天线
	cAnt: function () {
		rfdo.cAnt();
	}
};

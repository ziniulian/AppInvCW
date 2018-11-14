function init() {
	rfid.powerLed (1);
	dat.config = JSON.parse (rfid.getConfig());
	dat.init();
}

dat = {
	tp: "E281CA002000000000",		// TID前缀
	config: {
		tempL: 40,
		tempH: 70,
		timout: 30000,
		tb: {}
	},
	d: null,

	memo: {
		tid: 0,
		doe: null,
		timout: 3000,

		show: function (msg) {
			if (this.tid) {
				clearTimeout(this.tid);
			}
			this.doe.innerHTML = msg;
			this.tid = setTimeout(this.hid_s, this.timout);
		},

		bind: function (d) {
			var self = this;
			this.doe = d;
			this.hid_s = function () {
				self.hid ();
			};
		},

		hid: function () {
			this.doe.innerHTML = "";
			this.tid = 0;
		}
	},

	// 页面初始化
	init: function () {
		var d, s, n;
		d = document.getElementById("tempL");
		d.value = dat.config.tempL;
		d = document.getElementById("tempH");
		d.value = dat.config.tempH;
		d = document.getElementById("timout");
		d.value = dat.config.timout;
		for (s in dat.config.tb) {
			n = dat.config.tb[s].nam;
			d = document.getElementById(n);
			d.value = s.substring(dat.tp.length);
			dat.config[n] = d.value;
		}

		d = document.getElementById("memoDeo");
		dat.memo.bind (d);

		// 按钮
		d = document.getElementById("savDeo");
		d.ontouchstart = function (e) {
			this.className = "midSub mfs btn btnscd";
		};
		d.ontouchend = function (e) {
			this.className = "midSub mfs btn";
			if (dat.d) {
				dat.d.blur();
			} else {
				dat.sav();
			}
		};
		d = document.getElementById("bckDeo");
		d.ontouchstart = function (e) {
			this.className = "midSub mfs btn btnscd";
		};
		d.ontouchend = function (e) {
			this.className = "midSub mfs btn";
			location.href = "home.html";
		};
	},

	// 信息提示
	log: function (msg) {
		dat.memo.show (msg);
	},

	// 设置值
	set: function (doe, v, err) {
		if (err) {
			doe.value = dat.config[doe.id];
			doe.select();
			dat.log (err);
		} else {
			dat.config[doe.id] = v;
			doe.value = v;
			doe.blur();
			dat.d = null;
		}
// console.log (dat.config);
	},

	// 设置温度
	setTemp: function (doe) {
		var v, err;
		if (doe.value) {
			v = doe.value * 100;
			if (v > 0) {
				v = Math.floor(v) / 100;
				if (doe.id === "tempL") {
					if (v >= dat.config.tempH) {
						err = "安全温度必须小于警告温度";
					}
				} else if (v <= dat.config.tempL) {
					err = "警告温度必须大于安全温度";
				}
			} else {
				err = "温度必须大于零！";
			}
		} else {
			err = "温度不能为空！";
		}
		dat.set (doe, v, err);
	},

	// 设置超时
	setTim: function (doe) {
		var v, err;
		if (doe.value) {
			v = Math.floor(doe.value - 0);
			if (v < 300) {
				err = "超时必须大于 300 毫秒！";
			} else if (v > 120000) {
				err = "超时不能大于 120000 毫秒！";
			}
		} else {
			err = "超时不能为空！";
		}

		dat.set (doe, v, err);
	},

	// 设置TID
	setTid: function (doe) {
		var v, p, err;
		v = doe.value.toUpperCase();
		p = dat.config[doe.id];
		if (p === undefined) {
			dat.config[doe.id] = "";
			p = "";
		}

		if (v) {
			if (v.length === 6) {
				if (v.match(/^[0-9A-F]{6}$/)) {
					if (dat.config.tb[dat.tp + v]) {
						err = "TID重复！";
					} else {
						dat.config.tb[dat.tp + v] = {
							nam: doe.id
						};
					}
				} else {
					err = "TID存在非法字符！";
				}
			} else {
				err = "TID字符长度错误！";
			}
		} else {
			v = "";
		}
		if (!err && p) {
			delete dat.config.tb[dat.tp + p];
		}

		dat.set (doe, v, err);
	},

	// 保存
	sav: function () {
		if (rfid.savConfig (
			dat.config.tempL,
			dat.config.tempH,
			dat.config.timout,
			JSON.stringify (dat.config.tb)
		)) {
			location.href = "home.html";
		}
	},

	scd: function (doe) {
		dat.d = doe;
		doe.select();		// 选中内容
		outter.scrollTop = doe.parentNode.offsetTop;	// 位置调整
	}
};

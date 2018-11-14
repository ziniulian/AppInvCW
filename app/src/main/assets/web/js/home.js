function init() {
	// 获取配置参数
	dat.config = JSON.parse(rfid.getConfig());
	rfid.timf = dat.config.timf;
	dat.ts = dat.config.tb;

	window.onbeforeunload = dat.stop;

	dat.initData();
	dat.run();
}

rfid.hdScan = function (arr) {
	var o;
	for (var i = 0; i < arr.length; i ++) {
		o = dat.ts[arr[i].tid];
		if (o) {		// 找到了对应标签
			dat.flush(o,  arr[i].tmp);
		}
	}

	dat.checkAlarm();

	if (rfid.tid === 0) {
		if (dat.tid === -1) {
			rfid.cAnt();
			rfid.powerLed (1);
			dat.tid = setTimeout(dat.run, dat.config.timp);
		} else {
// rfid.log("overed!");
			rfid.powerLed (0);
		}
	} else {
		o = Date.now() - dat.tim;
// rfid.log(dat.n + " , " + o + "/" + dat.config.timout);
		if ((dat.n === 0) || (o < 0) || (o > dat.config.timout)) {
			rfid.scanStop();
		}
	}
};

dat = {
	config: undefined,
	ts: undefined,		// 标签数据
	tid: -1,
	tim: 0,
	n: 0,	// 预计读取的标签个数

	// 初始化标签数据信息
	initData: function () {
		var o, s, r, d;
		/*
			属性说明：
			{
				"nam": "",		// 位置名，对应dom元素的id号
				"tid": "",		// 键名
				"tmp": 0.0,		// 温度
				"alarmDoe": <tr>,	// 警告容器
				"atimDoe": <td>,	// 警告时间容器
				"atmpDoe": <td>,	// 警告温度容器
				"ctmpDoe": <>,	// 温度颜色容器
				"isRead": false,	// 读取状态
			}
		*/
		for (s in dat.ts) {
// rfid.log("|" + s + "|");
			o = dat.ts[s];
			o.tid = s;
			o.tmp = 0;
			o.isRead = false;
			o.ctmpDoe = document.getElementById(o.nam);
			o.ctmpDoe.className = "";

			r = document.createElement("tr");
			d = document.createElement("td");
			d.className = "tbwtim";
			r.appendChild(d);
			o.atimDoe = d;
			d = document.createElement("td");
			d.className = "tbwnam";
			d.innerHTML = o.nam;
			r.appendChild(d);
			d = document.createElement("td");
			d.className = "tbwtmp";
			r.appendChild(d);
			o.atmpDoe = d;
			o.alarmDoe = r;
		}
	},

	// 开始
	run: function () {
		if (rfid.isRunAble()) {
			dat.tid = -1;
			dat.tim = Date.now();
			dat.n = 0;

			// 将所有标签读取状态改为未读取 ， 并统计标签个数
			for (var s in dat.ts) {
				dat.n ++;
				dat.ts[s].isRead = false;
			}

// rfid.log(" runing ... ");
			rfid.scanStart();
		} else {
// rfid.log(" waite ");
		}
	},

	// 刷新标签
	flush: function (o, t) {
		// 清除警告
		if (o.tmp >= dat.config.tempH) {
			alarmDoe.removeChild(o.alarmDoe);
		}

		// 更新温度
		o.tmp = t;
// rfid.log(o.nam + " : " + o.tmp);
		// 更新对应标签的颜色
		if (o.tmp < dat.config.tempL) {
			o.ctmpDoe.className = "rg";
		} else if (o.tmp < dat.config.tempH) {
			o.ctmpDoe.className = "ry";
		} else {
			o.ctmpDoe.className = "rr";

			// 添加警告
			o.atmpDoe.innerHTML = o.tmp;
			o.atimDoe.innerHTML = dat.getTimeStr();
			alarmDoe.insertBefore(o.alarmDoe, alarmDoe.childNodes[0]);
		}

		// 设定读取状态
		if (!o.isRead) {
			o.isRead = true;
			dat.n --;
		}
	},

	// 获取时间
	getTimeStr: function () {
		var t = new Date();
		var r = t.getFullYear();
		r += "-";
		r += dat.format((t.getMonth() + 1) + "", 2, "0");
		r += "-";
		r += dat.format(t.getDate() + "", 2, "0");
		r += " ";
		r += dat.format(t.getHours() + "", 2, "0");
		r += ":";
		r += dat.format(t.getMinutes() + "", 2, "0");
		r += ":";
		r += dat.format(t.getSeconds() + "", 2, "0");
		return r;
	},

	// 文字格式化
	format: function (s, width, subs) {
		var r = s;
		var n = s.length;
		if (width > n) {
			n = width - n;
			for (var i = 0; i < n; i++) {
				r = subs + r;
			}
		}
		return r;
	},

	// 警告监控
	checkAlarm: function () {
		if (alarmDoe.childNodes.length > 0) {
			rfid.alarmLed (true);
		} else {
			rfid.alarmLed (false);
		}
		rfid.powerLed (2);	// 电源灯闪烁
	},

	// 结束
	stop: function () {
// rfid.log("over ...");
		if (dat.tid > 0) {
			clearTimeout(dat.tid);
		} else {
			dat.tid = 0;
			rfid.scanStop();
		}
	}
};

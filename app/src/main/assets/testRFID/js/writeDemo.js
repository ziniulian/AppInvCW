function init() {
	var req = dat.getUrlReq();
	if (req.tid) {
		tid.innerHTML = req.tid;
	}
	if (req.epc) {
		epc.innerHTML = req.epc;
	}
	if (req.use) {
		use.innerHTML = req.use;
	}
}

rfid.hdWrt = function (ok) {
	if (ok) {
		note.innerHTML = "写入成功";
		dat.dom.innerHTML = txt.value;
		txt.value = "";
	} else {
		note.innerHTML = "写入失败";
	}
	txt.disabled = false;
};

dat = {
	getUrlReq: function () {
		var url = location.search;
		var theRequest = {};
		if (url.indexOf("?") != -1) {
			url = url.substr(1).split("&");
			for(var i = 0; i < url.length; i ++) {
				var str = url[i].split("=");
				theRequest[str[0]] = decodeURIComponent(str[1]);
			}
		}
		return theRequest;
	},

	set: function (d) {
		if (!txt.disabled) {
			if (txt.value) {
				if (txt.value === d.innerHTML) {
					note.innerHTML = "不能写入相同数据";
				} else {
					txt.disabled = true;
					dat.dom = d;
					note.innerHTML = "正在写入 ...";
					rfid.wrt(d.id, txt.value, tid.innerHTML);
				}
			} else {
				note.innerHTML = "不能写入空值";
			}
		}
	},

	setTxt: function (d) {
		if (!txt.disabled) {
			if (d) {
				txt.value = d.innerHTML;
			} else {
				txt.value = "";
				note.innerHTML = "";
			}
		}
	},

	back: function () {
		window.history.back();
	}

};

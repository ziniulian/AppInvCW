function init() {
	rfid.setBank("tmp");
}

rfid.hdScan = function (arr) {
	var o;
	for (var i = 0; i < arr.length; i ++) {
		o = dat.ts[arr[i].tid];
		if (o) {
			o.tmp = arr[i].tmp;
			o.tim += arr[i].tim;
			o.tmpDom.innerHTML = o.tmp;
			o.timDom.innerHTML = o.tim;
		} else {
			dat.crtTag(arr[i]);
		}
	}
};

dat = {
	// 标签集合
	ts: {},

	// 生成标签
	crtTag: function (o) {
		var r = {
			dom: document.createElement("div"),
			tidDom: document.createElement("div"),
			tmpDom: document.createElement("div"),
			timDom: document.createElement("div"),
			delDom: document.createElement("div"),
			tmp: o.tmp,
			tim: o.tim
		};

		var d = document.createElement("div");
		d.className = "out";
		r.tidDom.className = "txt mfs";
		r.tmpDom.className = "tmp sfs";
		r.timDom.className = "tim sfs";
		r.delDom.className = "op mfs";
		d.appendChild(r.tidDom);
		d.appendChild(r.tmpDom);
		d.appendChild(r.timDom);
		r.dom.appendChild(d);
		r.dom.appendChild(r.delDom);

		r.tidDom.innerHTML = o.tid;
		r.tmpDom.innerHTML = r.tmp;
		r.timDom.innerHTML = r.tim;
		r.delDom.innerHTML = "<a href=\"javascript:dat.delTag('" + o.tid + "');\">删除</a>";
		tago.appendChild(r.dom);
		dat.ts[o.tid] = r;
	},

	// 删除标签
	delTag: function (key) {
		tago.removeChild(dat.ts[key].dom);
		delete (dat.ts[key]);
	},

	// 清空集合
	clear: function () {
		tago.innerHTML = "";
		dat.ts = {};
	},

	back: function () {
		window.history.back();
	}

};

package com.invengo.rfd6c.cwthd.enums;

/**
 * 页面信息
 * Created by 李泽荣 on 2018/7/17.
 */

public enum EmUrl {
	// RFID 测试
	RfScaning("javascript: rfid.scan();"),
	RfStoped("javascript: rfid.stop();"),
	RfWrtOk("javascript: rfid.hdWrt(true);"),
	RfWrtErr("javascript: rfid.hdWrt(false);"),
	RfOver("javascript: dat.stop();"),
	RfRun("javascript: dat.run();"),

	// 主页
	Home("file:///android_asset/web/home.html"),
	Setting("file:///android_asset/web/setting.html"),
	Back("javascript: dat.back();"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}

	@Override
	public String toString() {
		return url;
	}
}

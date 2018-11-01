package com.invengo.rfd6c.cwthd.entity;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.invengo.rfd6c.cwthd.Ma;
import com.invengo.rfd6c.cwthd.enums.EmUh;
import com.invengo.rfd6c.cwthd.enums.EmUrl;

import tk.ziniulian.job.rfid.EmCb;
import tk.ziniulian.job.rfid.EmPushMod;
import tk.ziniulian.job.rfid.InfTagListener;
import tk.ziniulian.job.rfid.tag.T6C;
import tk.ziniulian.job.rfid.xc2910.Rd;
import tk.ziniulian.util.dao.DbLocal;

/**
 * 业务接口
 * Created by 李泽荣 on 2018/7/17.
 */

public class Web {
	private Rd rfd = new Rd();
	private Gson gson = new Gson();
	private DbLocal ldao = null;
	private Ma ma;

	// 配置信息
	private Double tempL = 40.0;	// 温度下限 40 （包含）
	private Double tempH = 70.0;	// 温度上限 70 （不包含）
	private int timout = 60000;		// 读取超时（毫秒）
	private int timp = 30000;		// 扫描间隔（毫秒）
	private int timf = 1000;		// 刷新间隔（毫秒）
	private String tb = "{}";		// 编号表

	public Web (Ma m) {
		this.ma = m;
		ldao = new DbLocal(m);
	}

	// 读写器设置
	public void initRd () {
		rfd.setBank("tmp");
		rfd.setHex(true);
		rfd.setPm(EmPushMod.Catch);
		rfd.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(T6C bt, InfTagListener itl) {}

			@Override
			public void onWrtTag(T6C bt, InfTagListener itl) {
				ma.sendUrl(EmUrl.RfWrtOk);
			}

			@Override
			public void cb(EmCb e, String[] args) {
				// Log.i("--rfd--", e.name());
				switch (e) {
					case Scanning:
						ma.sendUrl(EmUrl.RfScaning);
						break;
					case Stopped:
						ma.sendUrl(EmUrl.RfStoped);
						break;
					case ErrWrt:
						ma.sendUrl(EmUrl.RfWrtErr);
						break;
					case ErrConnect:
						ma.sendUrl(EmUrl.Err);
						break;
					case Connected:
						ma.sendUh(EmUh.Connected);
						break;
				}
			}
		});
		rfd.init();
	}

	// 读取Double值
	private Double dbReadK (String k, Double defv) {
		String s = ldao.kvGet(k);
		if (s == null) {
			ldao.kvSet(k, defv + "");
		} else {
			defv = Double.parseDouble(s);
		}
		return defv;
	}

	// 读取String值
	private String dbReadK (String k, String defv) {
		String s = ldao.kvGet(k);
		if (s == null) {
			ldao.kvSet(k, defv);
		} else {
			defv = s;
		}
		return defv;
	}

	// 读取int值
	private int dbReadK (String k, int defv) {
		String s = ldao.kvGet(k);
		if (s == null) {
			ldao.kvSet(k, defv + "");
		} else {
			defv = Integer.parseInt(s);
		}
		return defv;
	}

	// 初始化数据库
	public void initDb () {
		timout = dbReadK("timout", timout);		// 读取超时
		timp = dbReadK("timp", timp);		// 扫描间隔
		timf = dbReadK("timf", timf);		// 刷新间隔
		tempL = dbReadK("tempL", tempL);	// 温度下限
		tempH = dbReadK("tempH", tempH);	// 温度上限
		tb = dbReadK("tb", tb);		// 编号表
	}

	public void open() {
		rfd.open();
	}

	public void close() {
		rfd.close();
	}

/*------------------- RFID ---------------------*/

	@JavascriptInterface
	public boolean isRfidBusy () {
		return rfd.isBusy();
	}

	@JavascriptInterface
	public void rfidScan() {
		rfd.scan();
	}

	@JavascriptInterface
	public void rfidStop() {
		rfd.stop();
	}

	@JavascriptInterface
	public void rfidWrt (String bankNam, String dat, String tid) {
		rfd.wrt(bankNam, dat, tid);
	}

	@JavascriptInterface
	public String rfidCatchScanning() {
		return rfd.catchScanning();
	}

	@JavascriptInterface
	public boolean setBank(String bankNam) {
		return rfd.setBank(bankNam);
	}

/*------------------- 数据库 ---------------------*/

	@JavascriptInterface
	public String kvGet(String k) {
		return ldao.kvGet(k);
	}

	@JavascriptInterface
	public void kvSet(String k, String v) {
		ldao.kvSet(k, v);
	}

	@JavascriptInterface
	public void kvDel(String k) {
		ldao.kvDel(k);
	}

/*------------------- 其它 ---------------------*/

	@JavascriptInterface
	public void log(String msg) {
		Log.i("---- Web ----", msg);
	}

	@JavascriptInterface
	public String getConfig() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		sb.append("\"timf\":");
		sb.append(timf);
		sb.append(",");

		sb.append("\"timp\":");
		sb.append(timp);
		sb.append(",");

		sb.append("\"timout\":");
		sb.append(timout);
		sb.append(",");

		sb.append("\"tempL\":");
		sb.append(tempL);
		sb.append(",");

		sb.append("\"tempH\":");
		sb.append(tempH);
		sb.append(",");

		sb.append("\"tb\":");
		sb.append(tb);

		sb.append("}");
		return sb.toString();
	}

}

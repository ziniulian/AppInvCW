package com.invengo.rfd6c.cwthd.entity;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.invengo.rfd6c.cwthd.Ma;
import com.invengo.rfd6c.cwthd.enums.EmUh;
import com.invengo.rfd6c.cwthd.enums.EmUrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
//	private Gson gson = new Gson();
	private DbLocal ldao = null;
	private Ma ma;

	// 配置信息
	private Double tempL = 40.0;	// 温度下限 40 （包含）	安全温度
	private Double tempH = 70.0;	// 温度上限 70 （不包含）	警告温度
	private int timout = 30000;		// 读取超时（毫秒）
	private int timp = 100;			// 扫描间隔（毫秒）
	private int timf = 1000;		// 刷新间隔（毫秒）
	private String tb = "{}";		// 编号表

	// 指示灯
	private boolean aledt = false;	// 警告指示灯状态
	private int pledt = 0;		// 电源指示灯状态		0,关; 1,开;
	private int antt = 42;		// 天线状态

	private boolean runAble = false;	// 可用js启动的标记

	public Web (Ma m) {
		this.ma = m;
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
		ldao = new DbLocal(ma);
		timout = dbReadK("timout", timout);		// 读取超时
		timp = dbReadK("timp", timp);		// 扫描间隔
		timf = dbReadK("timf", timf);		// 刷新间隔
		tempL = dbReadK("tempL", tempL);	// 温度下限
		tempH = dbReadK("tempH", tempH);	// 温度上限
		tb = dbReadK("tb", tb);		// 编号表
	}

	// 关闭数据库
	public void closeDb() {
		ldao.close();
	}

	// 平板设备串口控制
	private boolean XC290xGPIOControl(String str) {
		boolean result = false;
		/*
		* 91 ： 串口上电（21）
		* 90 ： 串口断电（21）
		* 51 ： 红灯亮（120）
		* 50 ： 红灯灭（120）
		* 61 ： 绿灯亮（127）
		* 60 ： 绿灯灭（127）
		* 71 ： 天线1开（42）
		* 70 ： 天线1关（42）
		* 81 ： 天线2开（43）
		* 80 ： 天线2关（43）
		* */
		File file = new File("/proc/c620_ledctrl");
		FileWriter fr = null;
		try {
			fr = new FileWriter(file);
			fr.write(str);
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (IOException e) {
				result = false;
			}
		}
		return result;
	}

	// 启动
	public void open() {
		if (XC290xGPIOControl("91")) {
			if (XC290xGPIOControl("50")) {
				if (XC290xGPIOControl("80")) {
					if (XC290xGPIOControl("71")) {
						if (XC290xGPIOControl("61")) {
							pledt = 1;
							antt = 42;
							rfd.open();
						}
					}
				}
			}
		}
	}

	// 停止
	public void close() {
		runAble = false;
		rfd.close();
		pledt = 2;
		XC290xGPIOControl("50");
		XC290xGPIOControl("70");
		XC290xGPIOControl("80");
		XC290xGPIOControl("90");
		XC290xGPIOControl("60");
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

	public Web setRunAble(boolean runAble) {
		this.runAble = runAble;
		return this;
	}

	@JavascriptInterface
	public boolean isRunAble() {
		return runAble;
	}

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

	// 警告指示灯
	@JavascriptInterface
	public void alarmLed(boolean show) {
		if (!aledt && show) {
			aledt = XC290xGPIOControl("51");
		} else if (aledt && !show) {
			XC290xGPIOControl("50");
			aledt = false;
		}
	}

	// 电源指示灯
	@JavascriptInterface
	public void powerLed(int d) {
		if (pledt != 2) {
			if (d == 1) {
				pledt = 0;
			} else if (d == 0) {
				pledt = 1;
			}
			if (pledt == 0) {
				if (XC290xGPIOControl("61")) {
					pledt = 1;
				}
			} else if (pledt == 1) {
				XC290xGPIOControl("60");
				pledt = 0;
			}
		}
	}

	// 切换天线
	@JavascriptInterface
	public void cAnt() {
		if (antt == 42) {
			if (XC290xGPIOControl("70")) {
				if (XC290xGPIOControl("81")) {
					antt = 43;
				}
			}
		} else if (antt == 43) {
			if (XC290xGPIOControl("80")) {
				if (XC290xGPIOControl("71")) {
					antt = 42;
				}
			}
		}
	}

	// 切换天线
	@JavascriptInterface
	public boolean savConfig (double tL, double tH, int to, String tb) {
		tempL = tL;
		tempH = tH;
		timout = to;
		this.tb = tb;

		ldao.kvSet("tempL", tL + "");
		ldao.kvSet("tempH", tH + "");
		ldao.kvSet("timout", to + "");
		ldao.kvSet("tb", tb);

		return true;
	}

}

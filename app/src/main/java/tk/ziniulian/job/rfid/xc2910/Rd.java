package tk.ziniulian.job.rfid.xc2910;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.protocol.IRP1.IntegrateReaderManager;
import invengo.javaapi.protocol.IRP1.PowerOff;
import invengo.javaapi.protocol.IRP1.RXD_TagData;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;
import invengo.javaapi.protocol.IRP1.SysConfig_800;
import invengo.javaapi.protocol.IRP1.WriteEpc;
import invengo.javaapi.protocol.IRP1.WriteUserData_6C;
import tk.ziniulian.job.rfid.Base;
import tk.ziniulian.job.rfid.EmCb;
import tk.ziniulian.job.rfid.EmPushMod;
import tk.ziniulian.job.rfid.tag.T6C;
import tk.ziniulian.job.rfid.tag.T6Ctemperature;
import tk.ziniulian.util.Str;

/**
 * XC2910型标签读写器
 * Created by LZR on 2017/8/9.
 */

public class Rd extends Base implements IMessageNotificationReceivedHandle {
	private Reader rd = null;
	private boolean isConnect = false;
	private boolean isScanning = false;
	private boolean isReading = false;
	private final byte antenna = 1;
	private ReadTag.ReadMemoryBank bank = ReadTag.ReadMemoryBank.EPC_TID_UserData_6C;
	private final byte[] defaulPwd = new byte[] {0, 0, 0, 0};
	private byte[] pwd = null;
	private Class tagc = T6C.class;

	// 连接设备
	private Runnable connectRa = new Runnable() {
		@Override
		public void run() {
			isConnect = rd.connect();
			cb(EmCb.HidProgress);
			if (isConnect) {
				cb(EmCb.Connected);
			} else {
				cb(EmCb.ErrConnect);
			}
		}
	};

	// 断开设备
	private Runnable disConnectRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			if (isScanning) {
				isScanning = false;
				cb(EmCb.Stopped);
			}
			rd.disConnect();
			isConnect = false;
			isReading = false;
			cb(EmCb.DisConnected);
		}
	};

	// 扫描
	private Runnable scanRa = new Runnable() {
		@Override
		public void run() {
			ReadTag rt = new ReadTag (bank);
			rd.send(rt);
			cb(EmCb.Scanning);
		}
	};

	// 停止
	private Runnable stopRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			isScanning = false;
			cb(EmCb.Stopped);
		}
	};

	// 读标签
	private Runnable readRa = new Runnable() {
		@Override
		public void run() {
			ReadTag msg = new ReadTag (bank, true);
			if (rd.send(msg)) {
				T6C bt = crtBt(msg.getReceivedMessage().getList_RXD_TagData()[0]);
				isReading = false;
				onReadTag(bt);
			} else {
				isReading = false;
				cb(EmCb.ErrRead);
			}
		}	};

	// 写标签
	private class WrtRa implements Runnable {
		private T6C bt = null;
		private IMessage msg;
		@Override
		public void run() {
			if (rd.send(msg)) {
				isReading = false;
				onWrtTag(bt);
			} else {
				isReading = false;
				cb(EmCb.ErrWrt);
			}
		}
	}

	// 功率
	private RateRunable rateRa = new RateRunable();
	private class RateRunable implements Runnable {
		private byte pm = 0x65;		// 功率的配置参数
		private byte len = 0x02;	// 字长
		private byte ant = 0x00;	// 天线端口号
		private String rat = "30";

		public RateRunable setRat(String rat) {
			this.rat = rat;
			return this;
		}

		public String getRat() {
			return rat;
		}

		@Override
		public void run() {
			isScanning = false;
			rd.send(new PowerOff());
			byte[] d = {len, ant, Byte.parseByte(rat)};
			rd.send(new SysConfig_800(pm, d));
			cb(EmCb.RateChg);
		}
	}

	// 创建基本标签
	private T6C crtBt (RXD_TagData r) {
		RXD_TagData.ReceivedInfo ri = r.getReceivedMessage();
		String tid = Str.Bytes2Hexstr(ri.getTID());
		T6C bt = ts.get(tid);
		if (bt == null) {
			try {
				bt = (T6C)tagc.newInstance();
				bt.setByRXD_TagData_ReceivedInfo(ri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			bt.addOne();
		}
		return bt;
	}

	@Override
	public void init() {
		if (rd == null) {
			rd = IntegrateReaderManager.getInstance();
			isConnect = false;
			isScanning = false;
			if (rd == null) {
				cb(EmCb.ErrConnect);
			} else {
				rd.onMessageNotificationReceived.add(this);
			}
		}
	}

	@Override
	public void open() {
		if (!isConnect && rd != null) {
			cb(EmCb.ShowProgress);
			new Thread(connectRa).start();
		}
	}

	@Override
	public void close() {
		if (isConnect) {
			new Thread(disConnectRa).start();
		}
	}

	@Override
	public void read(String bankNam) {
		if (!isScanning && isConnect && !isReading) {
			isReading = true;
			if (bankNam == null || bankNam.equals("")) {
				new Thread(readRa).start();
			} else if (setBank(bankNam)) {
				new Thread(readRa).start();
			} else {
				isReading = false;
			}
		}
	}

	@Override
	public void wrt(String bankNam, String dat, String tid) {
		if (!isScanning && isConnect && !isReading) {
			WrtRa r = new WrtRa();
			try {
				r.bt = (T6C)tagc.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (r.bt == null) {
				return;
			}

			isReading = true;
			boolean assign = (tid != null && (tid.length() > 0));
			if (assign) {
				r.bt.setTid(tid);
			}
			switch (bankNam) {
				case "epc":
					if (isHex()) {
						r.bt.setEpc(dat);
					} else {
						r.bt.setEpcDat(dat);
					}
					if (assign) {
						r.msg = new WriteEpc(antenna, getPwd(), r.bt.getEpc(), r.bt.getTid(), MemoryBank.TIDMemory);
					} else {
						r.msg = new WriteEpc(antenna, getPwd(), r.bt.getEpc());
					}
					break;
				case "use":
					if (isHex()) {
						r.bt.setUse(dat);
					} else {
						r.bt.setUseDat(dat);
					}
					if (assign) {
						r.msg = new WriteUserData_6C(antenna, getPwd(), (byte)0, r.bt.getUse(), r.bt.getTid(), MemoryBank.TIDMemory);
					} else {
						r.msg = new WriteUserData_6C(antenna, getPwd(), 0, r.bt.getUse());
					}
					break;
				default:
					isReading = false;
					return;
			}
			new Thread(r).start();
		}
	}

	@Override
	public void scan() {
		if (!isScanning && isConnect && !isReading) {
			isScanning = true;
			if (getPm() != EmPushMod.Event) {
				clearScanning();
			}
			new Thread(scanRa).start();
		}
	}

	@Override
	public void stop() {
		if (isScanning && isConnect) {
			new Thread(stopRa).start();
		}
	}

	public void rate(String r) {
		rateRa.setRat(r);
		new Thread(rateRa).start();
	}

	@Override
	public void messageNotificationReceivedHandle(BaseReader baseReader, IMessageNotification iMessageNotification) {
		if (iMessageNotification instanceof RXD_TagData) {
//Log.i("---", Str.Bytes2Hexstr(iMessageNotification.getReceivedData()));
			onReadTag(crtBt((RXD_TagData)iMessageNotification));
		}
	}

	// 设置bank
	public boolean setBank (String bankNam) {
		switch (bankNam) {
			case "epc":
				bank = ReadTag.ReadMemoryBank.EPC_6C;
				break;
			case "tid":
				bank = ReadTag.ReadMemoryBank.TID_6C;
				break;
			case "use":
			case "all":
				bank = ReadTag.ReadMemoryBank.EPC_TID_UserData_6C;
				break;
			case "bck":
				bank = ReadTag.ReadMemoryBank.EPC_TID_UserData_Reserved_6C_ID_UserData_6B;
				break;
			case "tmp":
				bank = ReadTag.ReadMemoryBank.EPC_TID_TEMPERATURE;
				break;
			default:
				return false;
		}
		if (bankNam.equals("tmp")) {
			tagc = T6Ctemperature.class;
		} else {
			tagc = T6C.class;
		}
		return true;
	}

	// 获取bank
	public String getBank () {
		switch (bank) {
			case EPC_6C:
				return "epc";
			case TID_6C:
				return "tid";
			case EPC_TID_UserData_6C:
				return "use";
			case EPC_TID_UserData_Reserved_6C_ID_UserData_6B:
				return "bck";
			case EPC_TID_TEMPERATURE:
				return "tmp";
			default:
				return "";
		}
	}

	@Override
	public boolean isBusy() {
		return isScanning || isReading;
	}

	private byte[] getPwd() {
		if (pwd == null) {
			return defaulPwd;
		} else {
			return pwd;
		}
	}

	public Rd setPwd(byte[] pwd) {
		this.pwd = pwd;
		return this;
	}

	// TODO: 2018/7/18 修改标签密码

}

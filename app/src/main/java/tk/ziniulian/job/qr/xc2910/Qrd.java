package tk.ziniulian.job.qr.xc2910;

import com.invengo.lib.dev.ScanManager;
import com.invengo.lib.dev.Scanner;
import com.invengo.lib.dev.barcode.type.BarcodeType;
import com.invengo.lib.dev.barcode.type.EventType;
import com.invengo.lib.dev.event.BarcodeEventListener;

import tk.ziniulian.job.qr.BasQr;
import tk.ziniulian.job.qr.EmQrCb;

/**
 * XC2910型二维码读取器
 * Created by 李泽荣 on 2018/7/18.
 */

public class Qrd extends BasQr {
	private Scanner sr = null;
	private boolean isConnected = false;
	private boolean isScanning = false;

	@Override
	public void init() {
		if (sr == null) {
			sr = ScanManager.getInstance();
			if (sr == null) {
				cb(EmQrCb.ErrConnect);
			} else {
				isConnected = true;
				sr.setEventListener(bel);
				cb(EmQrCb.Connected);
			}
		}
	}

	// 连接设备
	private Runnable connectRa = new Runnable() {
		@Override
		public void run() {
			cb(EmQrCb.HidProgress);
			if (sr.connect()) {
				isConnected = true;
				sr.setEventListener(bel);
				cb(EmQrCb.Connected);
			} else {
				cb(EmQrCb.ErrConnect);
			}
		}
	};

	// 断开设备
	private DisConnectRa disConnectRa = new DisConnectRa ();
	private class DisConnectRa implements Runnable {
		boolean drop = false;
		private void setDrop (boolean s) {
			this.drop = s;
		}

		@Override
		public void run() {
			sr.stopDecode();
			isScanning = false;
			cb(EmQrCb.Stopped);
			if (drop) {
				sr.disconnect();
				isConnected = false;
				cb(EmQrCb.DisConnected);
			}
		}
	}

	// 扫描
	private Runnable scanRa = new Runnable() {
		@Override
		public void run() {
			if (sr.startDecode()) {
				isScanning = true;
				cb(EmQrCb.Scanning);
			} else {
				cb(EmQrCb.ErrScann);
			}
		}
	};

	private BarcodeEventListener bel = new BarcodeEventListener() {
		@Override
		public void onStateChanged(EventType eventType) {
//			Log.i("--qrsc--", eventType.name());
		}

		@Override
		public void onDecodeEvent(BarcodeType barcodeType, String s) {
			if (barcodeType != BarcodeType.NoRead) {
				isScanning = false;
				onRead(s);
			}
		}
	};

	public void destroy () {
		ScanManager.onDestroy();
	}

	@Override
	public void open() {
		if (!isConnected && sr != null) {
			isScanning = false;
			cb(EmQrCb.ShowProgress);
			new Thread(connectRa).start();
		}
	}

	@Override
	public void close() {
		if (isConnected) {
			disConnectRa.setDrop(true);
			new Thread(disConnectRa).start();
		}
	}

	@Override
	public void scan() {
		if (isConnected && !isScanning) {
			new Thread(scanRa).start();
		}
	}

	@Override
	public void stop() {
		if (isScanning) {
			disConnectRa.setDrop(false);
			new Thread(disConnectRa).start();
		}
	}

	@Override
	public boolean isBusy() {
		return isScanning;
	}

}

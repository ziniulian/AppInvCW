package tk.ziniulian.job.qr;

/**
 * 二维码基类
 * Created by 李泽荣 on 2018/7/18.
 */

public abstract class BasQr implements InfBasQr {
	private InfQrListener il = null;

	@Override
	public void setQrListenter(InfQrListener l) {
		this.il = l;
	}

	// 回调
	protected void cb (EmQrCb e, String... args) {
		if (il != null) {
			il.cb(e, args);
		}
	}

	// 读到标签时的触发事件
	protected void onRead (String msg) {
		il.onRead(msg);
	}

}

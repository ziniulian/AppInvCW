package tk.ziniulian.job.qr;

/**
 * 事件
 * Created by 李泽荣 on 2018/7/18.
 */

public interface InfQrListener {
	public void onRead (String msg);
	public void cb (EmQrCb e, String[] args);
}

package tk.ziniulian.job.qr;

/**
 * 二维码基础接口
 * Created by 李泽荣 on 2018/7/18.
 */

public interface InfBasQr {
	// 初始化
	public void init();

	// 打开
	public void open();

	// 关闭
	public void close();

	// 设置监听
	public void setQrListenter (InfQrListener l);

	// 扫描
	public void scan();

	// 停止
	public void stop();

	// 是否工作
	public boolean isBusy();
}

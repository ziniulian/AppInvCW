package tk.ziniulian.job.rfid;

import android.webkit.JavascriptInterface;

/**
 * RFID基础接口
 * Created by LZR on 2017/8/8.
 */

public interface InfBaseRfid {
	// 初始化
	public void init();

	// 打开
	public void open();

	// 关闭
	public void close();

	// 设置监听
	public void setTagListenter (InfTagListener l);

	// 是否工作
	public boolean isBusy();

	// 获取扫描结果
	@JavascriptInterface
	public String catchScanning();

	// 读
	@JavascriptInterface
	public void read(String bankNam);

	// 写
	@JavascriptInterface
	public void wrt(String bankNam, String dat, String tid);

	// 扫描
	@JavascriptInterface
	public void scan();

	// 停止
	@JavascriptInterface
	public void stop();
}

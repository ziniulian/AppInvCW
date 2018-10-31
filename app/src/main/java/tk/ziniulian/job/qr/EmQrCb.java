package tk.ziniulian.job.qr;

/**
 * 回调信息
 * Created by 李泽荣 on 2018/7/18.
 */

public enum EmQrCb {
	ShowProgress,	// 显示进度条
	HidProgress,	// 隐藏进度条
	ShowToast,		// 信息提示
	Scanning,		// 开始扫描
	Stopped,		// 扫描已停止
	Connected,		// 与RFID设备已建立连接
	DisConnected,	// 与RFID设备已断开连接
	ErrConnect,		// 与RFID设备连接失败
	ErrScann		// 扫描失败
}

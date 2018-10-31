package tk.ziniulian.job.rfid;

import tk.ziniulian.job.rfid.tag.T6C;

/**
 * 事件
 * Created by LZR on 2017/8/7.
 */

public interface InfTagListener {
	public void onReadTag (T6C bt, InfTagListener itl);
	public void onWrtTag (T6C bt, InfTagListener itl);
	public void cb (EmCb e, String[] args);
}

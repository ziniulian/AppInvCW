package tk.ziniulian.job.rfid;

import java.util.HashMap;
import java.util.Map;

import tk.ziniulian.job.rfid.tag.T6C;

/**
 * 读写器基类
 * Created by LZR on 2017/8/8.
 */

public abstract class Base implements InfBaseRfid {
	protected Map<String, T6C> ts = new HashMap<>();	// 标签集
	private InfTagListener itl = null;
	private boolean hex = false;	// 使用二进制数据
	private EmPushMod pm = EmPushMod.Event;

	@Override
	public void setTagListenter(InfTagListener l) {
		this.itl = l;
	}

	@Override
	public String catchScanning() {
		StringBuilder sb = new StringBuilder();
		int n = ts.size();
		if (n > 0) {
			sb.append('[');
			for (T6C t : ts.values()) {
				sb.append(t.toJson(hex));
				sb.append(',');
			}
			clearScanning();
			sb.deleteCharAt(sb.length() - 1);
			sb.append(']');
			return sb.toString();
		} else {
			return "[]";
		}
	}

	public Base setPm(EmPushMod pm) {
		this.pm = pm;
		return this;
	}

	public Base setHex(boolean hex) {
		this.hex = hex;
		return this;
	}

	protected EmPushMod getPm() {
		return pm;
	}

	public boolean isHex() {
		return hex;
	}

	// 获取标签集
	public Map<String, T6C> getScanning () {
		return ts;
	}

	// 清空标签集
	public synchronized void clearScanning() {
		ts.clear();
	}

	// 添加标签
	private synchronized void appendReadTag (T6C bt) {
		ts.put(bt.getTidHexstr(), bt);
	}

	// 回调
	protected void cb (EmCb e, String... args) {
		if (itl != null) {
			itl.cb(e, args);
		}
	}

	// 读到标签时的触发事件
	protected void onReadTag (T6C bt) {
		if (pm != EmPushMod.Event) {
			appendReadTag(bt);
		}
		if (pm != EmPushMod.Catch && itl != null) {
			itl.onReadTag(bt, itl);
		}
	}

	// 写完标签时的触发事件
	protected void onWrtTag (T6C bt) {
		if (itl != null) {
			itl.onWrtTag(bt, itl);
		}
	}
}

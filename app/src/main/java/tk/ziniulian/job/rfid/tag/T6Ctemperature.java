package tk.ziniulian.job.rfid.tag;

import invengo.javaapi.protocol.IRP1.RXD_TagData;

/**
 * 温度标签
 * Created by 李泽荣 on 2018/10/31.
 */

public class T6Ctemperature extends T6C {
	private double tmp = 0;		// 温度

	public double getTmp() {
		return tmp;
	}

	public T6Ctemperature setTmp(double tmp) {
		this.tmp = tmp;
		return this;
	}

	@Override
	protected void hdJson(StringBuilder sb, boolean isHex) {
		super.hdJson(sb, isHex);
		sb.append("\"tmp\":");
		sb.append(tmp);
		sb.append(',');
	}

	@Override
	public void setByRXD_TagData_ReceivedInfo(RXD_TagData.ReceivedInfo ri) {
		setEpc(ri.getEPC());
		setTid(ri.getTID());
		setTmp(ri.getTemperature());
	}
}
